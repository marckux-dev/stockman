package com.marckux.stockman.stock.application.services.centers;

import java.util.UUID;

import org.springframework.stereotype.Service;

import com.marckux.stockman.shared.domain.exceptions.InvalidAttributeException;
import com.marckux.stockman.shared.domain.exceptions.ResourceNotFoundException;
import com.marckux.stockman.stock.application.dtos.CenterResponse;
import com.marckux.stockman.stock.application.ports.in.usecases.UpdateCenterUseCase;
import com.marckux.stockman.stock.domain.model.Center;
import com.marckux.stockman.stock.domain.ports.out.CenterRepositoryPort;

import lombok.RequiredArgsConstructor;

/**
 * Servicio de aplicación para actualizar Centers.
 */
@Service
@RequiredArgsConstructor
public class UpdateCenter implements UpdateCenterUseCase {

  private final CenterRepositoryPort centerRepository;

  /**
   * Actualiza un Center validando existencia y unicidad de nombre.
   */
  @Override
  public CenterResponse execute(Input input) {
    UUID id = input.id();
    var request = input.request();

    Center center = centerRepository.findById(id)
      .orElseThrow(() -> new ResourceNotFoundException("Centro", id.toString()));

    if (request.name() != null) {
      if (request.name().isBlank()) {
        throw new InvalidAttributeException("El nombre del centro no puede estar vacío");
      }
      if (!request.name().equals(center.getName())) {
        centerRepository.findByName(request.name())
          .filter(existing -> !existing.getId().equals(id))
          .ifPresent(existing -> {
            throw new InvalidAttributeException("El nombre del centro ya está registrado");
          });
      }
    }

    Center updated = center.toBuilder()
      .name(request.name() != null ? request.name() : center.getName())
      .build();

    Center saved = centerRepository.save(updated);
    return CenterResponse.fromDomain(saved);
  }
}
