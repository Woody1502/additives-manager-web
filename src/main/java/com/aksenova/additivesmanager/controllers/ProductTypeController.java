package com.aksenova.additivesmanager.controllers;

import com.aksenova.additivesmanager.dto.ProductTypeDto;
import com.aksenova.additivesmanager.entity.ProductType;
import com.aksenova.additivesmanager.service.ProductTypeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "http://localhost:4200")
@RestController
@RequestMapping("/api/product-types")
@RequiredArgsConstructor
public class ProductTypeController {

    private final ProductTypeService productTypeService;

    @GetMapping
    public ResponseEntity<List<ProductType>> getAll() {
        return ResponseEntity.ok(productTypeService.getAllProductTypes());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductType> getById(@PathVariable Integer id) {
        return productTypeService.getProductTypeById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<ProductType> create(@Valid @RequestBody ProductTypeDto dto) {
        return ResponseEntity.ok(productTypeService.createProductType(dto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProductType> update(@PathVariable Integer id,
                                              @Valid @RequestBody ProductTypeDto dto) {
        return ResponseEntity.ok(productTypeService.updateProductType(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        productTypeService.deleteProductType(id);
        return ResponseEntity.noContent().build();
    }
}
