
package com.FreightFox.dispatch_load_balancer.service;

import com.FreightFox.dispatch_load_balancer.dto.*;
import com.FreightFox.dispatch_load_balancer.entity.Order;
import com.FreightFox.dispatch_load_balancer.entity.Vehicle;
import com.FreightFox.dispatch_load_balancer.repository.OrderRepository;

import com.FreightFox.dispatch_load_balancer.repository.VehicleRepository;
import com.FreightFox.dispatch_load_balancer.util.DistanceCalculator;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
public class DispatchService {

    private final OrderRepository orderRepository;
    private final VehicleRepository vehicleRepository;

    public DispatchService(
            OrderRepository orderRepository,
            VehicleRepository vehicleRepository) {

        this.orderRepository = orderRepository;
        this.vehicleRepository = vehicleRepository;
    }

    @Transactional
    public void saveOrders(List<OrderRequestDto> orderDtos) {

        Set<String> orderIds = new HashSet<>();

        List<Order> orders = orderDtos.stream()
                .map(dto -> {

                    if (!orderIds.add(dto.getOrderId())) {
                        throw new IllegalArgumentException(
                                "Duplicate order ID: " + dto.getOrderId()
                        );
                    }

                    Order order = new Order();

                    order.setOrderId(dto.getOrderId());
                    order.setLatitude(dto.getLatitude());
                    order.setLongitude(dto.getLongitude());
                    order.setAddress(dto.getAddress());
                    order.setPackageWeight(dto.getPackageWeight());
                    order.setPriority(dto.getPriority());

                    return order;
                })
                .toList();

        orderRepository.saveAll(orders);
    }


    @Transactional
    public void saveVehicles(List<VehicleRequestDto> vehicleDtos) {

        Set<String> vehicleIds = new HashSet<>();

        List<Vehicle> vehicles = vehicleDtos.stream()
                .map(dto -> {

                    if (!vehicleIds.add(dto.getVehicleId())) {
                        throw new IllegalArgumentException(
                                "Duplicate vehicle ID: " + dto.getVehicleId()
                        );
                    }

                    Vehicle vehicle = new Vehicle();

                    vehicle.setVehicleId(dto.getVehicleId());
                    vehicle.setCapacity(dto.getCapacity());
                    vehicle.setCurrentLatitude(dto.getCurrentLatitude());
                    vehicle.setCurrentLongitude(dto.getCurrentLongitude());
                    vehicle.setCurrentAddress(dto.getCurrentAddress());

                    return vehicle;
                })
                .toList();

        vehicleRepository.saveAll(vehicles);
    }


    public DispatchPlanResponse generateDispatchPlan() {

        // 1. Fetch orders and vehicles from PostgreSQL
        List<Order> orders = orderRepository.findAll();
        List<Vehicle> vehicles = vehicleRepository.findAll();

        System.out.println("orders" + orders.size());
        System.out.println("vehicles" + vehicles.size());


        // 2. Sort orders: HIGH -> MEDIUM -> LOW
        orders.sort(
                Comparator.comparingInt(order -> {
                    return switch (order.getPriority()) {
                        case HIGH -> 1;
                        case MEDIUM -> 2;
                        case LOW -> 3;
                    };
                })
        );

        // 3. Create a working state for every vehicle
        List<VehicleState> vehicleStates = new ArrayList<>();

        for (Vehicle vehicle : vehicles) {
            vehicleStates.add(new VehicleState(vehicle));
        }

        List<OrderPlanDto> unassignedOrders = new ArrayList<>();

        // 4. Process orders one by one
        for (Order order : orders) {

            VehicleState bestVehicle = null;
            double shortestDistance = Double.MAX_VALUE;

            // 5. Find the nearest vehicle with enough capacity
            for (VehicleState state : vehicleStates) {

                double remainingCapacity =
                        state.vehicle.getCapacity() - state.totalLoad;

                if (remainingCapacity < order.getPackageWeight()) {
                    continue;
                }

                double distance = DistanceCalculator.calculateDistance(
                        state.currentLatitude,
                        state.currentLongitude,
                        order.getLatitude(),
                        order.getLongitude()
                );

                if (distance < shortestDistance) {
                    shortestDistance = distance;
                    bestVehicle = state;
                }
            }

            // 6. If no vehicle can carry this order
            if (bestVehicle == null) {
                unassignedOrders.add(toOrderPlanDto(order));
                continue;
            }

            // 7. Assign order to selected vehicle
            bestVehicle.assignedOrders.add(toOrderPlanDto(order));

            bestVehicle.totalLoad += order.getPackageWeight();

            bestVehicle.totalDistance += shortestDistance;

            // 8. Update vehicle's current location
            bestVehicle.currentLatitude = order.getLatitude();
            bestVehicle.currentLongitude = order.getLongitude();
        }

        // 9. Build final response
        List<VehiclePlanDto> dispatchPlan = new ArrayList<>();

        for (VehicleState state : vehicleStates) {

            VehiclePlanDto plan = new VehiclePlanDto();

            plan.setVehicleId(state.vehicle.getVehicleId());
            plan.setTotalLoad(state.totalLoad);

            plan.setTotalDistance(
                    String.format(
                            Locale.US,
                            "%.2f km",
                            state.totalDistance
                    )
            );

            plan.setAssignedOrders(state.assignedOrders);

            dispatchPlan.add(plan);
        }

        System.out.println("========== DISPATCH RESULT ==========");

        System.out.println("Total vehicles: " + dispatchPlan.size());
        System.out.println("Unassigned orders: " + unassignedOrders.size());

        for (VehiclePlanDto plan : dispatchPlan) {
            System.out.println("Vehicle: " + plan.getVehicleId());
            System.out.println("Load: " + plan.getTotalLoad());
            System.out.println("Distance: " + plan.getTotalDistance());
            System.out.println("Assigned orders: " + plan.getAssignedOrders().size());
        }

        System.out.println("=====================================");

        return new DispatchPlanResponse(
                dispatchPlan,
                unassignedOrders
        );
    }


    // Converts the database Order entity into a response DTO
    private OrderPlanDto toOrderPlanDto(Order order) {

        OrderPlanDto dto = new OrderPlanDto();

        dto.setOrderId(order.getOrderId());
        dto.setLatitude(order.getLatitude());
        dto.setLongitude(order.getLongitude());
        dto.setAddress(order.getAddress());
        dto.setPackageWeight(order.getPackageWeight());
        dto.setPriority(order.getPriority());

        return dto;
    }


    // Keeps track of each vehicle while creating the plan
    private static class VehicleState {

        private final Vehicle vehicle;

        private double totalLoad = 0.0;
        private double totalDistance = 0.0;

        private double currentLatitude;
        private double currentLongitude;

        private final List<OrderPlanDto> assignedOrders =
                new ArrayList<>();

        private VehicleState(Vehicle vehicle) {

            this.vehicle = vehicle;

            this.currentLatitude = vehicle.getCurrentLatitude();
            this.currentLongitude = vehicle.getCurrentLongitude();
        }
    }
}
