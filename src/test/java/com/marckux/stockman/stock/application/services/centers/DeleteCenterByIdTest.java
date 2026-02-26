package com.marckux.stockman.stock.application.services.centers;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.marckux.stockman.shared.BaseTest;
import com.marckux.stockman.shared.domain.exceptions.ResourceNotFoundException;
import com.marckux.stockman.stock.domain.model.Center;
import com.marckux.stockman.stock.domain.ports.out.CenterRepositoryPort;

/**
 * Tests unitarios para DeleteCenterById.
 */
@ExtendWith(MockitoExtension.class)
public class DeleteCenterByIdTest extends BaseTest {

  @Mock
  private CenterRepositoryPort centerRepository;

  @InjectMocks
  private DeleteCenterById service;

  /**
   * Verifica eliminación.
   */
  @Test
  @DisplayName("Elimina center por id")
  void shouldDeleteCenter() {
    UUID id = UUID.randomUUID();
    Center center = Center.builder()
      .id(id)
      .name("Centro 1")
      .isActive(true)
      .createdBy(UUID.randomUUID())
      .build();

    when(centerRepository.findByIdIncludingInactive(id)).thenReturn(Optional.of(center));

    service.execute(id);

    verify(centerRepository).deleteById(id);
  }

  /**
   * Verifica error cuando no existe.
   */
  @Test
  @DisplayName("Lanza error si no existe")
  void shouldThrowWhenNotFound() {
    UUID id = UUID.randomUUID();
    when(centerRepository.findByIdIncludingInactive(id)).thenReturn(Optional.empty());

    assertThrows(ResourceNotFoundException.class, () -> service.execute(id));
  }
}
