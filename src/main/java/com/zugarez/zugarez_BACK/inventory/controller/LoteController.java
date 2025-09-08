package com.zugarez.zugarez_BACK.inventory.controller;

import com.zugarez.zugarez_BACK.global.dto.MessageDto;
import com.zugarez.zugarez_BACK.global.exceptions.AttributeException;
import com.zugarez.zugarez_BACK.global.exceptions.ResourceNotFoundException;
import com.zugarez.zugarez_BACK.inventory.dto.LoteDto;
import com.zugarez.zugarez_BACK.inventory.entity.Lote;
import com.zugarez.zugarez_BACK.inventory.service.LoteService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST controller for managing inventory batches (lotes).
 * Provides endpoints for CRUD operations and stock management on Lote entities.
 */
@RestController
@RequestMapping("/inventory/lotes")
public class LoteController {

    @Autowired
    private LoteService loteService;

    /**
     * Retrieves all batches (lotes).
     * @return List of all batches
     */
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    @GetMapping
    public ResponseEntity<List<Lote>> getAllLotes() {
        return ResponseEntity.ok(loteService.getAllLotes());
    }

    /**
     * Retrieves a batch by its ID.
     * @param id Batch ID
     * @return The batch with the given ID
     * @throws ResourceNotFoundException if the batch is not found
     */
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    @GetMapping("/{id}")
    public ResponseEntity<Lote> getLoteById(@PathVariable("id") int id) throws ResourceNotFoundException {
        return ResponseEntity.ok(loteService.getLoteById(id));
    }

    /**
     * Retrieves all batches for a given product.
     * @param productId Product ID
     * @return List of batches for the product
     */
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    @GetMapping("/product/{productId}")
    public ResponseEntity<List<Lote>> getLotesByProduct(@PathVariable("productId") int productId) {
        return ResponseEntity.ok(loteService.getLotesByProduct(productId));
    }

    /**
     * Retrieves all available batches (with available quantity > 0).
     * @return List of available batches
     */
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    @GetMapping("/available")
    public ResponseEntity<List<Lote>> getAvailableLotes() {
        return ResponseEntity.ok(loteService.getAvailableLotes());
    }

    /**
     * Retrieves batches that are about to expire within a given number of days.
     * @param days Number of days until expiration
     * @return List of batches expiring soon
     */
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    @GetMapping("/expiring/{days}")
    public ResponseEntity<List<Lote>> getLotesProximosAVencer(@PathVariable("days") int days) {
        return ResponseEntity.ok(loteService.getLotesProximosAVencer(days));
    }

    /**
     * Gets the total available stock for a product.
     * @param productId Product ID
     * @return Total available quantity
     */
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    @GetMapping("/stock/{productId}")
    public ResponseEntity<Integer> getStockByProduct(@PathVariable("productId") int productId) {
        return ResponseEntity.ok(loteService.getStockTotalByProduct(productId));
    }

    /**
     * Creates a new batch (lote).
     * @param dto LoteDto with batch data
     * @return MessageDto with creation status
     * @throws ResourceNotFoundException if the product is not found
     * @throws AttributeException if validation fails
     */
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<MessageDto> createLote(@Valid @RequestBody LoteDto dto) throws ResourceNotFoundException, AttributeException {
        System.out.println("🔍 DEBUG LoteController - Datos recibidos:");
        System.out.println("ProductId: " + dto.getProductId());
        System.out.println("ExpirationDate: " + dto.getExpirationDate());
        System.out.println("InitialQuantity: " + dto.getInitialQuantity());
        System.out.println("AvailableQuantity: " + dto.getAvailableQuantity());
        System.out.println("BatchPrice: " + dto.getBatchPrice());
        System.out.println("UnitPrice: " + dto.getUnitPrice());
        
        Lote lote = loteService.saveLote(dto);
        
        System.out.println("🔍 DEBUG LoteController - Lote creado con ID: " + lote.getId());
        
        String message = "Lote #" + lote.getId() + " creado correctamente";
        return ResponseEntity.ok(new MessageDto(HttpStatus.OK, message));
    }

    /**
     * Updates an existing batch (lote).
     * @param id Batch ID
     * @param dto LoteDto with updated data
     * @return MessageDto with update status
     * @throws ResourceNotFoundException if the batch or product is not found
     * @throws AttributeException if validation fails
     */
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<MessageDto> updateLote(@PathVariable("id") int id, @Valid @RequestBody LoteDto dto) 
            throws ResourceNotFoundException, AttributeException {
        Lote lote = loteService.updateLote(id, dto);
        String message = "Lote #" + lote.getId() + " actualizado correctamente";
        return ResponseEntity.ok(new MessageDto(HttpStatus.OK, message));
    }

    /**
     * Deletes a batch (lote) by its ID.
     * @param id Batch ID
     * @return MessageDto with deletion status
     * @throws ResourceNotFoundException if the batch is not found
     */
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<MessageDto> deleteLote(@PathVariable("id") int id) throws ResourceNotFoundException {
        Lote lote = loteService.deleteLote(id);
        String message = "Lote #" + lote.getId() + " eliminado correctamente";
        return ResponseEntity.ok(new MessageDto(HttpStatus.OK, message));
    }

    /**
     * Testing endpoint: creates a batch and returns the full Lote entity.
     * @param dto LoteDto with batch data
     * @return The created Lote entity
     * @throws ResourceNotFoundException if the product is not found
     * @throws AttributeException if validation fails
     */
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/test")
    public ResponseEntity<Lote> createLoteAndReturn(@Valid @RequestBody LoteDto dto) 
            throws ResourceNotFoundException, AttributeException {
        Lote lote = loteService.saveLote(dto);
        return ResponseEntity.ok(lote);
    }

    /**
     * Reduces the available stock of a batch.
     * @param id Batch ID
     * @param quantity Quantity to reduce
     * @return MessageDto with reduction status
     * @throws ResourceNotFoundException if the batch is not found
     * @throws AttributeException if there is not enough stock
     */
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}/reduce-stock")
    public ResponseEntity<MessageDto> reduceStock(@PathVariable("id") int id, @RequestParam("quantity") int quantity) 
            throws ResourceNotFoundException, AttributeException {
        loteService.reducirStock(id, quantity);
        String message = "Stock reducido correctamente. Cantidad: " + quantity;
        return ResponseEntity.ok(new MessageDto(HttpStatus.OK, message));
    }
}
