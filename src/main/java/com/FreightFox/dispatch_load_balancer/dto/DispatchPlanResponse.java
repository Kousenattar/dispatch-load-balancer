
package com.FreightFox.dispatch_load_balancer.dto;

import java.util.List;

public class DispatchPlanResponse {

    private List<VehiclePlanDto> dispatchPlan;
    private List<OrderPlanDto> unassignedOrders;

    public DispatchPlanResponse() {
    }

    public DispatchPlanResponse(
            List<VehiclePlanDto> dispatchPlan,
            List<OrderPlanDto> unassignedOrders) {

        this.dispatchPlan = dispatchPlan;
        this.unassignedOrders = unassignedOrders;
    }

    // Generate getters and setters using Alt + Insert

    public List<VehiclePlanDto> getDispatchPlan() {
        return dispatchPlan;
    }

    public void setDispatchPlan(List<VehiclePlanDto> dispatchPlan) {
        this.dispatchPlan = dispatchPlan;
    }

    public List<OrderPlanDto> getUnassignedOrders() {
        return unassignedOrders;
    }

    public void setUnassignedOrders(List<OrderPlanDto> unassignedOrders) {
        this.unassignedOrders = unassignedOrders;
    }
}
