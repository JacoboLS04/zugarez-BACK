# Zugarez Backend - Test Suite Documentation

## Overview
This document describes the comprehensive test suite created for the Zugarez e-commerce backend application.

## Test Coverage

### 1. **Unit Tests**

#### Product Module (CRUD)
- **ProductServiceTest** - Tests business logic for product management
  - `getAllProducts_ShouldReturnAllProducts()`
  - `getProductById_WhenProductExists_ShouldReturnProduct()`
  - `getProductById_WhenProductDoesNotExist_ShouldThrowException()`
  - `saveProduct_WhenProductNameIsUnique_ShouldSaveProduct()`
  - `saveProduct_WhenProductNameExists_ShouldThrowException()`
  - `updateProduct_WhenProductExists_ShouldUpdateProduct()`
  - `updateProduct_WhenProductDoesNotExist_ShouldThrowException()`
  - `updateProduct_WhenNameExistsForDifferentProduct_ShouldThrowException()`
  - `deleteProduct_WhenProductExists_ShouldDeleteProduct()`
  - `deleteProduct_WhenProductDoesNotExist_ShouldThrowException()`

- **ProductTest** - Tests Product entity
  - Constructor tests
  - Getter/Setter tests
  - Business logic validation tests

- **ProductDtoTest** - Tests DTO validation
  - Validation annotation tests
  - Required field tests
  - Min/Max value tests

#### Security Module
- **UserEntityServiceTest** - Tests authentication and user management
  - `checkPassword_WhenPasswordMatches_ShouldReturnTrue()`
  - `checkPassword_WhenPasswordDoesNotMatch_ShouldReturnFalse()`
  - `loginByUser_ShouldGenerateJwtToken()`
  - `create_WithValidData_ShouldCreateUser()`
  - `create_WithEmptyUsername_ShouldThrowException()`
  - `create_WithEmptyEmail_ShouldThrowException()`
  - `create_WithEmptyPassword_ShouldThrowException()`
  - `create_WithExistingUsername_ShouldThrowException()`
  - `create_WithExistingEmail_ShouldThrowException()`

#### Inventory Module
- **LoteServiceTest** - Tests inventory batch management
  - `getAllLotes_ShouldReturnAllLotes()`
  - `getLoteById_WhenLoteExists_ShouldReturnLote()`
  - `getLoteById_WhenLoteDoesNotExist_ShouldThrowException()`
  - `getLotesByProduct_ShouldReturnLotesForProduct()`
  - `getAvailableLotes_ShouldReturnOnlyAvailableLotes()`
  - `getLotesProximosAVencer_ShouldReturnExpiringLotes()`
  - `getStockTotalByProduct_ShouldReturnTotalStock()`
  - `saveLote_WhenProductExists_ShouldCreateLote()`
  - `saveLote_WhenProductDoesNotExist_ShouldThrowException()`

#### Payment Module
- **OrderServiceTest** - Tests order and payment processing
  - `createOrder_WithValidData_ShouldCreateOrder()`
  - `createOrder_WithEmptyCart_ShouldThrowException()`
  - `createOrder_WithNullItems_ShouldThrowException()`
  - `createOrder_WithInvalidQuantity_ShouldThrowException()`
  - `createOrder_WithNonExistentProduct_ShouldThrowException()`
  - `createOrder_WithInvalidPrice_ShouldThrowException()`
  - `createOrder_ShouldCalculateCorrectTotals()`
  - `getOrdersByUser_ShouldReturnUserOrders()`

#### Global Module
- **CustomExceptionsTest** - Tests custom exception handling
  - ResourceNotFoundException tests
  - AttributeException tests

### 2. **Integration Tests**

#### REST API Tests
- **ProductControllerTest** - Tests REST endpoints with security
  - `getAll_WithUserRole_ShouldReturnProducts()`
  - `getAll_WithoutAuthentication_ShouldReturnUnauthorized()`
  - `getOne_WhenProductExists_ShouldReturnProduct()`
  - `getOne_WhenProductDoesNotExist_ShouldReturnNotFound()`
  - `save_WithAdminRole_ShouldCreateProduct()`
  - `save_WithUserRole_ShouldReturnForbidden()`
  - `save_WithInvalidData_ShouldReturnBadRequest()`
  - `save_WhenNameExists_ShouldReturnBadRequest()`
  - `update_WithAdminRole_ShouldUpdateProduct()`
  - `update_WithUserRole_ShouldReturnForbidden()`
  - `delete_WithAdminRole_ShouldDeleteProduct()`
  - `delete_WithUserRole_ShouldReturnForbidden()`
  - `delete_WhenProductDoesNotExist_ShouldReturnNotFound()`

