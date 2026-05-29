package com.aksenova.additivesmanager.service;

import com.aksenova.additivesmanager.dto.ManufacturerDto;
import com.aksenova.additivesmanager.entity.Manufacturer;
import com.aksenova.additivesmanager.repository.ManufacturerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ManufacturerService {

    private final ManufacturerRepository manufacturerRepository;

    @Transactional
    public Manufacturer createManufacturer(ManufacturerDto dto) {
        Manufacturer manufacturer = new Manufacturer();
        mapDtoToManufacturer(dto, manufacturer);
        return manufacturerRepository.save(manufacturer);
    }

    @Transactional
    public Manufacturer updateManufacturer(Integer id, ManufacturerDto dto) {
        Manufacturer existing = manufacturerRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Manufacturer not found with id: " + id));
        mapDtoToManufacturer(dto, existing);
        return manufacturerRepository.save(existing);
    }

    @Transactional
    public void deleteManufacturer(Integer id) {
        manufacturerRepository.deleteById(id);
    }

    public Optional<Manufacturer> getManufacturerById(Integer id) {
        return manufacturerRepository.findById(id);
    }

    public List<Manufacturer> getAllManufacturers() {
        return manufacturerRepository.findAll();
    }

    private void mapDtoToManufacturer(ManufacturerDto dto, Manufacturer manufacturer) {
        manufacturer.setName(dto.getName());
        manufacturer.setCountry(dto.getCountry());
        manufacturer.setLegalAddress(dto.getLegalAddress());
        manufacturer.setInn(dto.getInn());
        manufacturer.setOgrn(dto.getOgrn());
        manufacturer.setContactPhone(dto.getContactPhone());
        manufacturer.setContactEmail(dto.getContactEmail());
        manufacturer.setWebsite(dto.getWebsite());
    }
}
