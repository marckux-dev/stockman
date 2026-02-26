package com.marckux.stockman.stock.application.services.centers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
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
import com.marckux.stockman.shared.domain.exceptions.InvalidAttributeException;
import com.marckux.stockman.shared.domain.exceptions.ResourceNotFoundException;
import com.marckux.stockman.stock.application.dtos.UpdateCenterRequest;
import com.marckux.stockman.stock.application.ports.in.usecases.UpdateCenterUseCase;
import com.marckux.stockman.stock.domain.model.Center;
import com.marckux.stockman.stock.domain.ports.out.CenterRepositoryPort;

/**
 * Tests unitarios para UpdateCenter.
 */
@ExtendWith(MockitoExtension.class)
public class UpdateCenterTest extends BaseTest {

  @Mock
  private CenterRepositoryPort centerRepository;

  @InjectMocks
  private UpdateCenter service;

  /**
   * Verifica actualización exitosa.
   */
  @Test
  @DisplayName("Actualiza un center correctamente")
  void shouldUpdateCenter() {
    UUID id = UUID.randomUUID();
    Center center = Center.builder()
      .id(id)
      .name("Centro 1")
      .isActive(true)
      .createdBy(UUID.randomUUID())
      .build();

    UpdateCenterRequest request = new UpdateCenterRequest("Centro 2");

    when(centerRepository.findById(id)).thenReturn(Optional.of(center));
    when(centerRepository.findByName(request.name())).thenReturn(Optional.empty());
    when(centerRepository.save(org.mockito.ArgumentMatchers.any(Center.class)))
      .thenAnswer(i -> i.getArgument(0));

    var response = service.execute(new UpdateCenterUseCase.Input(id, request));

    assertEquals("Centro 2", response.name());
    assertEquals(true, response.isActive());
  }

  /**
   * Verifica error cuando no existe.
   */
  @Test
  @DisplayName("Rechaza actualización si no existe")
  void shouldRejectWhenNotFound() {
    UUID id = UUID.randomUUID();
    UpdateCenterRequest request = new UpdateCenterRequest("Centro");

    when(centerRepository.findById(id)).thenReturn(Optional.empty());

    assertThrows(ResourceNotFoundException.class, () ->
      service.execute(new UpdateCenterUseCase.Input(id, request))
    );
  }

  /**
   * Verifica rechazo por nombre duplicado.
   */
  @Test
  @DisplayName("Rechaza nombre duplicado en actualización")
  void shouldRejectDuplicateName() {
    UUID id = UUID.randomUUID();
    Center center = Center.builder()
      .id(id)
      .name("Centro 1")
      .isActive(true)
      .createdBy(UUID.randomUUID())
      .build();
    Center other = Center.builder()
      .id(UUID.randomUUID())
      .name("Centro 2")
      .isActive(true)
      .createdBy(UUID.randomUUID())
      .build();

    UpdateCenterRequest request = new UpdateCenterRequest("Centro 2");

    when(centerRepository.findById(id)).thenReturn(Optional.of(center));
    when(centerRepository.findByName(request.name())).thenReturn(Optional.of(other));

    assertThrows(InvalidAttributeException.class, () ->
      service.execute(new UpdateCenterUseCase.Input(id, request))
    );
  }
}
