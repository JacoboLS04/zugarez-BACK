package com.zugarez.zugarez_BACK.inventory.service;

import com.zugarez.zugarez_BACK.CRUD.entity.Product;
import com.zugarez.zugarez_BACK.CRUD.repository.ProductRepository;
import com.zugarez.zugarez_BACK.global.exceptions.AttributeException;
import com.zugarez.zugarez_BACK.global.exceptions.ResourceNotFoundException;
import com.zugarez.zugarez_BACK.inventory.dto.LoteDto;
import com.zugarez.zugarez_BACK.inventory.entity.Lote;
import com.zugarez.zugarez_BACK.inventory.repository.LoteRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for LoteService.
 * Tests business logic for inventory batch (lote) CRUD operations and stock management.
 */
@ExtendWith(MockitoExtension.class)
class LoteServiceTest {

    @Mock
    private LoteRepository loteRepository;

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private LoteService loteService;

    private Product testProduct;
    private Lote testLote;
    private LoteDto testLoteDto;

    @BeforeEach
    void setUp() {
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

        testLote = new Lote();
        testLote.setId(1);
        testLote.setProduct(testProduct);
        testLote.setExpirationDate(LocalDate.now().plusMonths(6));
        testLote.setInitialQuantity(100);
        testLote.setAvailableQuantity(100);
        testLote.setBatchPrice(5000.0);
        testLote.setUnitPrice(50.0);

        testLoteDto = new LoteDto();
        testLoteDto.setProductId(1);
        testLoteDto.setExpirationDate(LocalDate.now().plusMonths(6));
        testLoteDto.setInitialQuantity(100);
        testLoteDto.setAvailableQuantity(100);
        testLoteDto.setBatchPrice(5000.0);
        testLoteDto.setUnitPrice(50.0);
    }

    @Test
    void getAllLotes_ShouldReturnAllLotes() {
        // Arrange
        List<Lote> lotes = Arrays.asList(testLote);
        when(loteRepository.findAllOrderByExpirationDate()).thenReturn(lotes);

        // Act
        List<Lote> result = loteService.getAllLotes();

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(100, result.get(0).getAvailableQuantity());
        verify(loteRepository, times(1)).findAllOrderByExpirationDate();
    }

    @Test
    void getLoteById_WhenLoteExists_ShouldReturnLote() throws ResourceNotFoundException {
        // Arrange
        when(loteRepository.findById(1)).thenReturn(Optional.of(testLote));

        // Act
        Lote result = loteService.getLoteById(1);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.getId());
        assertEquals(100, result.getAvailableQuantity());
        verify(loteRepository, times(1)).findById(1);
    }

    @Test
    void getLoteById_WhenLoteDoesNotExist_ShouldThrowException() {
        // Arrange
        when(loteRepository.findById(999)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> {
            loteService.getLoteById(999);
        });
        verify(loteRepository, times(1)).findById(999);
    }

    @Test
    void getLotesByProduct_ShouldReturnLotesForProduct() {
        // Arrange
        List<Lote> lotes = Arrays.asList(testLote);
        when(loteRepository.findByProductId(1)).thenReturn(lotes);

        // Act
        List<Lote> result = loteService.getLotesByProduct(1);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(1, result.get(0).getProduct().getId());
        verify(loteRepository, times(1)).findByProductId(1);
    }

    @Test
    void getAvailableLotes_ShouldReturnOnlyAvailableLotes() {
        // Arrange
        List<Lote> availableLotes = Arrays.asList(testLote);
        when(loteRepository.findByAvailableQuantityGreaterThan(0)).thenReturn(availableLotes);

        // Act
        List<Lote> result = loteService.getAvailableLotes();

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertTrue(result.get(0).getAvailableQuantity() > 0);
        verify(loteRepository, times(1)).findByAvailableQuantityGreaterThan(0);
    }

    @Test
    void getLotesProximosAVencer_ShouldReturnExpiringLotes() {
        // Arrange
        Lote expiringLote = new Lote();
        expiringLote.setId(2);
        expiringLote.setProduct(testProduct);
        expiringLote.setExpirationDate(LocalDate.now().plusDays(5));
        expiringLote.setAvailableQuantity(50);

        List<Lote> expiringLotes = Arrays.asList(expiringLote);
        when(loteRepository.findByExpirationDateBefore(any(LocalDate.class))).thenReturn(expiringLotes);

        // Act
        List<Lote> result = loteService.getLotesProximosAVencer(10);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertTrue(result.get(0).getExpirationDate().isBefore(LocalDate.now().plusDays(11)));
        verify(loteRepository, times(1)).findByExpirationDateBefore(any(LocalDate.class));
    }

    @Test
    void getStockTotalByProduct_ShouldReturnTotalStock() {
        // Arrange
        when(loteRepository.getTotalAvailableQuantityByProduct(1)).thenReturn(250);

        // Act
        int result = loteService.getStockTotalByProduct(1);

        // Assert
        assertEquals(250, result);
        verify(loteRepository, times(1)).getTotalAvailableQuantityByProduct(1);
    }

    @Test
    void saveLote_WhenProductExists_ShouldCreateLote() throws ResourceNotFoundException, AttributeException {
        // Arrange
        when(productRepository.findById(1)).thenReturn(Optional.of(testProduct));
        when(loteRepository.save(any(Lote.class))).thenReturn(testLote);

        // Act
        Lote result = loteService.saveLote(testLoteDto);

        // Assert
        assertNotNull(result);
        assertEquals(100, result.getAvailableQuantity());
        verify(productRepository, times(1)).findById(1);
        verify(loteRepository, times(1)).save(any(Lote.class));
    }

    @Test
    void saveLote_WhenProductDoesNotExist_ShouldThrowException() {
        // Arrange
        when(productRepository.findById(999)).thenReturn(Optional.empty());
        testLoteDto.setProductId(999);

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> {
            loteService.saveLote(testLoteDto);
        });
        verify(productRepository, times(1)).findById(999);
        verify(loteRepository, never()).save(any(Lote.class));
    }
}

