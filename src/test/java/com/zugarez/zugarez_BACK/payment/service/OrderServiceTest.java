package com.zugarez.zugarez_BACK.payment.service;

import com.mercadopago.resources.preference.Preference;
import com.zugarez.zugarez_BACK.CRUD.entity.Product;
import com.zugarez.zugarez_BACK.CRUD.repository.ProductRepository;
import com.zugarez.zugarez_BACK.payment.dto.CheckoutRequest;
import com.zugarez.zugarez_BACK.payment.entity.Order;
import com.zugarez.zugarez_BACK.payment.entity.OrderStatus;
import com.zugarez.zugarez_BACK.payment.repository.OrderRepository;
import com.zugarez.zugarez_BACK.security.entity.UserEntity;
import com.zugarez.zugarez_BACK.security.enums.RoleEnum;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for OrderService.
 * Tests order creation and payment processing logic.
 */
@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private MercadoPagoService mercadoPagoService;

    @InjectMocks
    private OrderService orderService;

    private UserEntity testUser;
    private Product testProduct;
    private CheckoutRequest checkoutRequest;

    @BeforeEach
    void setUp() {
        testUser = new UserEntity();
        testUser.setId(1);
        testUser.setUsername("testuser");
        testUser.setEmail("test@test.com");
        testUser.setRoles(Collections.singletonList(RoleEnum.ROLE_USER));

        testProduct = new Product();
        testProduct.setId(1);
        testProduct.setName("Test Product");
        testProduct.setPrice(100.0);
        testProduct.setBrand("Test Brand");
        testProduct.setSupplierId(1);
        testProduct.setDescription("Test Description");
        testProduct.setUrlImage("http://test.com/image.jpg");
        testProduct.setStockMinimo(10);
        testProduct.setStockActual(50);

        checkoutRequest = new CheckoutRequest();
        CheckoutRequest.CartItem cartItem = new CheckoutRequest.CartItem();
        cartItem.productId = 1;
        cartItem.quantity = 2;
        checkoutRequest.setItems(Collections.singletonList(cartItem));
    }

    @Test
    void createOrder_WithValidData_ShouldCreateOrder() throws Exception {
        // Arrange
        Preference mockPreference = mock(Preference.class);
        when(mockPreference.getId()).thenReturn("test-preference-id-123");

        when(productRepository.findById(1)).thenReturn(Optional.of(testProduct));
        when(mercadoPagoService.createPreference(any(Order.class))).thenReturn(mockPreference);
        when(orderRepository.save(any(Order.class))).thenAnswer(invocation -> {
            Order order = invocation.getArgument(0);
            if (order.getId() == null || order.getId() == 0) {
                order.setId(1);
            }
            return order;
        });

        // Act
        Order result = orderService.createOrder(testUser, checkoutRequest);

        // Assert
        assertNotNull(result);
        assertEquals(OrderStatus.PENDING, result.getStatus());
        assertNotNull(result.getSubtotal());
        assertNotNull(result.getTax());
        assertNotNull(result.getTotal());
        assertEquals("test-preference-id-123", result.getMercadopagoPreferenceId());
        verify(productRepository, times(1)).findById(1);
        verify(mercadoPagoService, times(1)).createPreference(any(Order.class));
        verify(orderRepository, atLeastOnce()).save(any(Order.class));
    }

    @Test
    void createOrder_WithEmptyCart_ShouldThrowException() {
        // Arrange
        checkoutRequest.setItems(new ArrayList<>());

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () ->
            orderService.createOrder(testUser, checkoutRequest)
        );
        assertTrue(exception.getMessage().contains("carrito") ||
                   exception.getMessage().contains("vacío"));
        verify(orderRepository, never()).save(any(Order.class));
    }

    @Test
    void createOrder_WithNullItems_ShouldThrowException() {
        // Arrange
        checkoutRequest.setItems(null);

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () ->
            orderService.createOrder(testUser, checkoutRequest)
        );

        String message = exception.getMessage();
        assertTrue(message.contains("carrito") ||
                   message.contains("vacío") ||
                   message.contains("empty") ||
                   message.toLowerCase().contains("null"),
                   "Expected exception message to contain cart-related error but was: " + message);
        verify(orderRepository, never()).save(any(Order.class));
    }

    @Test
    void createOrder_WithInvalidQuantity_ShouldThrowException() {
        // Arrange
        CheckoutRequest.CartItem cartItem = new CheckoutRequest.CartItem();
        cartItem.productId = 1;
        cartItem.quantity = 0;
        checkoutRequest.setItems(Collections.singletonList(cartItem));

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () ->
            orderService.createOrder(testUser, checkoutRequest)
        );
        assertTrue(exception.getMessage().contains("cantidad") ||
                   exception.getMessage().contains("inválida"));
        verify(orderRepository, never()).save(any(Order.class));
    }

    @Test
    void createOrder_WithNonExistentProduct_ShouldThrowException() {
        // Arrange
        when(productRepository.findById(999)).thenReturn(Optional.empty());
        CheckoutRequest.CartItem cartItem = new CheckoutRequest.CartItem();
        cartItem.productId = 999;
        cartItem.quantity = 1;
        checkoutRequest.setItems(Collections.singletonList(cartItem));

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () ->
            orderService.createOrder(testUser, checkoutRequest)
        );
        assertTrue(exception.getMessage().contains("no encontrado") ||
                   exception.getMessage().contains("not found"));
        verify(productRepository, times(1)).findById(999);
        verify(orderRepository, never()).save(any(Order.class));
    }

    @Test
    void createOrder_WithInvalidPrice_ShouldThrowException() {
        // Arrange
        testProduct.setPrice(0.0); // Invalid price
        when(productRepository.findById(1)).thenReturn(Optional.of(testProduct));

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () ->
            orderService.createOrder(testUser, checkoutRequest)
        );
        assertTrue(exception.getMessage().contains("precio") ||
                   exception.getMessage().contains("price"));
        verify(orderRepository, never()).save(any(Order.class));
    }

    @Test
    void createOrder_ShouldCalculateCorrectTotals() throws Exception {
        // Arrange
        Preference mockPreference = mock(Preference.class);
        when(mockPreference.getId()).thenReturn("test-preference-id-123");

        when(productRepository.findById(1)).thenReturn(Optional.of(testProduct));
        when(mercadoPagoService.createPreference(any(Order.class))).thenReturn(mockPreference);
        when(orderRepository.save(any(Order.class))).thenAnswer(invocation -> {
            Order order = invocation.getArgument(0);
            if (order.getId() == null || order.getId() == 0) {
                order.setId(1);
            }
            return order;
        });

        // Act
        Order result = orderService.createOrder(testUser, checkoutRequest);

        // Assert
        BigDecimal expectedSubtotal = BigDecimal.valueOf(200.0); // 100 * 2
        BigDecimal expectedTax = expectedSubtotal.multiply(BigDecimal.valueOf(0.05)); // 5%
        BigDecimal expectedTotal = expectedSubtotal.add(expectedTax);

        assertNotNull(result);
        assertEquals(expectedSubtotal.doubleValue(), result.getSubtotal().doubleValue(), 0.01);
        assertEquals(expectedTax.doubleValue(), result.getTax().doubleValue(), 0.01);
        assertEquals(expectedTotal.doubleValue(), result.getTotal().doubleValue(), 0.01);
    }

    @Test
    void getOrdersByUser_ShouldReturnUserOrders() {
        // Arrange
        Order order1 = new Order();
        order1.setId(1);
        order1.setUser(testUser);
        order1.setStatus(OrderStatus.PENDING);

        when(orderRepository.findByUserId(1)).thenReturn(Collections.singletonList(order1));

        // Act
        var result = orderService.getOrdersByUser(1);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(1, result.get(0).getUser().getId());
        verify(orderRepository, times(1)).findByUserId(1);
    }
}
