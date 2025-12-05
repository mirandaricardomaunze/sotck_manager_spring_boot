package com.stock.stockmanager.service;

import com.stock.stockmanager.enums.InvoiceStatus;
import com.stock.stockmanager.exception.ResourceNotFoundException;
import com.stock.stockmanager.model.Company;
import com.stock.stockmanager.model.Invoice;
import com.stock.stockmanager.model.InvoiceItem;
import com.stock.stockmanager.repository.CompanyRepository;
import com.stock.stockmanager.repository.InvoiceRepository;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.engine.export.ooxml.JRXlsxExporter;
import net.sf.jasperreports.export.SimpleExporterInput;
import net.sf.jasperreports.export.SimpleOutputStreamExporterOutput;
import net.sf.jasperreports.export.SimpleXlsxReportConfiguration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Service
public class InvoiceReportService {

    private final InvoiceRepository invoiceRepository;
    private final CompanyRepository companyRepository;
    private JasperReport compiledReport;
    private boolean reportCompiled = false;

    public InvoiceReportService(
            CompanyRepository companyRepository,
            InvoiceRepository invoiceRepository) {
        this.companyRepository = companyRepository;
        this.invoiceRepository = invoiceRepository;
        initializeReport();
    }

    private synchronized void initializeReport() {
        if (!reportCompiled) {
            compileReport();
        }
    }

    private void compileReport() {
        InputStream reportStream = null;
        try {
            System.out.println("=== INICIANDO COMPILAÇÃO DO RELATÓRIO PROFISSIONAL ===");

            // Tentar diferentes métodos para encontrar o arquivo
            String[] possiblePaths = {
                    "reports/professional_invoice.jrxml",  // Novo arquivo profissional
                    "reports/invoice.jrxml",               // Arquivo original
                    "/reports/professional_invoice.jrxml",
                    "/reports/invoice.jrxml"
            };

            for (String path : possiblePaths) {
                reportStream = getClass().getClassLoader().getResourceAsStream(path);
                if (reportStream != null) {
                    System.out.println("✅ Arquivo encontrado: " + path);
                    break;
                }
            }

            if (reportStream == null) {
                // Tentar ClassPathResource como fallback
                ClassPathResource resource = new ClassPathResource("reports/invoice.jrxml");
                if (resource.exists()) {
                    reportStream = resource.getInputStream();
                    System.out.println("✅ Arquivo encontrado via ClassPathResource");
                }
            }

            if (reportStream == null) {
                System.err.println("❌ Nenhum arquivo de relatório encontrado!");
                throw new ResourceNotFoundException("Arquivo do relatório não encontrado. Verifique se o arquivo está em src/main/resources/reports/");
            }

            this.compiledReport = JasperCompileManager.compileReport(reportStream);
            this.reportCompiled = true;
            System.out.println("✅ Relatório profissional compilado com sucesso!");

        } catch (JRException | IOException e) {
            System.err.println("❌ ERRO na compilação do relatório: " + e.getMessage());
            e.printStackTrace();
            this.reportCompiled = false;
            throw new RuntimeException("Falha ao compilar template da fatura.", e);
        } finally {
            if (reportStream != null) {
                try {
                    reportStream.close();
                } catch (Exception e) {
                    System.err.println("Erro ao fechar stream: " + e.getMessage());
                }
            }
        }
    }

    private synchronized void ensureReportCompiled() {
        if (!reportCompiled) {
            compileReport();
        }
    }

