package com.stock.stockmanager.service;

import com.stock.stockmanager.dto.SaleItemReportDTO;
import com.stock.stockmanager.exception.BusinessException;
import com.stock.stockmanager.mapper.MapperReportItems;
import com.stock.stockmanager.model.Company;
import com.stock.stockmanager.model.Sale;
import com.stock.stockmanager.repository.CompanyRepository;
import com.stock.stockmanager.repository.SaleRepository;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.engine.export.HtmlExporter;
import net.sf.jasperreports.engine.export.ooxml.JRXlsxExporter;
import net.sf.jasperreports.export.SimpleExporterInput;
import net.sf.jasperreports.export.SimpleHtmlExporterOutput;
import net.sf.jasperreports.export.SimpleOutputStreamExporterOutput;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class SaleJasperReportService {

    private static final Logger log = LoggerFactory.getLogger(SaleJasperReportService.class);

    private final SaleRepository saleRepository;
    private final CompanyRepository companyRepository;

    private JasperReport compiledReport;
    private boolean reportCompiled = false;
    private boolean lastCompiledThermal = false;

    public SaleJasperReportService(SaleRepository saleRepository, CompanyRepository companyRepository) {
        this.saleRepository = saleRepository;
        this.companyRepository = companyRepository;
    }

    // ================= COMPILAÇÃO DO RELATÓRIO =================
    private synchronized void compileReport(boolean thermal) {
        String path = thermal ? "reports/thermal_sale.jrxml" : "reports/sale.jrxml";
        log.info("[JASPER] Iniciando compilação. Thermal={}", thermal);

        try {
            InputStream reportStream = getClass().getClassLoader().getResourceAsStream(path);
            if (reportStream == null) {
                ClassPathResource resource = new ClassPathResource(path);
                if (resource.exists()) {
                    reportStream = resource.getInputStream();
                }
            }
            if (reportStream == null) throw new BusinessException("Arquivo de relatório não encontrado: " + path);

            compiledReport = JasperCompileManager.compileReport(reportStream);
            reportCompiled = true;
            lastCompiledThermal = thermal;

        } catch (Exception e) {
            reportCompiled = false;
            throw new BusinessException("Erro ao compilar relatório de venda: " + e.getMessage());
        }
    }

    private synchronized void ensureReportCompiled(boolean thermal) {
        if (!reportCompiled || lastCompiledThermal != thermal) compileReport(thermal);
    }

    // ================= GERAÇÃO DO JASPER PRINT =================
    private JasperPrint generateJasperPrint(Long saleId, boolean thermal) {
        ensureReportCompiled(thermal);

        Sale sale = saleRepository.findById(saleId)
                .orElseThrow(() -> new BusinessException("Venda não encontrada com id: " + saleId));

        // Pega os dados da empresa (assumindo apenas 1 registro)
        Company company = companyRepository.findAll().stream().findFirst()
                .orElseThrow(() -> new BusinessException("Dados da empresa não encontrados"));

        Map<String, Object> parameters = new HashMap<>();
        parameters.put("SALE_NUMBER", sale.getId());
        parameters.put("SALE_DATE", Date.from(sale.getSaleDate().atZone(ZoneId.systemDefault()).toInstant()));
        parameters.put("SALE_TIME", sale.getSaleDate().format(DateTimeFormatter.ofPattern("HH:mm:ss")));
        parameters.put("CUSTOMER_NAME", sale.getClientName());
        parameters.put("USER_NAME",
                sale.getUserName() != null ? sale.getUserName().getUsername() : "Mirasoft");
        parameters.put("COMPANY_NAME", company.getName());
        parameters.put("COMPANY_PHONE", company.getContactPhone());
        parameters.put("COMPANY_ADDRESS", company.getAddress());
        parameters.put("COMPANY_NUIT", company.getTaxId());


        // Mensagem de agradecimento
        parameters.put("THANK_YOU_MSG", "Obrigado pela sua compra!");

        List<SaleItemReportDTO> reportItems = MapperReportItems.map(sale.getItems());
        JRBeanCollectionDataSource dataSource = new JRBeanCollectionDataSource(reportItems);

        try {
            return JasperFillManager.fillReport(compiledReport, parameters, dataSource);
        } catch (JRException e) {
            throw new BusinessException("Erro ao gerar relatório de venda: " + e.getMessage());
        }
    }

    // ================= EXPORTAÇÕES =================
    public byte[] exportSaleToPdf(Long saleId, boolean thermal) {
        try {
            JasperPrint jasperPrint = generateJasperPrint(saleId, thermal);
            return JasperExportManager.exportReportToPdf(jasperPrint);
        } catch (JRException e) {
            throw new BusinessException("Erro ao exportar PDF da venda: " + e.getMessage());
        }
    }

    public byte[] exportSaleToHtml(Long saleId, boolean thermal) {
        try {
            JasperPrint jasperPrint = generateJasperPrint(saleId, thermal);
            HtmlExporter exporter = new HtmlExporter();
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            exporter.setExporterInput(new SimpleExporterInput(jasperPrint));
            exporter.setExporterOutput(new SimpleHtmlExporterOutput(outputStream));
            exporter.exportReport();
            return outputStream.toByteArray();
        } catch (Exception e) {
            throw new BusinessException("Erro ao exportar HTML da venda: " + e.getMessage());
        }
    }

    public byte[] exportSaleToExcel(Long saleId, boolean thermal) {
        try {
            JasperPrint jasperPrint = generateJasperPrint(saleId, thermal);
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            JRXlsxExporter exporter = new JRXlsxExporter();
            exporter.setExporterInput(new SimpleExporterInput(jasperPrint));
            exporter.setExporterOutput(new SimpleOutputStreamExporterOutput(outputStream));
            exporter.exportReport();
            return outputStream.toByteArray();
        } catch (Exception e) {
            throw new BusinessException("Erro ao exportar Excel da venda: " + e.getMessage());
        }
    }

    public boolean isReportCompiled() { return reportCompiled; }
    public synchronized void recompileReport() { this.reportCompiled = false; }
}
