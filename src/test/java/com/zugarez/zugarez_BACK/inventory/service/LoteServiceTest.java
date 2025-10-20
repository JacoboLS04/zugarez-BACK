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
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Unit tests for LoteService.
 */
@ExtendWith(MockitoExtension.class)
class LoteServiceTest {

    @Mock
    private LoteRepository loteRepository;

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private LoteService loteService;

    private Lote testLote;
    private LoteDto testLoteDto;
    private Product testProduct;

    @BeforeEach
    void setUp() {
        testProduct = new Product();
        testProduct.setId(1);
        testProduct.setName("Test Product");
        testProduct.setPrice(99.99);

        testLote = new Lote();
        testLote.setId(1);
        testLote.setProduct(testProduct);
        testLote.setExpirationDate(LocalDate.now().plusMonths(6));
        testLote.setInitialQuantity(100);
        testLote.setAvailableQuantity(80);
        testLote.setBatchPrice(1000.0);
        testLote.setUnitPrice(10.0);

        testLoteDto = new LoteDto();
        testLoteDto.setProductId(1);
        testLoteDto.setExpirationDate(LocalDate.now().plusMonths(6));
        testLoteDto.setInitialQuantity(100);
        testLoteDto.setAvailableQuantity(80);
        testLoteDto.setBatchPrice(1000.0);
        testLoteDto.setUnitPrice(10.0);
    }

    @Test
    void testGetAllLotes() {
        List<Lote> lotes = Collections.singletonList(testLote);
        when(loteRepository.findAllOrderByExpirationDate()).thenReturn(lotes);

        List<Lote> result = loteService.getAllLotes();

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(loteRepository, times(1)).findAllOrderByExpirationDate();
    }

    @Test
    void testGetLoteById_Success() throws ResourceNotFoundException {
        when(loteRepository.findById(1)).thenReturn(Optional.of(testLote));

        Lote result = loteService.getLoteById(1);

        assertNotNull(result);
        assertEquals(1, result.getId());
        verify(loteRepository, times(1)).findById(1);
    }

    @Test
    void testGetLoteById_NotFound() {
        when(loteRepository.findById(999)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> {
            loteService.getLoteById(999);
        });
    }

    @Test
    void testGetLotesByProduct() {
        List<Lote> lotes = Collections.singletonList(testLote);
        when(loteRepository.findByProductId(1)).thenReturn(lotes);

        List<Lote> result = loteService.getLotesByProduct(1);

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(loteRepository, times(1)).findByProductId(1);
    }

    @Test
    void testGetAvailableLotes() {
        List<Lote> lotes = Collections.singletonList(testLote);
        when(loteRepository.findByAvailableQuantityGreaterThan(0)).thenReturn(lotes);

        List<Lote> result = loteService.getAvailableLotes();

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(loteRepository, times(1)).findByAvailableQuantityGreaterThan(0);
    }

    @Test
    void testGetLotesProximosAVencer() {
        List<Lote> lotes = Collections.singletonList(testLote);
        when(loteRepository.findByExpirationDateBefore(any(LocalDate.class))).thenReturn(lotes);

        List<Lote> result = loteService.getLotesProximosAVencer(30);

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(loteRepository, times(1)).findByExpirationDateBefore(any(LocalDate.class));
    }

    @Test
    void testGetStockTotalByProduct() {
        when(loteRepository.getTotalAvailableQuantityByProduct(1)).thenReturn(80);

        int result = loteService.getStockTotalByProduct(1);

        assertEquals(80, result);
        verify(loteRepository, times(1)).getTotalAvailableQuantityByProduct(1);
    }

    @Test
    void testSaveLote_Success() throws ResourceNotFoundException, AttributeException {
        when(productRepository.findById(1)).thenReturn(Optional.of(testProduct));
        when(loteRepository.save(any(Lote.class))).thenReturn(testLote);

        Lote result = loteService.saveLote(testLoteDto);

        assertNotNull(result);
        verify(productRepository, times(1)).findById(1);
        verify(loteRepository, times(1)).save(any(Lote.class));
    }

    @Test
    void testSaveLote_ProductNotFound() {
        when(productRepository.findById(999)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> {
            testLoteDto.setProductId(999);
            loteService.saveLote(testLoteDto);
        });
    }

    @Test
    void testSaveLote_ExpiredDate() {
        when(productRepository.findById(1)).thenReturn(Optional.of(testProduct));
        testLoteDto.setExpirationDate(LocalDate.now().minusDays(1));

        assertThrows(AttributeException.class, () -> {
            loteService.saveLote(testLoteDto);
        });
    }

    @Test
    void testSaveLote_InvalidQuantity() {
        when(productRepository.findById(1)).thenReturn(Optional.of(testProduct));
        testLoteDto.setAvailableQuantity(150);
        testLoteDto.setInitialQuantity(100);

        assertThrows(AttributeException.class, () -> {
            loteService.saveLote(testLoteDto);
        });
    }

    @Test
    void testUpdateLote_Success() throws ResourceNotFoundException, AttributeException {
        when(loteRepository.findById(1)).thenReturn(Optional.of(testLote));
        when(productRepository.findById(1)).thenReturn(Optional.of(testProduct));
        when(loteRepository.save(any(Lote.class))).thenReturn(testLote);

        Lote result = loteService.updateLote(1, testLoteDto);

        assertNotNull(result);
        verify(loteRepository, times(1)).findById(1);
        verify(loteRepository, times(1)).save(any(Lote.class));
    }

    // File: `src/test/java/com/zugarez/zugarez_BACK/inventory/service/LoteServiceTest.java`
    @Test
    void testDeleteLote_Success() throws ResourceNotFoundException {
        when(loteRepository.findById(1)).thenReturn(Optional.of(testLote));
        doNothing().when(loteRepository).delete(testLote); // <-- cambiado

        Lote result = loteService.deleteLote(1);

        assertNotNull(result);
        verify(loteRepository, times(1)).findById(1);
        verify(loteRepository, times(1)).delete(testLote); // <-- cambiado
    }
}

