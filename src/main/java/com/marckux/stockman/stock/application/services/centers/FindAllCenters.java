package com.marckux.stockman.stock.application.services.centers;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.marckux.stockman.stock.application.dtos.CenterResponse;
import com.marckux.stockman.stock.application.ports.in.usecases.FindAllCentersUseCase;
import com.marckux.stockman.stock.domain.ports.out.CenterRepositoryPort;

import lombok.RequiredArgsConstructor;

/**
 * Servicio de aplicación para listar Centers.
 */
@Service
@RequiredArgsConstructor
public class FindAllCenters implements FindAllCentersUseCase {

  private final CenterRepositoryPort centerRepository;

  /**
   * Lista todos los Centers.
   */
  @Override
  public List<CenterResponse> execute(Void input) {
    return centerRepository.findAll()
      .stream()
      .map(CenterResponse::fromDomain)
      .collect(Collectors.toList());
  }
}
