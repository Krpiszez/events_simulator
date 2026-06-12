# RokEvents - Mobile Game Event Simulator API

A Spring Boot REST API for simulating gem spending in mobile game events like Esmeralda's House. The API runs simulations to calculate the probability of obtaining specific items when spending a given amount of gems.

## Features

- **Event Management**: Create and manage game events with configurable item drop rates
- **Simulation Engine**: Run 1000 simulations of pulls/spins to get accurate probability distributions
- **REST API**: Simple POST endpoint for simulation requests
- **Database Seeding**: Automatically initializes Esmeralda's House event with item data on startup
- **Error Handling**: Proper validation and error responses

## Technology Stack

- **Framework**: Spring Boot 4.0.6
- **Java Version**: 17
- **Database**: H2 (in-memory for development)
- **Build Tool**: Maven

## Project Structure

```
src/main/java/com/example/rokevents/
├── entity/              # JPA entities
│   ├── Event.java
│   ├── Item.java
│   └── ItemDropRate.java
├── dto/                 # Data Transfer Objects
│   ├── SimulationRequest.java
│   ├── SimulationResponse.java
│   └── ItemResult.java
├── repository/          # JPA Repositories
│   ├── EventRepository.java
│   ├── ItemRepository.java
│   └── ItemDropRateRepository.java
├── service/             # Business Logic
│   ├── EventService.java
│   └── SimulationService.java
├── controller/          # REST Controllers
│   └── EventController.java
├── config/              # Configuration
│   └── DataInitializer.java
└── RokeventsApplication.java
```

## API Endpoints

### Simulate Event Pulls
**POST** `/api/events/simulate`

Simulates gem spending in an event and returns the aggregated item distribution after running 1000 simulations.

#### Request Body
```json
{
  "eventId": 1,
  "gemAmount": 8000
}
```

**Parameters:**
- `eventId` (Long): The ID of the event to simulate
- `gemAmount` (Long): Total gems to spend (must be non-negative)

#### Response
```json
{
  "eventId": 1,
  "eventName": "Esmeralda's House",
  "totalPulls": 10000,
  "gemSpent": 8000,
  "results": [
    {
      "itemName": "Level 4 \"Pick One\" Resource Chest x5",
      "count": 2490,
      "percentage": 24.90
    },
    {
      "itemName": "8-Hour Healing Speedup x4",
      "count": 1541,
      "percentage": 15.41
    },
    ...
  ]
}
```

**Response Fields:**
- `eventId`: The event ID
- `eventName`: The event name
- `totalPulls`: Total number of pulls across all 1000 simulations
- `gemSpent`: Total gems spent
- `results`: Array of item results, sorted by percentage (highest first)

**HTTP Status Codes:**
- `200 OK`: Simulation successful
- `400 Bad Request`: Invalid request parameters
- `404 Not Found`: Event not found
- `500 Internal Server Error`: Server error

#### Example Usage

```bash
curl -X POST http://localhost:8080/api/events/simulate \
  -H "Content-Type: application/json" \
  -d '{"eventId":1,"gemAmount":8000}'
```

## Esmeralda's House Event Details

The event is automatically seeded with the following items and drop rates:

| Item | Quantity | Drop Rate |
|------|----------|-----------|
| Level 4 "Pick One" Resource Chest | x5 | 24.69% |
| 8-Hour Healing Speedup | x4 | 15.43% |
| 8-Hour Training Speedup | x4 | 15.43% |
| Epic Equipment Material Choice Chest | x1 | 12.35% |
| Blueprint Fragment Choice Chest Helmet (Includes Engineering) | x4 | 8.23% |
| Blueprint Fragment Choice Chest (Includes Engineering) | x4 | 8.23% |
| Epic Equipment Material Choice Chest | x2 | 8.23% |
| Epic Equipment Material Choice Chest | x3 | 4.12% |
| Legendary Equipment Material Choice Chest | x1 | 2.06% |
| Legendary Equipment Material Choice Chest | x3 | 1.23% |

**Total:** 100.00% ✓

**Pricing:**
- Standard pull: 800 gems
- 5-pull bundle: 3600 gems (720 per pull)
- Daily pull: 400 gems
- Free pull: 1 per day

## Running the Application

### Prerequisites
- Java 17 or higher
- Maven 3.6+

### Build
```bash
./mvnw clean install
```

### Run
```bash
./mvnw spring-boot:run
```

The application will start on `http://localhost:8080`

### Access H2 Console
When running locally, you can access the H2 database console at:
`http://localhost:8080/h2-console`

Connection details (from `application.yaml`):
- URL: `jdbc:h2:mem:testdb`
- Username: `sa`
- Password: (empty)

## Simulation Logic

The simulation service operates as follows:

1. **Calculate Total Pulls**: Divide the gem amount by the pull cost (800 gems)
2. **Initialize Results**: Create a counter for each item
3. **Run Simulations**: For each of the 1000 simulations:
   - For each pull in the total pulls calculated
   - Select an item using weighted random selection based on drop rates
   - Increment the counter for that item
4. **Calculate Percentages**: Compute each item's percentage of total drops
5. **Sort Results**: Sort items by percentage in descending order

## Development

### Adding a New Event

1. Create entries in the database or add them to `DataInitializer.java`
2. The event will be automatically available through the API

### Modifying Drop Rates

Drop rates are stored in the `ItemDropRate` table. You can:
- Modify `DataInitializer.java` to change seeded values
- Update the database directly through the H2 console

### Testing

Run tests with:
```bash
./mvnw test
```

## Configuration

All configurations can be modified in `application.yaml`:

```yaml
spring:
  jpa:
    hibernate:
      ddl-auto: create-drop  # create-drop for dev, validate for prod
    show-sql: false
  h2:
    console:
      enabled: true
server:
  port: 8080
```

## Future Enhancements

- [ ] Support for multiple pull cost tiers
- [ ] Historical simulation results storage
- [ ] Event scheduling and time-based availability
- [ ] User accounts and pull history tracking
- [ ] Probability distribution validation
- [ ] Advanced analytics and trends

## License

This project is part of the RokEvents system.

## Notes

- Drop rates are automatically normalized to sum to 100%
- Simulations are independent; results vary due to random distribution
- The pull cost is hardcoded to 800 gems per pull
- All simulations run 1000 iterations for accuracy
