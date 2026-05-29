package com.aksenova.additivesmanager.service;

import com.aksenova.additivesmanager.dto.ProductDto;
import com.aksenova.additivesmanager.entity.Manufacturer;
import com.aksenova.additivesmanager.entity.Product;
import com.aksenova.additivesmanager.entity.ProductStatus;
import com.aksenova.additivesmanager.entity.ProductType;
import com.aksenova.additivesmanager.repository.ManufacturerRepository;
import com.aksenova.additivesmanager.repository.ProductRepository;
import com.aksenova.additivesmanager.repository.ProductStatusRepository;
import com.aksenova.additivesmanager.repository.ProductTypeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProductService {

    private final ProductRepository productRepository;
    private final ManufacturerRepository manufacturerRepository;
    private final ProductTypeRepository productTypeRepository;
    private final ProductStatusRepository productStatusRepository;

    @Transactional
    public Product createProduct(ProductDto dto) {
        Product product = new Product();
        mapDtoToProduct(dto, product);
        return productRepository.save(product);
    }

    @Transactional
    public Product updateProduct(Integer id, ProductDto dto) {
        Product existing = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found with id: " + id));
        mapDtoToProduct(dto, existing);
        return productRepository.save(existing);
    }

    @Transactional
    public void deleteProduct(Integer id) {
        productRepository.deleteById(id);
    }

    public Optional<Product> getProductById(Integer id) {
        return productRepository.findById(id);
    }

    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    private void mapDtoToProduct(ProductDto dto, Product product) {
        ProductType type = productTypeRepository.findById(dto.getProductTypeId())
                .orElseThrow(() -> new RuntimeException("Product type not found with id: " + dto.getProductTypeId()));
        ProductStatus status = productStatusRepository.findById(dto.getStatusId())
                .orElseThrow(() -> new RuntimeException("Product status not found with id: " + dto.getStatusId()));

        product.setProductName(dto.getProductName());
        product.setProductType(type);
        product.setStatus(status);
        product.setTnVedCode(dto.getTnVedCode());
        product.setENumber(dto.getENumber());
        product.setReleaseForm(dto.getReleaseForm());
        product.setSgrNumber(dto.getSgrNumber());
        product.setSgrRegistrationDate(dto.getSgrRegistrationDate());
        product.setShelfLifeMonths(dto.getShelfLifeMonths());
        product.setStorageConditions(dto.getStorageConditions());

        product.getManufacturers().clear();
        if (dto.getManufacturerIds() != null) {
            for (Integer mId : dto.getManufacturerIds()) {
                Manufacturer m = manufacturerRepository.findById(mId)
                        .orElseThrow(() -> new RuntimeException("Manufacturer not found with id: " + mId));
                product.getManufacturers().add(m);
            }
        }
    }
}
