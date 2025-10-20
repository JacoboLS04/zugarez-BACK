package com.zugarez.zugarez_BACK.payment.service;

import com.mercadopago.resources.preference.Preference;
import com.zugarez.zugarez_BACK.CRUD.entity.Product;
import com.zugarez.zugarez_BACK.CRUD.repository.ProductRepository;
import com.zugarez.zugarez_BACK.payment.dto.CheckoutRequest;
import com.zugarez.zugarez_BACK.payment.entity.Order;
import com.zugarez.zugarez_BACK.payment.entity.OrderStatus;
import com.zugarez.zugarez_BACK.payment.repository.OrderRepository;
import com.zugarez.zugarez_BACK.security.entity.UserEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Unit tests for OrderService.
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
    private Order testOrder;

    @BeforeEach
    void setUp() {
        testUser = new UserEntity();
        testUser.setId(1);
        testUser.setUsername("testuser");
        testUser.setEmail("test@example.com");

        testProduct = new Product();
        testProduct.setId(1);
        testProduct.setName("Test Product");
        testProduct.setPrice(100.0);

        CheckoutRequest.CartItem cartItem = new CheckoutRequest.CartItem();
        cartItem.productId = 1;
        cartItem.quantity = 2;

        checkoutRequest = new CheckoutRequest();
        checkoutRequest.setItems(Arrays.asList(cartItem));

        testOrder = new Order();
        testOrder.setId(1);
        testOrder.setUser(testUser);
        testOrder.setSubtotal(BigDecimal.valueOf(200.0));
        testOrder.setTax(BigDecimal.valueOf(10.0));
        testOrder.setTotal(BigDecimal.valueOf(210.0));
        testOrder.setStatus(OrderStatus.PENDING);
    }

    // java
    @Test
    void testCreateOrder_Success() throws Exception {
        when(productRepository.findById(1)).thenReturn(Optional.of(testProduct));
        // mock MercadoPago preference so getId() is not called on null
        Preference mockPref = mock(Preference.class);
        when(mockPref.getId()).thenReturn("pref-123");
        when(mercadoPagoService.createPreference(any())).thenReturn(mockPref);

        when(orderRepository.save(any(Order.class))).thenReturn(testOrder);

        Order result = orderService.createOrder(testUser, checkoutRequest);

        assertNotNull(result);
        assertEquals(OrderStatus.PENDING, result.getStatus());
        verify(orderRepository, atLeast(1)).save(any(Order.class));
    }

    @Test
    void testCreateOrder_EmptyCart() {
        checkoutRequest.setItems(new ArrayList<>());

        assertThrows(RuntimeException.class, () -> {
            orderService.createOrder(testUser, checkoutRequest);
        });
    }

    @Test
    void testCreateOrder_ProductNotFound() {
        when(productRepository.findById(1)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> {
            orderService.createOrder(testUser, checkoutRequest);
        });
    }

    @Test
    void testCreateOrder_InvalidQuantity() {
        CheckoutRequest.CartItem cartItem = new CheckoutRequest.CartItem();
        cartItem.productId = 1;
        cartItem.quantity = 0;

        checkoutRequest.setItems(Arrays.asList(cartItem));

        assertThrows(RuntimeException.class, () -> {
            orderService.createOrder(testUser, checkoutRequest);
        });
    }

    @Test
    void testGetOrderById_Success() {
        when(orderRepository.findById(1)).thenReturn(Optional.of(testOrder));

        Order result = orderService.getOrderById(1);

        assertNotNull(result);
        assertEquals(1, result.getId());
        verify(orderRepository, times(1)).findById(1);
    }

    @Test
    void testGetOrderById_NotFound() {
        when(orderRepository.findById(999)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> {
            orderService.getOrderById(999);
        });
    }

    @Test
    void testGetOrdersByUser() {
        List<Order> orders = Arrays.asList(testOrder);
        when(orderRepository.findByUserId(1)).thenReturn(orders);

        List<Order> result = orderService.getOrdersByUser(1);

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(orderRepository, times(1)).findByUserId(1);
    }

    @Test
    void testUpdateOrderStatus_Success() {
        testOrder.setMercadopagoPreferenceId("pref-123");
        when(orderRepository.findByMercadopagoPreferenceId("pref-123")).thenReturn(Optional.of(testOrder));
        when(orderRepository.save(any(Order.class))).thenReturn(testOrder);

        Order result = orderService.updateOrderStatus("pref-123", OrderStatus.APPROVED, "pay-456");

        assertNotNull(result);
        assertEquals(OrderStatus.APPROVED, result.getStatus());
        verify(orderRepository, times(1)).save(any(Order.class));
    }

    @Test
    void testUpdateOrderStatus_OrderNotFound() {
        when(orderRepository.findByMercadopagoPreferenceId("pref-999")).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> {
            orderService.updateOrderStatus("pref-999", OrderStatus.APPROVED, "pay-456");
        });
    }
}

