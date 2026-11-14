
package com.FreightFox.dispatch_load_balancer.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;

import java.util.List;

public class VehicleRequest {

    @NotEmpty(message = "At least one vehicle is required")
    @Valid
    private List<VehicleRequestDto> vehicles;

    public VehicleRequest() {
    }

    public List<VehicleRequestDto> getVehicles() {
        return vehicles;
    }

    public void setVehicles(List<VehicleRequestDto> vehicles) {
        this.vehicles = vehicles;
    }
}
