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

@RestController
@RequestMapping("/inventory/lotes")
public class LoteController {

    @Autowired
    private LoteService loteService;

    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    @GetMapping
    public ResponseEntity<List<Lote>> getAllLotes() {
        return ResponseEntity.ok(loteService.getAllLotes());
    }

    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    @GetMapping("/{id}")
    public ResponseEntity<Lote> getLoteById(@PathVariable("id") int id) throws ResourceNotFoundException {
        return ResponseEntity.ok(loteService.getLoteById(id));
    }

    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    @GetMapping("/product/{productId}")
    public ResponseEntity<List<Lote>> getLotesByProduct(@PathVariable("productId") int productId) {
        return ResponseEntity.ok(loteService.getLotesByProduct(productId));
    }

    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    @GetMapping("/available")
    public ResponseEntity<List<Lote>> getAvailableLotes() {
        return ResponseEntity.ok(loteService.getAvailableLotes());
    }

    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    @GetMapping("/expiring/{days}")
    public ResponseEntity<List<Lote>> getLotesProximosAVencer(@PathVariable("days") int days) {
        return ResponseEntity.ok(loteService.getLotesProximosAVencer(days));
    }

    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    @GetMapping("/stock/{productId}")
    public ResponseEntity<Integer> getStockByProduct(@PathVariable("productId") int productId) {
        return ResponseEntity.ok(loteService.getStockTotalByProduct(productId));
    }

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
        ActualizarStockProducto(dto.getProductId() , dto.getInitialQuantity());
        //so
        
        System.out.println("🔍 DEBUG LoteController - Lote creado con ID: " + lote.getId());
        
        String message = "Lote #" + lote.getId() + " creado correctamente";
        return ResponseEntity.ok(new MessageDto(HttpStatus.OK, message));
    }

    private void ActualizarStockProducto(int productId, int cantidadNueva) throws ResourceNotFoundException {
        int stockActual = loteService.getStockTotalByProduct(productId);
        int nuevoStock = stockActual + cantidadNueva;
        loteService.updateProductStock(productId, nuevoStock);
        System.out.println("🔍 DEBUG LoteController - Stock del producto ID " + productId + " actualizado a: " + nuevoStock);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<MessageDto> updateLote(@PathVariable("id") int id, @Valid @RequestBody LoteDto dto) 
            throws ResourceNotFoundException, AttributeException {
        Lote lote = loteService.updateLote(id, dto);
        String message = "Lote #" + lote.getId() + " actualizado correctamente";
        return ResponseEntity.ok(new MessageDto(HttpStatus.OK, message));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<MessageDto> deleteLote(@PathVariable("id") int id) throws ResourceNotFoundException {
        Lote lote = loteService.deleteLote(id);
        String message = "Lote #" + lote.getId() + " eliminado correctamente";
        return ResponseEntity.ok(new MessageDto(HttpStatus.OK, message));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/test")
    public ResponseEntity<Lote> createLoteAndReturn(@Valid @RequestBody LoteDto dto) 
            throws ResourceNotFoundException, AttributeException {
        Lote lote = loteService.saveLote(dto);
        return ResponseEntity.ok(lote);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}/reduce-stock")
    public ResponseEntity<MessageDto> reduceStock(@PathVariable("id") int id, @RequestParam("quantity") int quantity) 
            throws ResourceNotFoundException, AttributeException {
        loteService.reducirStock(id, quantity);
        String message = "Stock reducido correctamente. Cantidad: " + quantity;
        return ResponseEntity.ok(new MessageDto(HttpStatus.OK, message));
    }
}
