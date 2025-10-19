package com.zugarez.zugarez_BACK;

import com.zugarez.zugarez_BACK.CRUD.repository.ProductRepository;
import com.zugarez.zugarez_BACK.CRUD.service.ProductService;
import com.zugarez.zugarez_BACK.security.repository.UserEntityRepository;
import com.zugarez.zugarez_BACK.security.service.UserEntityService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Integration tests for the Spring Boot application.
 * Tests application context, bean loading, and configuration.
 */
@SpringBootTest
class ZugarezBackApplicationTests {

	@Autowired
	private ApplicationContext applicationContext;

	@Autowired(required = false)
	private ProductService productService;

	@Autowired(required = false)
	private ProductRepository productRepository;

	@Autowired(required = false)
	private UserEntityService userEntityService;

	@Autowired(required = false)
	private UserEntityRepository userEntityRepository;

	@Test
	void contextLoads() {
		// This test ensures the application context loads successfully
		assertNotNull(applicationContext);
	}

	@Test
	void productService_ShouldBeLoaded() {
		// Verify ProductService bean is loaded
		assertNotNull(productService, "ProductService should be loaded in application context");
	}

	@Test
	void productRepository_ShouldBeLoaded() {
		// Verify ProductRepository bean is loaded
		assertNotNull(productRepository, "ProductRepository should be loaded in application context");
	}

	@Test
	void userEntityService_ShouldBeLoaded() {
		// Verify UserEntityService bean is loaded
		assertNotNull(userEntityService, "UserEntityService should be loaded in application context");
	}

	@Test
	void userEntityRepository_ShouldBeLoaded() {
		// Verify UserEntityRepository bean is loaded
		assertNotNull(userEntityRepository, "UserEntityRepository should be loaded in application context");
	}

	@Test
	void applicationContext_ShouldContainProductService() {
		// Verify ProductService is available in context
		assertTrue(applicationContext.containsBean("productService"));
	}

	@Test
	void applicationContext_ShouldContainUserEntityService() {
		// Verify UserEntityService is available in context
		assertTrue(applicationContext.containsBean("userEntityService"));
	}

	@Test
	void beans_ShouldBeProperlyWired() {
		// Verify that services have their dependencies injected
		if (productService != null) {
			assertDoesNotThrow(() -> {
				// If the service can be called without NPE, dependencies are wired
				productService.getAllProducts();
			});
		}
	}
}

