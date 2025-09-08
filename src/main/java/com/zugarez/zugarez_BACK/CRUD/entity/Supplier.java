package com.zugarez.zugarez_BACK.CRUD.entity;

import jakarta.persistence.*;

/**
 * Entity representing a supplier in the system.
 * Contains supplier details such as name, email, and address.
 */
@Entity
@Table(name = "supplier")
public class Supplier {
    /** Supplier ID (Primary Key) */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id_supplier;
    /** Supplier name */
    private String name;
    /** Supplier email */
    private String email;
    /** Supplier address */
    private String address;

    /**
     * Gets the supplier ID.
     * @return Supplier ID
     */
    public Integer getId_supplier() {
        return id_supplier;
    }

    /**
     * Sets the supplier ID.
     * @param id_supplier Supplier ID
     */
    public void setId_supplier(Integer id_supplier) {
        this.id_supplier = id_supplier;
    }

    /**
     * Gets the supplier name.
     * @return Supplier name
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the supplier name.
     * @param name Supplier name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Gets the supplier email.
     * @return Supplier email
     */
    public String getEmail() {
        return email;
    }

    /**
     * Sets the supplier email.
     * @param email Supplier email
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * Gets the supplier address.
     * @return Supplier address
     */
    public String getAddress() {
        return address;
    }

    /**
     * Sets the supplier address.
     * @param address Supplier address
     */
    public void setAddress(String address) {
        this.address = address;
    }
}
