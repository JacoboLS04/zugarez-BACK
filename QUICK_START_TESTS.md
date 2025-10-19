# Quick Start Guide - Running Tests

## Prerequisites
- Java 17 or higher
- Maven (or use the included Maven wrapper)
- PostgreSQL (for integration tests, or configure H2 for testing)

## Running Tests

### Option 1: Using Maven Wrapper (Recommended)
```bash
# On Windows (PowerShell)
./mvnw clean test

# On Windows (CMD)
mvnw.cmd clean test

# On Linux/Mac
./mvnw clean test
```

### Option 2: Using Maven directly
```bash
mvn clean test
```

### Run Specific Test Class
```bash
./mvnw test -Dtest=ProductServiceTest
```

### Run Tests with Verbose Output
```bash
./mvnw test -X
```

## Test Files Created

### Unit Tests
1. **ProductServiceTest.java** - Tests product business logic (10 tests)
2. **ProductTest.java** - Tests product entity (9 tests)
3. **ProductDtoTest.java** - Tests DTO validation (11 tests)
4. **UserEntityServiceTest.java** - Tests user authentication (9 tests)
5. **LoteServiceTest.java** - Tests inventory management (9 tests)
6. **OrderServiceTest.java** - Tests payment processing (8 tests)
7. **CustomExceptionsTest.java** - Tests exception handling (6 tests)

### Integration Tests
1. **ProductControllerTest.java** - Tests REST API endpoints (13 tests)
2. **ProductRepositoryTest.java** - Tests database operations (10 tests)
3. **ZugarezBackApplicationTests.java** - Tests Spring context (8 tests)

### Configuration
1. **TestConfig.java** - Test-specific Spring configuration

**Total: 93 comprehensive tests**

## Test Structure Summary

```
src/test/java/
└── com/zugarez/zugarez_BACK/
    ├── ZugarezBackApplicationTests.java (8 tests)
    ├── config/
    │   └── TestConfig.java
    ├── CRUD/
    │   ├── controller/
    │   │   └── ProductControllerTest.java (13 tests)
    │   ├── dto/
    │   │   └── ProductDtoTest.java (11 tests)
    │   ├── entity/
    │   │   └── ProductTest.java (9 tests)
    │   ├── repository/
    │   │   └── ProductRepositoryTest.java (10 tests)
    │   └── service/
    │       └── ProductServiceTest.java (10 tests)
    ├── global/
    │   └── exceptions/
    │       └── CustomExceptionsTest.java (6 tests)
    ├── inventory/
    │   └── service/
    │       └── LoteServiceTest.java (9 tests)
    ├── payment/
    │   └── service/
    │       └── OrderServiceTest.java (8 tests)
    └── security/
        └── service/
            └── UserEntityServiceTest.java (9 tests)
```

## What's Tested?

### ✅ Product Management
- CRUD operations
- Validation (name uniqueness, required fields)
- Exception handling
- Database operations
- REST API security

### ✅ Authentication & Security
- User creation and validation
- Password encoding/verification
- JWT token generation
- Role-based access control

### ✅ Inventory Management
- Batch (Lote) creation and tracking
- Stock management
- Expiration date handling
- Product relationship validation

### ✅ Payment Processing
- Order creation
- Cart validation
- Price calculations (subtotal, tax, total)
- Item quantity validation

### ✅ Exception Handling
- ResourceNotFoundException
- AttributeException
- HTTP status codes

## Troubleshooting

### Tests failing due to database connection
Add this to `src/test/resources/application-test.properties`:
```properties
spring.datasource.url=jdbc:h2:mem:testdb
spring.datasource.driverClassName=org.h2.Driver
spring.jpa.database-platform=org.hibernate.dialect.H2Dialect
```

Then add H2 dependency to pom.xml:
```xml
<dependency>
    <groupId>com.h2database</groupId>
    <artifactId>h2</artifactId>
    <scope>test</scope>
</dependency>
```

### Tests failing due to security context
The tests use `@WithMockUser` annotation which is already configured.
Ensure `spring-security-test` dependency is in your pom.xml (already added).

## Next Steps

1. Review test results
2. Add more edge case tests as needed
3. Configure test coverage reporting (JaCoCo)
4. Set up CI/CD pipeline to run tests automatically

## Test Coverage Plugin (Optional)

Add to pom.xml for coverage reports:
```xml
<plugin>
    <groupId>org.jacoco</groupId>
    <artifactId>jacoco-maven-plugin</artifactId>
    <version>0.8.10</version>
    <executions>
        <execution>
            <goals>
                <goal>prepare-agent</goal>
            </goals>
        </execution>
        <execution>
            <id>report</id>
            <phase>test</phase>
            <goals>
                <goal>report</goal>
            </goals>
        </execution>
    </executions>
</plugin>
```

Then run: `./mvnw clean test jacoco:report`

Report will be at: `target/site/jacoco/index.html`

