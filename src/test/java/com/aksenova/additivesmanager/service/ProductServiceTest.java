package com.aksenova.additivesmanager.service;

import com.aksenova.additivesmanager.dto.ProductDto;
import com.aksenova.additivesmanager.entity.*;
import com.aksenova.additivesmanager.repository.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @Mock private ProductRepository productRepo;
    @Mock private ManufacturerRepository manufacturerRepo;
    @Mock private ProductTypeRepository typeRepo;
    @Mock private ProductStatusRepository statusRepo;

    @InjectMocks
    private ProductService service;

    @Test
    void create_validDto_returnsSaved() {
        var dto = buildDto();

        var type = new ProductType(); type.setId(1);
        var status = new ProductStatus(); status.setId(1);
        var saved = new Product(); saved.setId(10); saved.setProductName("Е100");

        when(typeRepo.findById(1)).thenReturn(Optional.of(type));
        when(statusRepo.findById(1)).thenReturn(Optional.of(status));
        when(productRepo.save(any())).thenReturn(saved);

        var result = service.createProduct(dto);

        assertThat(result.getId()).isEqualTo(10);
        assertThat(result.getProductName()).isEqualTo("Е100");
    }

    @Test
    void create_typeNotFound_throwsException() {
        var dto = buildDto();
        when(typeRepo.findById(1)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.createProduct(dto))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("not found");
    }

    @Test
    void update_productNotFound_throwsException() {
        when(productRepo.findById(99)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.updateProduct(99, buildDto()))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("not found");
    }

    @Test
    void getAll_returnsList() {
        when(productRepo.findAll()).thenReturn(List.of(new Product(), new Product()));

        assertThat(service.getAllProducts()).hasSize(2);
    }

    @Test
    void delete_callsRepository() {
        service.deleteProduct(4);
        verify(productRepo).deleteById(4);
    }

    private ProductDto buildDto() {
        var dto = new ProductDto();
        dto.setProductName("Е100");
        dto.setProductTypeId(1);
        dto.setStatusId(1);
        return dto;
    }
}
