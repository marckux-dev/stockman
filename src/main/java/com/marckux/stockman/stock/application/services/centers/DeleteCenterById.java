package com.marckux.stockman.stock.application.services.centers;

import java.util.UUID;

import org.springframework.stereotype.Service;

import com.marckux.stockman.shared.domain.exceptions.ResourceNotFoundException;
import com.marckux.stockman.stock.application.ports.in.usecases.DeleteCenterByIdUseCase;
import com.marckux.stockman.stock.domain.ports.out.CenterRepositoryPort;

import lombok.RequiredArgsConstructor;

/**
 * Servicio de aplicación para eliminar Centers.
 */
@Service
@RequiredArgsConstructor
public class DeleteCenterById implements DeleteCenterByIdUseCase {

  private final CenterRepositoryPort centerRepository;

  /**
   * Elimina un Center por id.
   */
  @Override
  public Void execute(UUID id) {
    centerRepository.findByIdIncludingInactive(id)
      .orElseThrow(() -> new ResourceNotFoundException("Centro", id.toString()));
    centerRepository.deleteById(id);
    return null;
  }
}