    private JasperPrint generateJasperPrint(Long invoiceId) {
        try {
            ensureReportCompiled();

            Invoice invoice = invoiceRepository.findById(invoiceId)
                    .orElseThrow(() -> new RuntimeException("Fatura não encontrada com id: " + invoiceId));

            System.out.println("📋 Processando fatura: " + invoice.getInvoiceNumber() +
                    " | Itens: " + (invoice.getItems() != null ? invoice.getItems().size() : 0) +
                    " | Total: " + invoice.getTotalAmount());

            // Validar e sanitizar itens
            if (invoice.getItems() != null) {
                invoice.getItems().forEach(this::sanitizeInvoiceItem);
            }

            // Buscar informações da empresa
            Company company = getCompanyInfo(invoice);

            // Preparar parâmetros para o relatório profissional
            Map<String, Object> parameters = buildProfessionalParameters(invoice, company);

            // Criar datasource
            JRBeanCollectionDataSource dataSource = new JRBeanCollectionDataSource(
                    invoice.getItems() != null ? invoice.getItems() : Collections.emptyList()
            );

            JasperPrint jasperPrint = JasperFillManager.fillReport(compiledReport, parameters, dataSource);
            System.out.println("✅ JasperPrint gerado com sucesso");
            return jasperPrint;

        } catch (JRException e) {
            throw new RuntimeException("Erro ao gerar relatório Jasper: " + e.getMessage(), e);
        } catch (Exception e) {
            throw new RuntimeException("Erro inesperado ao gerar fatura: " + e.getMessage(), e);
        }
    }

    private Company getCompanyInfo(Invoice invoice) {
        // Buscar empresa do banco de dados
        if (companyRepository.count() > 0) {
            Company company = companyRepository.findAll().get(0);
            System.out.println("🏢 Empresa encontrada: " + company.getName());
            return company;
        }

        // Criar empresa padrão se não existir
        System.out.println("⚠️ Nenhuma empresa encontrada no banco, usando dados padrão");
        return createDefaultCompany(invoice);
    }

    private Company createDefaultCompany(Invoice invoice) {
        Company company = new Company();
        company.setName(getSafeValue(invoice.getCompanyName(), "StockManager Pro"));
        company.setAddress("Av. 24 de Julho, Maputo - Moçambique");
        company.setContactPhone("+258 84 123 4567");
        company.setContactEmail("comercial@stockmanager.co.mz");
        company.setTaxId("123456789101");
        return company;
    }

    private Map<String, Object> buildProfessionalParameters(Invoice invoice, Company company) {
        Map<String, Object> parameters = new HashMap<>();

        // Informações da Empresa
        parameters.put("companyName", getSafeValue(company.getName(), "StockManager Pro"));
        parameters.put("companyAddress", getSafeValue(company.getAddress(), "Maputo, Moçambique"));
        parameters.put("warehouseName", getSafeValue(invoice.getWarehouseName(), "Armazém Principal"));

        // Informações do Cliente
        parameters.put("customerName", getSafeValue(invoice.getCustomerName(), "Cliente"));
        parameters.put("deliveryAddress", getSafeValue(invoice.getDeliveryAddress(), "Endereço não especificado"));
        parameters.put("customerNuit", getSafeValue(invoice.getCustomerNuit(), "Não disponível"));
        parameters.put("customerContact", getSafeValue(invoice.getCustomerContact(), "Não disponível"));
        parameters.put("customerEmail", getSafeValue(invoice.getCustomerEmail(), "Não disponível"));

        // Informações da Fatura
        parameters.put("invoiceNumber", getSafeValue(invoice.getInvoiceNumber(), "N/A"));
        parameters.put("invoiceDate", convertToDate(invoice.getInvoiceDate()));
        parameters.put("orderNumber", invoice.getOrder() != null ?
                getSafeValue(invoice.getOrder().getOrderNumber(), "N/A") : "N/A");
        parameters.put("paymentMethod", getSafeValue(invoice.getPaymentMethod(), "Não especificado"));
        parameters.put("status", getSafeValue(InvoiceStatus.INVOICED.toString(), "FATURADO"));
        parameters.put("totalAmount", invoice.getTotalAmount() != null ? invoice.getTotalAmount() : BigDecimal.ZERO);
        parameters.put("notes", getSafeValue(invoice.getNotes(), "Agradecemos a sua preferência!"));

        // Informações de Contacto da Empresa
        parameters.put("companyPhone", getSafeValue(company.getContactPhone(), "+258 84 000 0000"));
        parameters.put("companyEmail", getSafeValue(company.getContactEmail(), "geral@empresa.co.mz"));
        parameters.put("companyNuit", getSafeValue(company.getTaxId(), "000000000"));

        // Configurações do Relatório
        parameters.put("SUBREPORT_DIR", "reports/");

        System.out.println("⚙️ Parâmetros do relatório configurados para: " + invoice.getInvoiceNumber());
        return parameters;
    }

