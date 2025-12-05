package com.stock.stockmanager.service;

import com.lowagie.text.pdf.codec.Base64;
import com.stock.stockmanager.enums.OrderStatus;
import com.stock.stockmanager.exception.ResourceNotFoundException;
import com.stock.stockmanager.model.*;
import com.stock.stockmanager.repository.CompanyRepository;
import com.stock.stockmanager.repository.OrderRepository;
import lombok.Getter;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.engine.export.ooxml.JRXlsxExporter;
import net.sf.jasperreports.export.*;
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
public class OrderReportService {
    private final OrderRepository orderRepository;
    private final CompanyRepository companyRepository;
    @Getter
    private boolean reportCompiled = false;
    private JasperReport compiledReport;

    public OrderReportService (
            OrderRepository orderRepository,
            CompanyRepository companyRepository){
        this.orderRepository=orderRepository;
        this.companyRepository=companyRepository;
        initializeReport();
    }

    private synchronized void initializeReport(){
        if (!reportCompiled) {
            compileReport();
        }
    }

    private void compileReport(){
        InputStream reportStream=null;
        try{
            String [] possiblePaths={
                    "reports/order.jrxml",
                    " reports/order.jrxml",
                    "reports/order.jrxml"
            };
            for(String path: possiblePaths){
                reportStream=getClass().getClassLoader().getResourceAsStream(path);
                if (reportStream!=null){
                    System.out.println("✅ Arquivo encontrado: " + path);
                    break;
                }
            }

            if (reportStream == null) {
                // Tentar ClassPathResource como fallback
                ClassPathResource resource = new ClassPathResource("reports/order.jrxml");
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
            System.out.println("✅ Relatório de encomenda profissional compilado com sucesso!");

        } catch (JRException e) {
            System.err.println("❌ ERRO na compilação do relatório: " + e.getMessage());
            e.printStackTrace();
            this.reportCompiled = false;
            throw new RuntimeException("Falha ao compilar template da encomenda.", e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
         if (reportStream!=null){
             try {
                  reportStream.close();
             } catch (Exception e) {
                 System.err.println("Erro ao fechar stream: " + e.getMessage());
                 throw new IllegalStateException(e);
             }
         }
        }

    }

    private  synchronized void ensureReportCompiled(){
        if (!reportCompiled)
            compileReport();
    }

    private JasperPrint generateJasperPrint(Long orderId){
        try {
            ensureReportCompiled();
            Order order =orderRepository.findById(orderId)
                    .orElseThrow(()->new ResourceNotFoundException("Encomenda  não encontrada com id: "+orderId ));
            if (order.getItems()!=null){
                order.getItems().forEach(this::sanitizeOrder);
            }
            Company company =getCompanyInfo(order);
            Map<String,Object>parameters=buildProfessionalParameters(order,company);
            JRBeanCollectionDataSource dataSource=new JRBeanCollectionDataSource(
                    order.getItems()!= null ? order.getItems() : Collections.emptyList()
            );
            JasperPrint jasperPrint = JasperFillManager.fillReport(compiledReport, parameters, dataSource);
            System.out.println("✅ JasperPrint gerado com sucesso");
            return jasperPrint;

        } catch (JRException e) {
            throw new RuntimeException(e);
        }

    };

    private Company getCompanyInfo( Order order){
         int companyIndex=0;
        if (companyRepository.count()>companyIndex){
            return companyRepository.findAll().get(companyIndex);
        }
        return null;
    }

    private void sanitizeOrder(OrderItem item) {
        if (item == null) return;

        // Garantir que os campos obrigatórios não são nulos
        if (item.getProductName() == null) {
            item.setProductName("Produto não especificado!");
        }

        if (item.getQuantity() == null || item.getQuantity() < 0) {
            item.setQuantity(0);
        }

        if (item.getUnitPrice() == null || item.getUnitPrice().compareTo(BigDecimal.ZERO) < 0) {
            item.setUnitPrice(BigDecimal.ZERO);
        }

        if (item.getTotalPrice() == null) {
            item.setTotalPrice(item.getUnitPrice().multiply(BigDecimal.valueOf(item.getQuantity())));
        }
    }


    private Map<String, Object> buildProfessionalParameters(Order order, Company company) {
        Map<String, Object> parameters = new HashMap<>();

        // Informações da Empresa
        parameters.put("companyName", getSafeValue(company.getName(), "StockManager Pro"));
        parameters.put("companyAddress", getSafeValue(company.getAddress(), "Maputo, Moçambique"));
        parameters.put("warehouseName", getSafeValue(order.getWarehouseName(), "Armazém Principal"));

        // Informações do Cliente
        parameters.put("customerName", getSafeValue(order.getCustomerName(), "Cliente"));
        parameters.put("deliveryAddress", getSafeValue(order.getDeliveryAddress(), "Endereço não especificado"));
        parameters.put("customerNuit", getSafeValue(order.getCustomerNuit(), "Não disponível"));
        parameters.put("customerContact", getSafeValue(order.getCustomerContact(), "Não disponível"));
        parameters.put("customerEmail", getSafeValue(order.getCustomerEmail(), "Não disponível"));

        // Informações da Fatura
        parameters.put("orderNumber", getSafeValue(order.getOrderNumber(), "N/A"));
        parameters.put("orderDate", convertToDate(order.getOrderDate()));
        parameters.put("orderNumber", order.getOrderNumber() != null ?
                getSafeValue(order.getOrderNumber(), "N/A") : "N/A");
        parameters.put("paymentMethod", getSafeValue(order.getPaymentMethod(), "Não especificado"));
        parameters.put("status", getSafeValue(OrderStatus.PENDING.toString(), "PENDENTE"));
        parameters.put("totalAmount", order.getTotalAmount() != null ? order.getTotalAmount() : BigDecimal.ZERO);
        parameters.put("notes", getSafeValue(order.getNotes(), "Agradecemos a sua preferência!"));

        // Informações de Contacto da Empresa
        parameters.put("companyPhone", getSafeValue(company.getContactPhone(), "+258 84 000 0000"));
        parameters.put("companyEmail", getSafeValue(company.getContactEmail(), "geral@empresa.co.mz"));
        parameters.put("companyNuit", getSafeValue(company.getTaxId(), "000000000"));

        // Configurações do Relatório
        parameters.put("SUBREPORT_DIR", "reports/");

        System.out.println("⚙️ Parâmetros do relatório configurados para: " + order.getOrderNumber());
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
        int miniQuantity=0;
        if (item == null) return;

        // Garantir que os campos obrigatórios não são nulos
        if (item.getProductName() == null) {
            item.setProductName("Produto não especificado");
        }

        if (item.getQuantity() == null || item.getQuantity() < miniQuantity) {
            item.setQuantity(miniQuantity);
        }

        if (item.getUnitPrice() == null || item.getUnitPrice().compareTo(BigDecimal.ZERO) <miniQuantity) {
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

    public byte[] exportOrderToPdf(Long orderId){
       try{
           JasperPrint jasperPrint =generateJasperPrint(orderId);
           byte[] pdf = JasperExportManager.exportReportToPdf(jasperPrint);
           return  pdf;

       } catch (JRException e) {
           System.err.println("❌ Falha ao exportar PDF: " + e.getMessage());
           e.printStackTrace();
           throw new RuntimeException("Falha ao exportar PDF da a encomenda: " + e.getMessage(), e);
       }
    };


    public  byte[] exportOrderToExel( Long orderId){
        try {
            JasperPrint jasperPrint=generateJasperPrint(orderId);
            ByteArrayOutputStream outputStream=new ByteArrayOutputStream();

            JRXlsxExporter exporter=new JRXlsxExporter();
            exporter.setExporterInput(new SimpleExporterInput(jasperPrint));
            exporter.setExporterOutput( new SimpleOutputStreamExporterOutput(outputStream));

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
            byte[] excel=outputStream.toByteArray();
            System.out.println("✅ Excel gerado com sucesso - Tamanho: " + excel.length + " bytes");
            return excel;
        } catch (JRException e) {
            System.err.println("Falha ao exportar Excel: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Falha ao exportar Excel da encomenda: " + e.getMessage(), e);
        }
    };

    public String getReportStatus() {
        return reportCompiled ?
                "Relatório compilado e pronto para uso" :
                "Relatório não compilado";
    }
}
