package com.marckux.stockman.stock.application.services.locations;

import java.util.UUID;

import org.springframework.stereotype.Service;

import com.marckux.stockman.shared.domain.exceptions.InvalidAttributeException;
import com.marckux.stockman.shared.domain.exceptions.ResourceNotFoundException;
import com.marckux.stockman.stock.application.dtos.LocationResponse;
import com.marckux.stockman.stock.application.dtos.UpdateLocationRequest;
import com.marckux.stockman.stock.application.ports.in.usecases.UpdateLocationUseCase;
import com.marckux.stockman.stock.domain.model.Location;
import com.marckux.stockman.stock.domain.ports.out.LocationRepositoryPort;

import lombok.RequiredArgsConstructor;

/**
 * Servicio de aplicación para actualizar Locations.
 */
@Service
@RequiredArgsConstructor
public class UpdateLocation implements UpdateLocationUseCase {

  private final LocationRepositoryPort locationRepository;

  /**
   * Actualiza una Location validando existencia y unicidad de nombre en el Center.
   */
  @Override
  public LocationResponse execute(Input input) {
    UUID id = input.id();
    UpdateLocationRequest request = input.request();

    Location location = locationRepository.findById(id)
      .orElseThrow(() -> new ResourceNotFoundException("Ubicación", id.toString()));

    if (request.name() != null) {
      if (request.name().isBlank()) {
        throw new InvalidAttributeException("El nombre de la ubicación no puede estar vacío");
      }
      if (!request.name().equals(location.getName())) {
        locationRepository.findByCenterIdAndName(location.getCenter().getId(), request.name())
          .filter(existing -> !existing.getId().equals(id))
          .ifPresent(existing -> {
            throw new InvalidAttributeException("El nombre de la ubicación ya está registrado");
          });
      }
    }

    Location updated = location.toBuilder()
      .name(request.name() != null ? request.name() : location.getName())
      .description(request.description() != null ? request.description() : location.getDescription())
      .build();

    Location saved = locationRepository.save(updated);
    return LocationResponse.fromDomain(saved);
  }
}
