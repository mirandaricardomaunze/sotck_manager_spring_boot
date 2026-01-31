package com.stock.stockmanager.controller;

import com.stock.stockmanager.dto.OrderDTO;
import com.stock.stockmanager.service.OrderReportService;
import com.stock.stockmanager.service.OrderService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/orders")
public class OrderController {

    private final OrderService orderService;
    private final OrderReportService reportService;

    public OrderController(OrderService orderService, OrderReportService reportService) {
        this.orderService = orderService;
        this.reportService = reportService;
    }

    // ------------------------- CRUD BÁSICO ----------------------------

    @PostMapping
    public ResponseEntity<OrderDTO> createOrder(@RequestBody OrderDTO dto) {
        return ResponseEntity.ok(orderService.createOrder(dto));
    }

    @GetMapping
    public ResponseEntity<List<OrderDTO>> getAllOrders() {
        return ResponseEntity.ok(orderService.getAllOrders());
    }

    @GetMapping("/company/{companyId}")
    public ResponseEntity<List<OrderDTO>> getOrdersByCompany(@PathVariable Long companyId) {
        return ResponseEntity.ok(orderService.getOrdersByCompany(companyId));
    }

    @GetMapping("/warehouse/{warehouseId}")
    public ResponseEntity<List<OrderDTO>> getOrdersByWarehouse(@PathVariable Long warehouseId) {
        return ResponseEntity.ok(orderService.getOrdersByWarehouse(warehouseId));
    }

    @PutMapping("/{id}")
    public ResponseEntity<OrderDTO> updateOrder(@PathVariable Long id, @RequestBody OrderDTO dto) {
        return ResponseEntity.ok(orderService.updateOrder(id, dto));
    }

    /**
     * Endpoint para ANULAR pedido (cancelar e liberar estoque reservado).
     */
    @PutMapping("/{id}/cancel")
    public ResponseEntity<Void> cancelOrder(@PathVariable Long id) {
        orderService.cancelOrder(id);
        return ResponseEntity.noContent().build();
    }

    // ---------------------- EXPORTAÇÕES ----------------------

    @GetMapping(
            value = "/{id}/export/pdf",
            produces = {"application/pdf", "application/json"}
    )
    public ResponseEntity<byte[]> exportOrderPdf(@PathVariable Long id) throws Exception {
        byte[] data = reportService.exportOrderToPdf(id);
        return ResponseEntity.ok()
                .header("Content-Disposition", "inline; filename=encomenda_" + id + ".pdf")
                .body(data);
    }

    @GetMapping(
            value = "/{id}/export/excel",
            produces = {"application/vnd.openxmlformats-officedocument.spreadsheetml.sheet", "application/json"}
    )
    public ResponseEntity<byte[]> exportOrderExcel(@PathVariable Long id) throws Exception {
        byte[] data = reportService.exportOrderToExel(id);
        return ResponseEntity.ok()
                .header("Content-Disposition", "attachment; filename=encomenda_" + id + ".xlsx")
                .body(data);
    }

    @GetMapping(
            value = "/{id}/preview",
            produces = {"application/pdf", "application/json"}
    )
    public ResponseEntity<byte[]> previewInvoice(@PathVariable Long id) throws Exception {
        byte[] data = reportService.exportOrderToPdf(id);
        return ResponseEntity.ok()
                .header("Content-Disposition", "inline; filename=preview_fatura_" + id + ".pdf")
                .body(data);
    }
}