#### Repository Tests
- **ProductRepositoryTest** - Tests database queries
  - `save_ShouldPersistProduct()`
  - `findById_WhenProductExists_ShouldReturnProduct()`
  - `findById_WhenProductDoesNotExist_ShouldReturnEmpty()`
  - `existsByName_WhenProductExists_ShouldReturnTrue()`
  - `existsByName_WhenProductDoesNotExist_ShouldReturnFalse()`
  - `findByName_WhenProductExists_ShouldReturnProduct()`
  - `findByName_WhenProductDoesNotExist_ShouldReturnEmpty()`
  - `findAllExplicit_ShouldReturnAllProducts()`
  - `delete_ShouldRemoveProduct()`
  - `update_ShouldModifyProduct()`

#### Application Context Tests
- **ZugarezBackApplicationTests** - Tests Spring Boot configuration
  - `contextLoads()` - Ensures application context loads
  - Bean loading verification tests
  - Dependency injection tests

## Test Technologies Used

- **JUnit 5** - Testing framework
- **Mockito** - Mocking framework for unit tests
- **Spring Boot Test** - Integration testing support
- **Spring Security Test** - Security testing utilities
- **AssertJ** - Fluent assertions
- **MockMvc** - REST API testing
- **TestEntityManager** - JPA testing support

## Running the Tests

### Run All Tests
```bash
mvn test
```

### Run Specific Test Class
```bash
mvn test -Dtest=ProductServiceTest
```

### Run Tests with Coverage
```bash
mvn clean test jacoco:report
```

### Run Integration Tests Only
```bash
mvn test -Dtest=**/*IntegrationTest
```

## Test Configuration

- **TestConfig.java** - Provides test-specific Spring configuration
- **application-test.properties** - Test-specific application properties (if needed)

## Test Structure

```
src/test/java/com/zugarez/zugarez_BACK/
├── ZugarezBackApplicationTests.java
├── config/
│   └── TestConfig.java
├── CRUD/
│   ├── controller/
│   │   └── ProductControllerTest.java
│   ├── dto/
│   │   └── ProductDtoTest.java
│   ├── entity/
│   │   └── ProductTest.java
│   ├── repository/
│   │   └── ProductRepositoryTest.java
│   └── service/
│       └── ProductServiceTest.java
├── global/
│   └── exceptions/
│       └── CustomExceptionsTest.java
├── inventory/
│   └── service/
│       └── LoteServiceTest.java
├── payment/
│   └── service/
│       └── OrderServiceTest.java
└── security/
    └── service/
        └── UserEntityServiceTest.java
```

## Best Practices Implemented

1. **AAA Pattern** - Arrange, Act, Assert structure in all tests
2. **Mock Isolation** - Each test uses mocks to isolate units
3. **Clear Naming** - Descriptive test method names following `method_condition_expectedResult` pattern
4. **Test Independence** - Each test can run independently
5. **Setup Methods** - Common test data in @BeforeEach methods
6. **Security Testing** - Uses @WithMockUser for security tests
7. **Validation Testing** - Tests Jakarta Bean Validation annotations
8. **Exception Testing** - Verifies exception handling and messages

## Code Coverage Goals

- **Service Layer**: > 80%
- **Controller Layer**: > 75%
- **Repository Layer**: > 70%
- **Entity Layer**: > 60%

## Next Steps

1. Add more edge case tests
2. Implement E2E tests with TestContainers
3. Add performance tests
4. Implement mutation testing
5. Add contract tests for external APIs (MercadoPago)

## Troubleshooting

### Common Issues

1. **Database Connection Issues** - Use H2 in-memory database for tests
2. **Security Context Issues** - Use @WithMockUser or SecurityMockMvcRequestPostProcessors
3. **Mock Not Working** - Ensure @ExtendWith(MockitoExtension.class) is present

## Contributing

When adding new tests:
1. Follow the existing test structure
2. Use descriptive test names
3. Include Javadoc comments
4. Ensure tests are isolated
5. Run all tests before committing

---
**Created**: 2025-10-19  
**Last Updated**: 2025-10-19  
**Version**: 1.0

