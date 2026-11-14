
package com.FreightFox.dispatch_load_balancer.controller;

import com.FreightFox.dispatch_load_balancer.dto.ApiResponse;
import com.FreightFox.dispatch_load_balancer.dto.DispatchPlanResponse;
import com.FreightFox.dispatch_load_balancer.dto.OrderRequest;
import com.FreightFox.dispatch_load_balancer.service.DispatchService;
import com.FreightFox.dispatch_load_balancer.dto.VehicleRequest;

import jakarta.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;



@RestController
@RequestMapping("/api/dispatch")
public class DispatchController {

    private final DispatchService dispatchService;

    public DispatchController(DispatchService dispatchService) {
        this.dispatchService = dispatchService;
    }

    @PostMapping("/orders")
    public ResponseEntity<ApiResponse> addOrders(
            @Valid @RequestBody OrderRequest request) {

        dispatchService.saveOrders(request.getOrders());

        ApiResponse response = new ApiResponse(
                "Delivery orders accepted.",
                "success"
        );

        return ResponseEntity.ok(response);
    }

    @PostMapping("/vehicles")
    public ResponseEntity<ApiResponse> addVehicles(
            @Valid @RequestBody VehicleRequest request) {

        dispatchService.saveVehicles(request.getVehicles());

        ApiResponse response = new ApiResponse(
                "Vehicle details accepted.",
                "success"
        );

        return ResponseEntity.ok(response);
    }


    @GetMapping("/plan")
    public ResponseEntity<DispatchPlanResponse> getDispatchPlan() {

        DispatchPlanResponse response =
                dispatchService.generateDispatchPlan();

        return ResponseEntity.ok(response);
    }

}
