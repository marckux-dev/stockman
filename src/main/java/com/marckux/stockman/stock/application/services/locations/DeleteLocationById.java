package com.marckux.stockman.stock.application.services.locations;

import java.util.UUID;

import org.springframework.stereotype.Service;

import com.marckux.stockman.shared.domain.exceptions.ResourceNotFoundException;
import com.marckux.stockman.stock.application.ports.in.usecases.DeleteLocationByIdUseCase;
import com.marckux.stockman.stock.domain.ports.out.LocationRepositoryPort;

import lombok.RequiredArgsConstructor;

/**
 * Servicio de aplicación para eliminar Locations.
 */
@Service
@RequiredArgsConstructor
public class DeleteLocationById implements DeleteLocationByIdUseCase {

  private final LocationRepositoryPort locationRepository;

  /**
   * Elimina una Location por id.
   */
  @Override
  public Void execute(UUID id) {
    locationRepository.findByIdIncludingInactive(id)
      .orElseThrow(() -> new ResourceNotFoundException("Ubicación", id.toString()));
    locationRepository.deleteById(id);
    return null;
  }
}
