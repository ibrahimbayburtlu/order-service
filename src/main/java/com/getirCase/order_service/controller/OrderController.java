package com.getirCase.order_service.controller;

import com.getirCase.order_service.constants.ApiEndpoints;
import com.getirCase.order_service.model.request.OrderRequest;
import com.getirCase.order_service.model.response.OrderResponse;
import com.getirCase.order_service.service.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(ApiEndpoints.ORDER_BASE)
@RequiredArgsConstructor
public class OrderController {

    private static final Logger logger = LoggerFactory.getLogger(OrderController.class);
    private final OrderService orderService;

    /**
     * Retrieve an order by ID.
     */
    @GetMapping(ApiEndpoints.GET_ORDER)
    @Operation(summary = "Retrieve an order by ID", description = "Fetches order details using the provided ID.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Order found successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid order ID"),
            @ApiResponse(responseCode = "404", description = "Order not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<OrderResponse> getOrder(
            @Parameter(description = "ID of the order to retrieve", required = true)
            @PathVariable Long id) {

        logger.info("Retrieving order with ID: {}", id);
        OrderResponse orderResponse = orderService.getOrder(id);
        return ResponseEntity.ok(orderResponse);
    }

    /**
     * Create a new order.
     */
    @PostMapping(ApiEndpoints.CREATE_ORDER)
    @Operation(summary = "Create a new order", description = "Creates a new order for a customer.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Order created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request data"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<OrderResponse> createOrder(
            @Valid @RequestBody OrderRequest orderRequest) {

        logger.info("Creating order for customer ID: {}", orderRequest.getCustomerId());
        OrderResponse createdOrder = orderService.createOrder(orderRequest);
        return ResponseEntity.ok(createdOrder);
    }

    /**
     * Update an existing order.
     */
    @PutMapping(ApiEndpoints.UPDATE_ORDER)
    @Operation(summary = "Update an order", description = "Updates an existing order details.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Order updated successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request data"),
            @ApiResponse(responseCode = "404", description = "Order not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<OrderResponse> updateOrder(
            @PathVariable Long id,
            @Valid @RequestBody OrderRequest orderRequest) {

        logger.info("Updating order with ID: {}", id);
        OrderResponse updatedOrder = orderService.updateOrder(id, orderRequest);
        return ResponseEntity.ok(updatedOrder);
    }

    /**
     * Cancel an order.
     */
    @PatchMapping(ApiEndpoints.CANCEL_ORDER)
    @Operation(summary = "Cancel an order", description = "Cancels an order by updating its status.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Order cancelled successfully"),
            @ApiResponse(responseCode = "404", description = "Order not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<String> cancelOrder(
            @PathVariable Long id) {

        logger.info("Cancelling order with ID: {}", id);
        orderService.cancelOrder(id);
        return ResponseEntity.ok("Order cancelled successfully");
    }

    /**
     * Delete an order permanently.
     */
    @DeleteMapping(ApiEndpoints.DELETE_ORDER)
    @Operation(summary = "Delete an order", description = "Deletes an order permanently from the system.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Order deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Order not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<String> deleteOrder(
            @PathVariable Long id) {

        logger.info("Deleting order with ID: {}", id);
        orderService.deleteOrder(id);
        return ResponseEntity.ok("Order deleted successfully");
    }

}
