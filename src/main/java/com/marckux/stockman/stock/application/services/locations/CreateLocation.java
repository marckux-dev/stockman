package com.marckux.stockman.stock.application.services.locations;

import java.util.UUID;

import org.springframework.stereotype.Service;

import com.marckux.stockman.shared.domain.exceptions.InvalidAttributeException;
import com.marckux.stockman.shared.domain.exceptions.ResourceNotFoundException;
import com.marckux.stockman.stock.application.dtos.CreateLocationRequest;
import com.marckux.stockman.stock.application.dtos.LocationResponse;
import com.marckux.stockman.stock.application.ports.in.usecases.CreateLocationUseCase;
import com.marckux.stockman.stock.domain.model.Center;
import com.marckux.stockman.stock.domain.model.Location;
import com.marckux.stockman.stock.domain.ports.out.CenterRepositoryPort;
import com.marckux.stockman.stock.domain.ports.out.LocationRepositoryPort;

import lombok.RequiredArgsConstructor;

/**
 * Servicio de aplicación para crear Locations.
 */
@Service
@RequiredArgsConstructor
public class CreateLocation implements CreateLocationUseCase {

  private final CenterRepositoryPort centerRepository;
  private final LocationRepositoryPort locationRepository;

  /**
   * Crea una Location validando existencia de Center y unicidad del nombre.
   * La Location se crea siempre con estado activo.
   */
  @Override
  public LocationResponse execute(Input input) {
    UUID centerId = input.centerId();
    CreateLocationRequest request = input.request();

    Center center = centerRepository.findById(centerId)
      .orElseThrow(() -> new ResourceNotFoundException("Centro", centerId.toString()));

    if (locationRepository.existsByCenterIdAndName(centerId, request.name())) {
      throw new InvalidAttributeException("El nombre de la ubicación ya está registrado");
    }

    Location parentLocation = null;
    if (request.parentLocation() != null && !request.parentLocation().isBlank()) {
      UUID parentId = UUID.fromString(request.parentLocation());
      parentLocation = locationRepository.findById(parentId)
        .orElseThrow(() -> new ResourceNotFoundException("Ubicación", parentId.toString()));
      if (!parentLocation.getCenter().getId().equals(centerId)) {
        throw new InvalidAttributeException("La ubicación padre no pertenece al centro indicado");
      }
    }

    Location location = Location.builder()
      .center(center)
      .name(request.name())
      .description(request.description())
      .parentLocation(parentLocation)
      .isActive(true)
      .build();

    Location saved = locationRepository.save(location);
    return LocationResponse.fromDomain(saved);
  }
}
