package com.zugarez.zugarez_BACK.global.entity;

import org.springframework.data.annotation.Id;

/**
 * Abstract base class for entities that have an integer ID.
 * Provides a common ID field and accessor methods for all entity classes.
 */
public abstract class EntityId {

    /**
     * Unique identifier for the entity.
     */
    @Id
    protected int id;

    /**
     * Gets the entity ID.
     * @return the entity ID
     */
    public int getId() {
        return id;
    }

    /**
     * Sets the entity ID.
     * @param id the entity ID
     */
    public void setId(int id) {
        this.id = id;
    }
}
