package com.FreightFox.dispatch_load_balancer.repository;


import com.FreightFox.dispatch_load_balancer.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderRepository extends JpaRepository<Order, String> {

}
