# Dispatch Load Balancer

A Spring Boot REST API that optimizes delivery order allocation across available vehicles based on vehicle capacity, order priority, and geographical distance.

## Tech Stack

- Java 17
- Spring Boot
- Spring Data JPA
- PostgreSQL
- Maven
- JUnit 5
- Mockito
- Haversine Formula

## Features

- Accept and store delivery orders.
- Register delivery vehicles.
- Allocate orders based on vehicle capacity.
- Process HIGH priority orders before MEDIUM and LOW priority orders.
- Calculate geographical distance using the Haversine formula.
- Generate a dispatch plan for each vehicle.
- Return orders that could not be assigned.
- Validate incoming API requests.
- Handle exceptions with meaningful error responses.
- Unit tests for allocation logic and edge cases.

## Project Structure

```text
src
├── main
│   ├── java/com/FreightFox/dispatch_load_balancer
│   │   ├── controller
│   │   ├── service
│   │   ├── repository
│   │   ├── entity
│   │   ├── dto
│   │   ├── enums
│   │   ├── exception
│   │   └── util
│   └── resources
│       └── application.properties
└── test
    └── java/com/FreightFox/dispatch_load_balancer
        ├── service
        └── util
```

## Prerequisites

- Java 17 or later
- Maven
- PostgreSQL

## Database Configuration

Create a PostgreSQL database:

```sql
CREATE DATABASE dispatch_db;
```

Configure `src/main/resources/application.properties`:

```properties
spring.application.name=dispatch-load-balancer

spring.datasource.url=jdbc:postgresql://localhost:5432/dispatch_db
spring.datasource.username=postgres
spring.datasource.password=YOUR_PASSWORD

spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.format_sql=true

server.port=8086
```

Replace `YOUR_PASSWORD` with your PostgreSQL password.

## Run the Application

Clone the repository and navigate to the project directory.

```bash
mvn clean install
```

Run the application:

```bash
mvn spring-boot:run
```

The API will be available at:

```text
http://localhost:8086
```

## API Endpoints

### 1. Submit Delivery Orders

**POST** `/api/dispatch/orders`

Request:

```json
{
  "orders": [
    {
      "orderId": "ORD001",
      "latitude": 28.6139,
      "longitude": 77.2090,
      "address": "Connaught Place, Delhi",
      "packageWeight": 15,
      "priority": "HIGH"
    }
  ]
}
```

### 2. Register Vehicles

**POST** `/api/dispatch/vehicles`

Request:

```json
{
  "vehicles": [
    {
      "vehicleId": "VEH001",
      "capacity": 100,
      "currentLatitude": 28.7041,
      "currentLongitude": 77.1025,
      "currentAddress": "Karol Bagh, Delhi"
    }
  ]
}
```

### 3. Generate Dispatch Plan

**GET** `/api/dispatch/plan`

Returns vehicle-wise allocation details, total load, travel distance, assigned orders, and unassigned orders.

## Dispatch Allocation Logic

1. Retrieve available orders and vehicles from PostgreSQL.
2. Sort orders by priority: HIGH, MEDIUM, then LOW.
3. For each order, find vehicles with sufficient remaining capacity.
4. Calculate the distance from each eligible vehicle's current position to the order using the Haversine formula.
5. Select the nearest eligible vehicle.
6. Update the vehicle's remaining capacity and current position.
7. Add orders that cannot be assigned to the unassigned orders list.
8. Generate the final dispatch plan.

**Note:** The current allocation strategy is a greedy nearest-vehicle heuristic. It prioritizes orders and selects the nearest eligible vehicle at each step; it does not guarantee a globally optimal route.

## Run Tests

Execute all tests:

```bash
mvn test
```

The tests cover:

- Haversine distance calculation.
- HIGH priority order allocation.
- Vehicle capacity restrictions.
- Unassignable orders.
- No available vehicles.
- No available orders.

## Error Handling

The application handles:

- Invalid request fields.
- Missing required values.
- Invalid priority values.
- Duplicate identifiers.
- Insufficient vehicle capacity.
- Malformed JSON requests.
- Unexpected server errors.

## Author

Developed as a backend assignment using Java and Spring Boot.
