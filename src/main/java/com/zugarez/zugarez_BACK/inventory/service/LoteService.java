package com.zugarez.zugarez_BACK.inventory.service;

import com.zugarez.zugarez_BACK.CRUD.entity.Product;
import com.zugarez.zugarez_BACK.CRUD.repository.ProductRepository;
import com.zugarez.zugarez_BACK.global.exceptions.AttributeException;
import com.zugarez.zugarez_BACK.global.exceptions.ResourceNotFoundException;
import com.zugarez.zugarez_BACK.inventory.dto.LoteDto;
import com.zugarez.zugarez_BACK.inventory.entity.Lote;
import com.zugarez.zugarez_BACK.inventory.repository.LoteRepository;
// import io.micrometer.core.instrument.Counter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

/**
 * Service class for managing inventory batches (lotes).
 * Provides business logic for CRUD operations and stock management on Lote entities.
 */
@Service
public class LoteService {

    @Autowired
    private LoteRepository loteRepository;

    @Autowired
    private ProductRepository productRepository;

    // @Autowired
    // private Counter loteCreatedCounter;

    /**
     * Retrieves all batches (lotes) ordered by expiration date.
     * @return List of all batches
     */
    public List<Lote> getAllLotes() {
        System.out.println("🔍 DEBUG LoteService - Obteniendo todos los lotes");
        List<Lote> lotes = loteRepository.findAllOrderByExpirationDate();
        System.out.println("🔍 DEBUG LoteService - Lotes encontrados: " + lotes.size());
        return lotes;
    }

    /**
     * Retrieves a batch by its ID.
     * @param id Batch ID
     * @return Lote with the given ID
     * @throws ResourceNotFoundException if the batch is not found
     */
    public Lote getLoteById(int id) throws ResourceNotFoundException {
        return loteRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Lote no encontrado"));
    }

    /**
     * Retrieves all batches for a given product.
     * @param productId Product ID
     * @return List of batches for the product
     */
    public List<Lote> getLotesByProduct(int productId) {
        return loteRepository.findByProductId(productId);
    }

    /**
     * Retrieves all available batches (with available quantity > 0).
     * @return List of available batches
     */
    public List<Lote> getAvailableLotes() {
        return loteRepository.findByAvailableQuantityGreaterThan(0);
    }

    /**
     * Retrieves batches that are about to expire within a given number of days.
     * @param dias Number of days until expiration
     * @return List of batches expiring soon
     */
    public List<Lote> getLotesProximosAVencer(int dias) {
        LocalDate fechaLimite = LocalDate.now().plusDays(dias);
        return loteRepository.findByExpirationDateBefore(fechaLimite);
    }

    /**
     * Gets the total available stock for a product.
     * @param productId Product ID
     * @return Total available quantity
     */
    public int getStockTotalByProduct(int productId) {
        return loteRepository.getTotalAvailableQuantityByProduct(productId);
    }

    /**
     * Saves a new batch (lote) to the database.
     * @param dto LoteDto containing batch data
     * @return The saved Lote entity
     * @throws ResourceNotFoundException if the product is not found
     * @throws AttributeException if validation fails
     */
    public Lote saveLote(LoteDto dto) throws ResourceNotFoundException, AttributeException {
        System.out.println("🔍 DEBUG LoteService - Creando lote para producto ID: " + dto.getProductId());
        
        // Validar que el producto existe
        Product product = productRepository.findById(dto.getProductId())
                .orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado"));

        // Validar fechas
        if (dto.getExpirationDate().isBefore(LocalDate.now())) {
            throw new AttributeException("La fecha de vencimiento no puede ser anterior a hoy");
        }

        // Validar cantidades
        if (dto.getAvailableQuantity() > dto.getInitialQuantity()) {
            throw new AttributeException("La cantidad disponible no puede ser mayor a la cantidad inicial");
        }

        Lote lote = new Lote();
        lote.setProduct(product);
        lote.setExpirationDate(dto.getExpirationDate());
        lote.setInitialQuantity(dto.getInitialQuantity());
        lote.setAvailableQuantity(dto.getAvailableQuantity());
        lote.setBatchPrice(dto.getBatchPrice());
        lote.setUnitPrice(dto.getUnitPrice());

        System.out.println("🔍 DEBUG LoteService - Guardando lote: " + lote.getId());
        Lote savedLote = loteRepository.save(lote);
        // loteCreatedCounter.increment();
        return savedLote;
    }

    /**
     * Updates an existing batch (lote) in the database.
     * @param id Batch ID
     * @param dto LoteDto containing updated data
     * @return The updated Lote entity
     * @throws ResourceNotFoundException if the batch or product is not found
     * @throws AttributeException if validation fails
     */
    public Lote updateLote(int id, LoteDto dto) throws ResourceNotFoundException, AttributeException {
        Lote lote = loteRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Lote no encontrado"));

        // Validar que el producto existe
        Product product = productRepository.findById(dto.getProductId())
                .orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado"));

        // Validar fechas
        if (dto.getExpirationDate().isBefore(LocalDate.now())) {
            throw new AttributeException("La fecha de vencimiento no puede ser anterior a hoy");
        }

        // Validar cantidades
        if (dto.getAvailableQuantity() > dto.getInitialQuantity()) {
            throw new AttributeException("La cantidad disponible no puede ser mayor a la cantidad inicial");
        }

        lote.setProduct(product);
        lote.setExpirationDate(dto.getExpirationDate());
        lote.setInitialQuantity(dto.getInitialQuantity());
        lote.setAvailableQuantity(dto.getAvailableQuantity());
        lote.setBatchPrice(dto.getBatchPrice());
        lote.setUnitPrice(dto.getUnitPrice());

        return loteRepository.save(lote);
    }

    /**
     * Deletes a batch (lote) from the database.
     * @param id Batch ID
     * @return The deleted Lote entity
     * @throws ResourceNotFoundException if the batch is not found
     */
    public Lote deleteLote(int id) throws ResourceNotFoundException {
        Lote lote = loteRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Lote no encontrado"));
        loteRepository.delete(lote);
        return lote;
    }

    /**
     * Reduces the available stock of a batch when a sale is made.
     * @param loteId Batch ID
     * @param cantidad Quantity to reduce
     * @return true if stock was reduced successfully
     * @throws ResourceNotFoundException if the batch is not found
     * @throws AttributeException if there is not enough stock
     */
    public boolean reducirStock(int loteId, int cantidad) throws ResourceNotFoundException, AttributeException {
        Lote lote = loteRepository.findById(loteId)
                .orElseThrow(() -> new ResourceNotFoundException("Lote no encontrado"));

        if (lote.getAvailableQuantity() < cantidad) {
            throw new AttributeException("No hay suficiente stock disponible. Stock actual: " + lote.getAvailableQuantity());
        }

        lote.setAvailableQuantity(lote.getAvailableQuantity() - cantidad);
        loteRepository.save(lote);
        return true;
    }
}
