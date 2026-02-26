package com.marckux.stockman.stock.application.services.centers;

import org.springframework.stereotype.Service;

import com.marckux.stockman.shared.domain.exceptions.InvalidAttributeException;
import com.marckux.stockman.stock.application.dtos.CenterResponse;
import com.marckux.stockman.stock.application.dtos.CreateCenterRequest;
import com.marckux.stockman.stock.application.ports.in.usecases.CreateCenterUseCase;
import com.marckux.stockman.stock.domain.model.Center;
import com.marckux.stockman.stock.domain.ports.out.CenterRepositoryPort;

import lombok.RequiredArgsConstructor;

/**
 * Servicio de aplicación para crear Centers.
 */
@Service
@RequiredArgsConstructor
public class CreateCenter implements CreateCenterUseCase {

  private final CenterRepositoryPort centerRepository;

  /**
   * Crea un Center validando unicidad del nombre.
   * El Center se crea siempre con estado activo.
   */
  @Override
  public CenterResponse execute(Input input) {
    CreateCenterRequest request = input.request();
    if (centerRepository.existsByName(request.name())) {
      throw new InvalidAttributeException("El nombre del centro ya está registrado");
    }

    Center center = Center.builder()
      .name(request.name())
      .isActive(true)
      .build();

    Center saved = centerRepository.save(center);
    return CenterResponse.fromDomain(saved);
  }
}
