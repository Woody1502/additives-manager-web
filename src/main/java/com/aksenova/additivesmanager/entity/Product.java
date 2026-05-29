package com.aksenova.additivesmanager.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "products")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "product_name", nullable = false, length = 255)
    private String productName;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "product_type_id", nullable = false)
    private ProductType productType;

    @Column(name = "tn_ved_code", length = 50)
    private String tnVedCode;

    @Column(name = "e_number", length = 20)
    private String eNumber;

    @Column(name = "release_form", length = 100)
    private String releaseForm;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "product_manufacturer",
            joinColumns = @JoinColumn(name = "product_id"),
            inverseJoinColumns = @JoinColumn(name = "manufacturer_id")
    )
    private List<Manufacturer> manufacturers = new ArrayList<>();

    @Column(name = "sgr_number", length = 100)
    private String sgrNumber;

    @JsonFormat(pattern = "yyyy-MM-dd")
    @Column(name = "sgr_registration_date")
    private LocalDate sgrRegistrationDate;

    @Column(name = "shelf_life_months")
    private Integer shelfLifeMonths;

    @Column(name = "storage_conditions", columnDefinition = "TEXT")
    private String storageConditions;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "status_id", nullable = false)
    private ProductStatus status;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;


    /**
     * Вспомогательный метод для проверки, является ли продукт пищевой добавкой
     */
    @Transient
    public boolean isFoodAdditive() {
        return productType != null && "Пищевая добавка".equals(productType.getTypeName());
    }

    /**
     * Вспомогательный метод для получения даты истечения СГР (через 5 лет после регистрации)
     */
    @Transient
    public LocalDate getSgrExpirationDate() {
        return sgrRegistrationDate != null ? sgrRegistrationDate.plusYears(5) : null;
    }
}