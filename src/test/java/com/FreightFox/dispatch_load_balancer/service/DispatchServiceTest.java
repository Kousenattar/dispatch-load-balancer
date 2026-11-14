
package com.FreightFox.dispatch_load_balancer.service;

import com.FreightFox.dispatch_load_balancer.entity.Order;
import com.FreightFox.dispatch_load_balancer.entity.Vehicle;
import com.FreightFox.dispatch_load_balancer.enums.Priority;
import com.FreightFox.dispatch_load_balancer.repository.OrderRepository;
import com.FreightFox.dispatch_load_balancer.repository.VehicleRepository;
import com.FreightFox.dispatch_load_balancer.dto.DispatchPlanResponse;
import com.FreightFox.dispatch_load_balancer.dto.VehiclePlanDto;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class DispatchServiceTest {

    @Test
    void shouldAssignHighPriorityOrderFirst() {

        // 1. Mock repositories
        OrderRepository orderRepository = mock(OrderRepository.class);
        VehicleRepository vehicleRepository = mock(VehicleRepository.class);

        // 2. Create vehicle with capacity 20 kg
        Vehicle vehicle = new Vehicle();
        vehicle.setVehicleId("VEH001");
        vehicle.setCapacity(20);
        vehicle.setCurrentLatitude(28.6139);
        vehicle.setCurrentLongitude(77.2090);
        vehicle.setCurrentAddress("Delhi");

        // 3. Create HIGH priority order (15 kg)
        Order highOrder = new Order();
        highOrder.setOrderId("ORD001");
        highOrder.setLatitude(28.6139);
        highOrder.setLongitude(77.2090);
        highOrder.setAddress("Delhi High Priority");
        highOrder.setPackageWeight(15);
        highOrder.setPriority(Priority.HIGH);

        // 4. Create LOW priority order (10 kg)
        Order lowOrder = new Order();
        lowOrder.setOrderId("ORD002");
        lowOrder.setLatitude(28.6139);
        lowOrder.setLongitude(77.2090);
        lowOrder.setAddress("Delhi Low Priority");
        lowOrder.setPackageWeight(10);
        lowOrder.setPriority(Priority.LOW);

        // 5. Provide test data to mocked repositories
        when(orderRepository.findAll())
                .thenReturn(new ArrayList<>(List.of(highOrder, lowOrder)));

        when(vehicleRepository.findAll())
                .thenReturn(List.of(vehicle));

        // 6. Create service
        DispatchService dispatchService =
                new DispatchService(orderRepository, vehicleRepository);

        // 7. Generate dispatch plan
        DispatchPlanResponse response =
                dispatchService.generateDispatchPlan();

        // 8. Verify results
        assertEquals(1, response.getDispatchPlan().size());

        VehiclePlanDto vehiclePlan =
                response.getDispatchPlan().get(0);

        assertEquals("VEH001", vehiclePlan.getVehicleId());
        assertEquals(15.0, vehiclePlan.getTotalLoad(), 0.001);

        assertEquals(1, vehiclePlan.getAssignedOrders().size());
        assertEquals(
                "ORD001",
                vehiclePlan.getAssignedOrders().get(0).getOrderId()
        );

        assertEquals(1, response.getUnassignedOrders().size());
        assertEquals(
                "ORD002",
                response.getUnassignedOrders().get(0).getOrderId()
        );
    }


    @Test
    void shouldNotAssignOrderWhenCapacityIsInsufficient() {

        OrderRepository orderRepository = mock(OrderRepository.class);
        VehicleRepository vehicleRepository = mock(VehicleRepository.class);

        Vehicle vehicle = new Vehicle();
        vehicle.setVehicleId("VEH001");
        vehicle.setCapacity(10);
        vehicle.setCurrentLatitude(28.6139);
        vehicle.setCurrentLongitude(77.2090);
        vehicle.setCurrentAddress("Delhi");

        Order order = new Order();
        order.setOrderId("ORD001");
        order.setLatitude(28.6139);
        order.setLongitude(77.2090);
        order.setAddress("Delhi");
        order.setPackageWeight(20);
        order.setPriority(Priority.HIGH);

        when(orderRepository.findAll())
                .thenReturn(new ArrayList<>(List.of(order)));

        when(vehicleRepository.findAll())
                .thenReturn(List.of(vehicle));

        DispatchService service =
                new DispatchService(orderRepository, vehicleRepository);

        DispatchPlanResponse response = service.generateDispatchPlan();

        assertEquals(0, response.getDispatchPlan().get(0)
                .getAssignedOrders().size());

        assertEquals(1, response.getUnassignedOrders().size());
        assertEquals("ORD001",
                response.getUnassignedOrders().get(0).getOrderId());
    }


    @Test
    void shouldKeepOrdersUnassignedWhenNoVehiclesAvailable() {

        OrderRepository orderRepository = mock(OrderRepository.class);
        VehicleRepository vehicleRepository = mock(VehicleRepository.class);

        Order order = new Order();
        order.setOrderId("ORD001");
        order.setLatitude(28.6139);
        order.setLongitude(77.2090);
        order.setAddress("Delhi");
        order.setPackageWeight(10);
        order.setPriority(Priority.HIGH);

        when(orderRepository.findAll())
                .thenReturn(new ArrayList<>(List.of(order)));

        when(vehicleRepository.findAll())
                .thenReturn(List.of());

        DispatchService service =
                new DispatchService(orderRepository, vehicleRepository);

        DispatchPlanResponse response = service.generateDispatchPlan();

        assertEquals(0, response.getDispatchPlan().size());
        assertEquals(1, response.getUnassignedOrders().size());
    }


    @Test
    void shouldReturnEmptyAssignmentsWhenNoOrdersAvailable() {

        OrderRepository orderRepository = mock(OrderRepository.class);
        VehicleRepository vehicleRepository = mock(VehicleRepository.class);

        Vehicle vehicle = new Vehicle();
        vehicle.setVehicleId("VEH001");
        vehicle.setCapacity(100);
        vehicle.setCurrentLatitude(28.6139);
        vehicle.setCurrentLongitude(77.2090);
        vehicle.setCurrentAddress("Delhi");

        when(orderRepository.findAll())
                .thenReturn(new ArrayList<>());

        when(vehicleRepository.findAll())
                .thenReturn(List.of(vehicle));

        DispatchService service =
                new DispatchService(orderRepository, vehicleRepository);

        DispatchPlanResponse response = service.generateDispatchPlan();

        assertEquals(1, response.getDispatchPlan().size());

        assertEquals(0, response.getDispatchPlan().get(0)
                .getAssignedOrders().size());

        assertEquals(0.0,
                response.getDispatchPlan().get(0).getTotalLoad(), 0.001);

        assertEquals(0, response.getUnassignedOrders().size());
    }


}
