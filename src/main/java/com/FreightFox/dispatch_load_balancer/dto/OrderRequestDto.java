
package com.FreightFox.dispatch_load_balancer.dto;

import com.FreightFox.dispatch_load_balancer.enums.Priority;
import jakarta.validation.constraints.*;

public class OrderRequestDto {

    @NotBlank(message = "Order ID is required")
    private String orderId;

    @DecimalMin(value = "-90.0")
    @DecimalMax(value = "90.0")
    private double latitude;

    @DecimalMin(value = "-180.0")
    @DecimalMax(value = "180.0")
    private double longitude;

    @NotBlank(message = "Address is required")
    private String address;

    @DecimalMin(value = "0.01",
            message = "Package weight must be greater than zero")
    private double packageWeight;

    @NotNull(message = "Priority is required")
    private Priority priority;

    public OrderRequestDto() {
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public double getPackageWeight() {
        return packageWeight;
    }

    public void setPackageWeight(double packageWeight) {
        this.packageWeight = packageWeight;
    }

    public Priority getPriority() {
        return priority;
    }

    public void setPriority(Priority priority) {
        this.priority = priority;
    }
}
