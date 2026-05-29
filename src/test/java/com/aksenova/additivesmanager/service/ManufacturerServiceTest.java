package com.aksenova.additivesmanager.service;

import com.aksenova.additivesmanager.dto.ManufacturerDto;
import com.aksenova.additivesmanager.entity.Manufacturer;
import com.aksenova.additivesmanager.repository.ManufacturerRepository;
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
class ManufacturerServiceTest {

    @Mock
    private ManufacturerRepository repo;

    @InjectMocks
    private ManufacturerService service;

    @Test
    void create_validDto_returnsSaved() {
        var dto = new ManufacturerDto();
        dto.setName("ООО Тест");
        dto.setCountry("Россия");

        var saved = new Manufacturer();
        saved.setId(1);
        saved.setName("ООО Тест");
        saved.setCountry("Россия");

        when(repo.save(any())).thenReturn(saved);

        var result = service.createManufacturer(dto);

        assertThat(result.getId()).isEqualTo(1);
        assertThat(result.getName()).isEqualTo("ООО Тест");
    }

    @Test
    void update_notFound_throwsException() {
        when(repo.findById(99)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.updateManufacturer(99, new ManufacturerDto()))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("not found");
    }

    @Test
    void update_existing_updatesFields() {
        var existing = new Manufacturer();
        existing.setId(1);
        existing.setName("Old Name");

        var dto = new ManufacturerDto();
        dto.setName("New Name");
        dto.setCountry("США");

        when(repo.findById(1)).thenReturn(Optional.of(existing));
        when(repo.save(any())).thenAnswer(inv -> inv.getArgument(0));

        var result = service.updateManufacturer(1, dto);

        assertThat(result.getName()).isEqualTo("New Name");
        assertThat(result.getCountry()).isEqualTo("США");
    }

    @Test
    void getAll_returnsList() {
        when(repo.findAll()).thenReturn(List.of(new Manufacturer(), new Manufacturer(), new Manufacturer()));

        assertThat(service.getAllManufacturers()).hasSize(3);
    }

    @Test
    void delete_callsRepository() {
        service.deleteManufacturer(7);
        verify(repo).deleteById(7);
    }
}
