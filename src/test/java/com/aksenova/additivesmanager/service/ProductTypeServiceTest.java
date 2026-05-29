package com.aksenova.additivesmanager.service;

import com.aksenova.additivesmanager.dto.ProductTypeDto;
import com.aksenova.additivesmanager.entity.ProductType;
import com.aksenova.additivesmanager.repository.ProductTypeRepository;
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
class ProductTypeServiceTest {

    @Mock
    private ProductTypeRepository repo;

    @InjectMocks
    private ProductTypeService service;

    @Test
    void create_validDto_returnsSaved() {
        var dto = new ProductTypeDto();
        dto.setTypeName("БАД");
        dto.setDescription("Описание");

        var saved = new ProductType();
        saved.setId(1);
        saved.setTypeName("БАД");

        when(repo.save(any())).thenReturn(saved);

        var result = service.createProductType(dto);

        assertThat(result.getId()).isEqualTo(1);
        assertThat(result.getTypeName()).isEqualTo("БАД");
        verify(repo).save(any());
    }

    @Test
    void update_notFound_throwsException() {
        when(repo.findById(99)).thenReturn(Optional.empty());

        var dto = new ProductTypeDto();
        dto.setTypeName("Test");

        assertThatThrownBy(() -> service.updateProductType(99, dto))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("not found");
    }

    @Test
    void update_existing_updatesFields() {
        var existing = new ProductType();
        existing.setId(1);
        existing.setTypeName("Old");

        var dto = new ProductTypeDto();
        dto.setTypeName("New");
        dto.setDescription("Desc");

        when(repo.findById(1)).thenReturn(Optional.of(existing));
        when(repo.save(any())).thenAnswer(inv -> inv.getArgument(0));

        var result = service.updateProductType(1, dto);

        assertThat(result.getTypeName()).isEqualTo("New");
    }

    @Test
    void getAll_returnsList() {
        when(repo.findAll()).thenReturn(List.of(new ProductType(), new ProductType()));

        assertThat(service.getAllProductTypes()).hasSize(2);
    }

    @Test
    void delete_callsRepository() {
        service.deleteProductType(5);
        verify(repo).deleteById(5);
    }
}