    private Date convertToDate(LocalDateTime localDateTime) {
        if (localDateTime == null) {
            return new Date();
        }
        return Date.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant());
    }

    private String getSafeValue(String value, String defaultValue) {
        return (value != null && !value.trim().isEmpty()) ? value.trim() : defaultValue;
    }

    private void sanitizeInvoiceItem(InvoiceItem item) {
        if (item == null) return;

        // Garantir que os campos obrigatórios não são nulos
        if (item.getProductName() == null) {
            item.setProductName("Produto não especificado");
        }

        if (item.getQuantity() == null || item.getQuantity() < 0) {
            item.setQuantity(0);
        }

        if (item.getUnitPrice() == null || item.getUnitPrice().compareTo(BigDecimal.ZERO) < 0) {
            item.setUnitPrice(BigDecimal.ZERO);
        }

        // Calcular totalPrice se necessário
        if (item.getTotalPrice() == null) {
            BigDecimal total = BigDecimal.valueOf(item.getQuantity()).multiply(item.getUnitPrice());
            item.setTotalPrice(total);
        }

        // Log para debug
        System.out.println("📦 Item: " + item.getProductName() +
                " | Qtd: " + item.getQuantity() +
                " | Preço: " + item.getUnitPrice() +
                " | Total: " + item.getTotalPrice());
    }

    public byte[] exportInvoiceToPdf(Long invoiceId) {
        try {
            System.out.println("🔄 Iniciando exportação PDF para fatura ID: " + invoiceId);
            JasperPrint jasperPrint = generateJasperPrint(invoiceId);
            byte[] pdf = JasperExportManager.exportReportToPdf(jasperPrint);
            System.out.println("✅ PDF gerado com sucesso - Tamanho: " + pdf.length + " bytes");
            return pdf;
        } catch (Exception e) {
            System.err.println("❌ Falha ao exportar PDF: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Falha ao exportar PDF da fatura: " + e.getMessage(), e);
        }
    }

    public byte[] exportInvoiceToExcel(Long invoiceId) {
        try {
            System.out.println("🔄 Iniciando exportação Excel para fatura ID: " + invoiceId);
            JasperPrint jasperPrint = generateJasperPrint(invoiceId);

            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            JRXlsxExporter exporter = new JRXlsxExporter();

            exporter.setExporterInput(new SimpleExporterInput(jasperPrint));
            exporter.setExporterOutput(new SimpleOutputStreamExporterOutput(outputStream));

            SimpleXlsxReportConfiguration configuration = new SimpleXlsxReportConfiguration();
            configuration.setDetectCellType(true);
            configuration.setCollapseRowSpan(false);
            configuration.setRemoveEmptySpaceBetweenColumns(true);
            configuration.setRemoveEmptySpaceBetweenRows(true);
            configuration.setWhitePageBackground(false);
            configuration.setOnePagePerSheet(false);
            configuration.setIgnoreGraphics(false);

            exporter.setConfiguration(configuration);
            exporter.exportReport();

            byte[] excel = outputStream.toByteArray();
            System.out.println("✅ Excel gerado com sucesso - Tamanho: " + excel.length + " bytes");
            return excel;

        } catch (Exception e) {
            System.err.println("❌ Falha ao exportar Excel: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Falha ao exportar Excel da fatura: " + e.getMessage(), e);
        }
    }

    /**
     * Método para verificar status da compilação
     */
    public boolean isReportCompiled() {
        return reportCompiled;
    }

    /**
     * Método para forçar recompilação do relatório
     */
    public synchronized void recompileReport() {
        System.out.println("🔄 Forçando recompilação do relatório...");
        this.reportCompiled = false;
        compileReport();
    }

    /**
     * Método para obter informações do relatório
     */
    public String getReportStatus() {
        return reportCompiled ?
                "Relatório compilado e pronto para uso" :
                "Relatório não compilado";
    }
}