package com.marckux.stockman.stock.application.services.locations;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.marckux.stockman.stock.application.dtos.LocationResponse;
import com.marckux.stockman.stock.application.ports.in.usecases.FindLocationsByCenterUseCase;
import com.marckux.stockman.stock.domain.ports.out.LocationRepositoryPort;

import lombok.RequiredArgsConstructor;

/**
 * Servicio de aplicación para listar Locations por Center.
 */
@Service
@RequiredArgsConstructor
public class FindLocationsByCenter implements FindLocationsByCenterUseCase {

  private final LocationRepositoryPort locationRepository;

  /**
   * Lista Locations por Center.
   */
  @Override
  public List<LocationResponse> execute(UUID centerId) {
    return locationRepository.findAllByCenterId(centerId)
      .stream()
      .map(LocationResponse::fromDomain)
      .collect(Collectors.toList());
  }
}
