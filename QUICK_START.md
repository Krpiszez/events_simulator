# RokEvents - Quick Start Guide

## 🚀 Getting Started

### Prerequisites
- Java 17 or higher
- Maven 3.6+
- Terminal/Command Prompt

### Running the Application

1. **Navigate to the project directory:**
   ```bash
   cd path/to/rokevents
   ```

2. **Start the application:**
   ```bash
   ./mvnw spring-boot:run
   ```

   The application will start on `http://localhost:8080`

3. **Test the API:**
   ```bash
   curl -X POST http://localhost:8080/api/events/simulate \
     -H "Content-Type: application/json" \
     -d '{"eventId":1,"gemAmount":8000}'
   ```

## 📊 API Example

### Request
```json
POST /api/events/simulate
Content-Type: application/json

{
  "eventId": 1,
  "gemAmount": 8000
}
```

### Response
```json
{
  "eventId": 1,
  "eventName": "Esmeralda's House",
  "totalPulls": 10000,
  "gemSpent": 8000,
  "results": [
    {
      "itemName": "8-Hour Healing Speedup x4",
      "count": 3020,
      "percentage": 30.20
    },
    {
      "itemName": "8-Hour Training Speedup x4",
      "count": 3031,
      "percentage": 30.31
    },
    {
      "itemName": "Blueprint Fragment Choice Chest (Includes Engineering) x4",
      "count": 1632,
      "percentage": 16.32
    },
    {
      "itemName": "Blueprint Fragment Choice Chest Helmet (Includes Engineering) x4",
      "count": 1635,
      "percentage": 16.35
    },
    {
      "itemName": "Legendary Equipment Material Choice Chest x1",
      "count": 414,
      "percentage": 4.14
    },
    {
      "itemName": "Legendary Equipment Material Choice Chest x3",
      "count": 268,
      "percentage": 2.68
    }
  ]
}
```

## 🧪 Running Tests

```bash
./mvnw test
```

All tests pass including:
- ✅ Zero gems simulation
- ✅ Probability distribution validation
- ✅ Invalid event handling
- ✅ Results percentage sum validation
- ✅ Results sorting by percentage
- ✅ Total count validation

## 📁 Project Structure

```
src/main/java/com/example/rokevents/
├── entity/              # JPA Entities
├── dto/                 # Data Transfer Objects
├── repository/          # Spring Data JPA Repositories
├── service/             # Business Logic
├── controller/          # REST Controllers
└── config/              # Configuration & Data Initialization
```

## Esmeralda's House Event

**Event ID:** 1

**Items & Drop Rates:**
- Level 4 "Pick One" Resource Chest x5: 24.691%
- 8-Hour Healing Speedup x4: 15.432%
- 8-Hour Training Speedup x4: 15.432%
- Epic Equipment Material Choice Chest x1: 12.345%
- Blueprint Fragment Choice Chest Helmet (Includes Engineering) x4: 8.23%
- Blueprint Fragment Choice Chest (Includes Engineering) x4: 8.23%
- Epic Equipment Material Choice Chest x2: 8.23%
- Epic Equipment Material Choice Chest x3: 4.115%
- Legendary Equipment Material Choice Chest x1: 2.057%
- Legendary Equipment Material Choice Chest x3: 1.234%

**Total Drop Rate:** 100% ✓

**Pull Costs:**
- Standard pull: 800 gems
- 5-pull bundle: 3600 gems (720/pull)
- Daily pull: 400 gems
- Free pull: 1 per day

## 🛠️ Building the Project

```bash
# Clean and install
./mvnw clean install

# Build without tests
./mvnw clean install -DskipTests
```

## 📚 Documentation

- **Full API Documentation:** See `README_API.md`
- **Architecture & Development:** See `HELP.md`

## 🔧 Configuration

The application uses H2 in-memory database for development.

**Database Console:**
- URL: `http://localhost:8080/h2-console`
- Username: `sa`
- Password: (empty)
- JDBC URL: `jdbc:h2:mem:testdb`

## 💡 Usage Examples

### Simulate 400 gems spend
```bash
curl -X POST http://localhost:8080/api/events/simulate \
  -H "Content-Type: application/json" \
  -d '{"eventId":1,"gemAmount":400}'
```

### Simulate 100,000 gems spend (large amount)
```bash
curl -X POST http://localhost:8080/api/events/simulate \
  -H "Content-Type: application/json" \
  -d '{"eventId":1,"gemAmount":100000}'
```

### Simulate 0 gems (edge case)
```bash
curl -X POST http://localhost:8080/api/events/simulate \
  -H "Content-Type: application/json" \
  -d '{"eventId":1,"gemAmount":0}'
```

## 🎯 Key Features

✨ **Run 1000 Simulations** - Each API call runs 1000 independent simulations for accuracy
✨ **Weighted Random Selection** - Accurate probability distribution based on configured drop rates
✨ **Detailed Results** - Get item counts and percentages for each item
✨ **Automatic Data Seeding** - Esmeralda's House is automatically initialized on startup
✨ **Input Validation** - Proper error handling for invalid requests
✨ **Comprehensive Tests** - Full unit test suite with edge cases

## 🐛 Troubleshooting

**Application won't start:**
- Ensure Java 17+ is installed: `java -version`
- Check Maven is installed: `mvn -version`
- Check port 8080 is available

**API returns 404:**
- Ensure the application is running
- Verify event ID is 1 (Esmeralda's House)
- Check Content-Type header is `application/json`

**Tests fail:**
- Run `mvn clean test` to ensure fresh build
- Check Java version is 17+

## 📞 Support

For issues or questions, check the full documentation in `README_API.md`
