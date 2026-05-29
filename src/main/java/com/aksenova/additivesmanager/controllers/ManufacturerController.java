package com.aksenova.additivesmanager.controllers;

import com.aksenova.additivesmanager.dto.ManufacturerDto;
import com.aksenova.additivesmanager.entity.Manufacturer;
import com.aksenova.additivesmanager.service.ManufacturerService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "http://localhost:4200")
@RestController
@RequestMapping("/api/manufacturers")
@RequiredArgsConstructor
public class ManufacturerController {

    private final ManufacturerService manufacturerService;

    @GetMapping
    public ResponseEntity<List<Manufacturer>> getAll() {
        return ResponseEntity.ok(manufacturerService.getAllManufacturers());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Manufacturer> getById(@PathVariable Integer id) {
        return manufacturerService.getManufacturerById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Manufacturer> create(@Valid @RequestBody ManufacturerDto dto) {
        return ResponseEntity.ok(manufacturerService.createManufacturer(dto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Manufacturer> update(@PathVariable Integer id,
                                               @Valid @RequestBody ManufacturerDto dto) {
        return ResponseEntity.ok(manufacturerService.updateManufacturer(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        manufacturerService.deleteManufacturer(id);
        return ResponseEntity.noContent().build();
    }
}
