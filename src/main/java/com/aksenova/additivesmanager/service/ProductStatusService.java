package com.aksenova.additivesmanager.service;

import com.aksenova.additivesmanager.dto.ProductStatusDto;
import com.aksenova.additivesmanager.entity.ProductStatus;
import com.aksenova.additivesmanager.repository.ProductStatusRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProductStatusService {

    private final ProductStatusRepository productStatusRepository;

    @Transactional
    public ProductStatus createProductStatus(ProductStatusDto dto) {
        ProductStatus productStatus = new ProductStatus();
        productStatus.setStatusName(dto.getStatusName());
        productStatus.setDescription(dto.getDescription());
        return productStatusRepository.save(productStatus);
    }

    @Transactional
    public ProductStatus updateProductStatus(Integer id, ProductStatusDto dto) {
        ProductStatus existing = productStatusRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product status not found with id: " + id));
        existing.setStatusName(dto.getStatusName());
        existing.setDescription(dto.getDescription());
        return productStatusRepository.save(existing);
    }

    @Transactional
    public void deleteProductStatus(Integer id) {
        productStatusRepository.deleteById(id);
    }

    public Optional<ProductStatus> getProductStatusById(Integer id) {
        return productStatusRepository.findById(id);
    }

    public List<ProductStatus> getAllProductStatuses() {
        return productStatusRepository.findAll();
    }
}
