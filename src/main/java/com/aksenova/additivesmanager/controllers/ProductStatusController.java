package com.aksenova.additivesmanager.controllers;

import com.aksenova.additivesmanager.dto.ProductStatusDto;
import com.aksenova.additivesmanager.entity.ProductStatus;
import com.aksenova.additivesmanager.service.ProductStatusService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "http://localhost:4200")
@RestController
@RequestMapping("/api/product-statuses")
@RequiredArgsConstructor
public class ProductStatusController {

    private final ProductStatusService productStatusService;

    @GetMapping
    public ResponseEntity<List<ProductStatus>> getAll() {
        return ResponseEntity.ok(productStatusService.getAllProductStatuses());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductStatus> getById(@PathVariable Integer id) {
        return productStatusService.getProductStatusById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<ProductStatus> create(@Valid @RequestBody ProductStatusDto dto) {
        return ResponseEntity.ok(productStatusService.createProductStatus(dto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProductStatus> update(@PathVariable Integer id,
                                                @Valid @RequestBody ProductStatusDto dto) {
        return ResponseEntity.ok(productStatusService.updateProductStatus(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        productStatusService.deleteProductStatus(id);
        return ResponseEntity.noContent().build();
    }
}
