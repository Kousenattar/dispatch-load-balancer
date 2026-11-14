
package com.FreightFox.dispatch_load_balancer.dto;

import java.util.List;

public class VehiclePlanDto {

    private String vehicleId;
    private double totalLoad;
    private String totalDistance;
    private List<OrderPlanDto> assignedOrders;

    public VehiclePlanDto() {
    }

    public String getVehicleId() {
        return vehicleId;
    }

    public void setVehicleId(String vehicleId) {
        this.vehicleId = vehicleId;
    }

    public double getTotalLoad() {
        return totalLoad;
    }

    public void setTotalLoad(double totalLoad) {
        this.totalLoad = totalLoad;
    }

    public String getTotalDistance() {
        return totalDistance;
    }

    public void setTotalDistance(String totalDistance) {
        this.totalDistance = totalDistance;
    }

    public List<OrderPlanDto> getAssignedOrders() {
        return assignedOrders;
    }

    public void setAssignedOrders(List<OrderPlanDto> assignedOrders) {
        this.assignedOrders = assignedOrders;
    }
// Generate getters and setters using Alt + Insert
}
