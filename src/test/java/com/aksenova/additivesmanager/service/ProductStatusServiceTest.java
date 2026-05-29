package com.aksenova.additivesmanager.service;

import com.aksenova.additivesmanager.dto.ProductStatusDto;
import com.aksenova.additivesmanager.entity.ProductStatus;
import com.aksenova.additivesmanager.repository.ProductStatusRepository;
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
class ProductStatusServiceTest {

    @Mock
    private ProductStatusRepository repo;

    @InjectMocks
    private ProductStatusService service;

    @Test
    void create_validDto_returnsSaved() {
        var dto = new ProductStatusDto();
        dto.setStatusName("действует");

        var saved = new ProductStatus();
        saved.setId(1);
        saved.setStatusName("действует");

        when(repo.save(any())).thenReturn(saved);

        var result = service.createProductStatus(dto);

        assertThat(result.getId()).isEqualTo(1);
        assertThat(result.getStatusName()).isEqualTo("действует");
    }

    @Test
    void update_notFound_throwsException() {
        when(repo.findById(99)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.updateProductStatus(99, new ProductStatusDto()))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("not found");
    }

    @Test
    void update_existing_updatesFields() {
        var existing = new ProductStatus();
        existing.setId(1);
        existing.setStatusName("Old");

        var dto = new ProductStatusDto();
        dto.setStatusName("New");

        when(repo.findById(1)).thenReturn(Optional.of(existing));
        when(repo.save(any())).thenAnswer(inv -> inv.getArgument(0));

        var result = service.updateProductStatus(1, dto);

        assertThat(result.getStatusName()).isEqualTo("New");
    }

    @Test
    void getAll_returnsList() {
        when(repo.findAll()).thenReturn(List.of(new ProductStatus(), new ProductStatus()));

        assertThat(service.getAllProductStatuses()).hasSize(2);
    }

    @Test
    void delete_callsRepository() {
        service.deleteProductStatus(3);
        verify(repo).deleteById(3);
    }
}
