
package com.FreightFox.dispatch_load_balancer.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;

import java.util.List;

public class OrderRequest {

    @NotEmpty(message = "At least one order is required")
    @Valid
    private List<@Valid OrderRequestDto> orders;

    public OrderRequest() {
    }

    public List<OrderRequestDto> getOrders() {
        return orders;
    }

    public void setOrders(List<OrderRequestDto> orders) {
        this.orders = orders;
    }
}
