package com.aksenova.additivesmanager.service;

import com.aksenova.additivesmanager.dto.ProductTypeDto;
import com.aksenova.additivesmanager.entity.ProductType;
import com.aksenova.additivesmanager.repository.ProductTypeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProductTypeService {

    private final ProductTypeRepository productTypeRepository;

    @Transactional
    public ProductType createProductType(ProductTypeDto dto) {
        ProductType productType = new ProductType();
        productType.setTypeName(dto.getTypeName());
        productType.setDescription(dto.getDescription());
        return productTypeRepository.save(productType);
    }

    @Transactional
    public ProductType updateProductType(Integer id, ProductTypeDto dto) {
        ProductType existing = productTypeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product type not found with id: " + id));
        existing.setTypeName(dto.getTypeName());
        existing.setDescription(dto.getDescription());
        return productTypeRepository.save(existing);
    }

    @Transactional
    public void deleteProductType(Integer id) {
        productTypeRepository.deleteById(id);
    }

    public Optional<ProductType> getProductTypeById(Integer id) {
        return productTypeRepository.findById(id);
    }

    public List<ProductType> getAllProductTypes() {
        return productTypeRepository.findAll();
    }
}
