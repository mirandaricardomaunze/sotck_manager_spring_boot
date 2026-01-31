package com.stock.stockmanager.service;

import com.stock.stockmanager.model.Transfer;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.time.ZoneId;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Service
public class TransferJasperReport {

    private JasperReport compiledReport;
    private boolean reportCompiled = false;

    // ===================== INIT =====================
    private synchronized void compileReport() {
        if (reportCompiled) return;

        try (InputStream reportStream =
                     new ClassPathResource("reports/transfer.jrxml").getInputStream()) {

            this.compiledReport = JasperCompileManager.compileReport(reportStream);
            this.reportCompiled = true;

            System.out.println("✅ Relatório de Transferência compilado com sucesso");

        } catch (Exception e) {
            throw new RuntimeException("Erro ao compilar relatório de transferência", e);
        }
    }

    private void ensureCompiled() {
        if (!reportCompiled) {
            compileReport();
        }
    }

    // ===================== JASPER PRINT =====================
    public JasperPrint generate(Transfer transfer) {
        try {
            ensureCompiled();

            Map<String, Object> params = buildParameters(transfer);

            JRBeanCollectionDataSource dataSource =
                    new JRBeanCollectionDataSource(
                            Collections.singletonList(transfer)
                    );

            return JasperFillManager.fillReport(
                    compiledReport,
                    params,
                    dataSource
            );

        } catch (JRException e) {
            throw new RuntimeException("Erro ao gerar JasperPrint da transferência", e);
        }
    }

    // ===================== PARAMETERS =====================
    private Map<String, Object> buildParameters(Transfer transfer) {
        Map<String, Object> params = new HashMap<>();

        params.put("companyName", safe(String.valueOf(transfer.getCompany()), "Empresa"));
        params.put("reference", safe(transfer.getReference(), "N/A"));
        params.put("userName", safe(String.valueOf(transfer.getUser()), "Sistema"));

        params.put("sourceWarehouse", safe(
                String.valueOf(transfer.getSourceWarehouse()), "Armazém Origem"));

        params.put("destinationWarehouse", safe(
                String.valueOf(transfer.getDestinationWarehouse()), "Armazém Destino"));

        params.put("transferDate",
                Date.from(
                        transfer.getTransferDate()
                                .atZone(ZoneId.systemDefault())
                                .toInstant()
                )
        );

        return params;
    }

    private String safe(String value, String defaultValue) {
        return value != null && !value.isBlank() ? value : defaultValue;
    }

    // ===================== EXPORT =====================
    public byte[] exportPdf(Transfer transfer) {
        try {
            JasperPrint jasperPrint = generate(transfer);
            return JasperExportManager.exportReportToPdf(jasperPrint);
        } catch (JRException e) {
            throw new RuntimeException("Erro ao exportar PDF da transferência", e);
        }
    }
}
