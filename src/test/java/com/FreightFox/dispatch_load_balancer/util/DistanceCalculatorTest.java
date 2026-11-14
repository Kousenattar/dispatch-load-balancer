
package com.FreightFox.dispatch_load_balancer.util;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class DistanceCalculatorTest {

    @Test
    void shouldReturnZeroForSameCoordinates() {

        double distance = DistanceCalculator.calculateDistance(
                28.6139, 77.2090,
                28.6139, 77.2090
        );

        assertEquals(0.0, distance, 0.0001);
    }

    @Test
    void shouldCalculateDistanceBetweenTwoLocations() {

        double distance = DistanceCalculator.calculateDistance(
                28.7041, 77.1025,
                28.6139, 77.2090
        );

        assertEquals(14.44, distance, 0.1);
    }

    @Test
    void shouldReturnPositiveDistance() {

        double distance = DistanceCalculator.calculateDistance(
                28.7041, 77.1025,
                28.5355, 77.3910
        );

        assertTrue(distance > 0);
    }

    @Test
    void distanceShouldBeSameInBothDirections() {

        double distance1 = DistanceCalculator.calculateDistance(
                28.7041, 77.1025,
                28.6139, 77.2090
        );

        double distance2 = DistanceCalculator.calculateDistance(
                28.6139, 77.2090,
                28.7041, 77.1025
        );

        assertEquals(distance1, distance2, 0.0001);
    }
}
