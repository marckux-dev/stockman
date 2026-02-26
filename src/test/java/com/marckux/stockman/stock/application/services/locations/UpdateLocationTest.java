package com.marckux.stockman.stock.application.services.locations;

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
import com.marckux.stockman.stock.application.dtos.UpdateLocationRequest;
import com.marckux.stockman.stock.application.ports.in.usecases.UpdateLocationUseCase;
import com.marckux.stockman.stock.domain.model.Center;
import com.marckux.stockman.stock.domain.model.Location;
import com.marckux.stockman.stock.domain.ports.out.LocationRepositoryPort;

/**
 * Tests unitarios para UpdateLocation.
 */
@ExtendWith(MockitoExtension.class)
public class UpdateLocationTest extends BaseTest {

  @Mock
  private LocationRepositoryPort locationRepository;

  @InjectMocks
  private UpdateLocation service;

  /**
   * Verifica actualización exitosa.
   */
  @Test
  @DisplayName("Actualiza una location correctamente")
  void shouldUpdateLocation() {
    UUID id = UUID.randomUUID();
    UUID creatorId = UUID.randomUUID();

    Center center = Center.builder()
      .id(UUID.randomUUID())
      .name("Centro")
      .isActive(true)
      .createdBy(creatorId)
      .build();

    Location location = Location.builder()
      .id(id)
      .name("Almacén")
      .description("Zona A")
      .center(center)
      .isActive(true)
      .createdBy(creatorId)
      .build();

    UpdateLocationRequest request = new UpdateLocationRequest("Nevera", "Zona B");

    when(locationRepository.findById(id)).thenReturn(Optional.of(location));
    when(locationRepository.findByCenterIdAndName(center.getId(), request.name()))
      .thenReturn(Optional.empty());
    when(locationRepository.save(org.mockito.ArgumentMatchers.any(Location.class)))
      .thenAnswer(i -> i.getArgument(0));

    var response = service.execute(new UpdateLocationUseCase.Input(id, request));
    assertEquals("Nevera", response.name());
    assertEquals(true, response.isActive());
  }

  /**
   * Verifica error cuando no existe.
   */
  @Test
  @DisplayName("Rechaza actualización si no existe")
  void shouldRejectWhenNotFound() {
    UUID id = UUID.randomUUID();
    UpdateLocationRequest request = new UpdateLocationRequest("Nevera", null);

    when(locationRepository.findById(id)).thenReturn(Optional.empty());

    assertThrows(ResourceNotFoundException.class, () ->
      service.execute(new UpdateLocationUseCase.Input(id, request))
    );
  }

  /**
   * Verifica rechazo por nombre duplicado.
   */
  @Test
  @DisplayName("Rechaza nombre duplicado en actualización")
  void shouldRejectDuplicateName() {
    UUID id = UUID.randomUUID();
    UUID creatorId = UUID.randomUUID();

    Center center = Center.builder()
      .id(UUID.randomUUID())
      .name("Centro")
      .isActive(true)
      .createdBy(creatorId)
      .build();

    Location location = Location.builder()
      .id(id)
      .name("Almacén")
      .center(center)
      .isActive(true)
      .createdBy(creatorId)
      .build();

    Location other = Location.builder()
      .id(UUID.randomUUID())
      .name("Nevera")
      .center(center)
      .isActive(true)
      .createdBy(creatorId)
      .build();

    UpdateLocationRequest request = new UpdateLocationRequest("Nevera", null);

    when(locationRepository.findById(id)).thenReturn(Optional.of(location));
    when(locationRepository.findByCenterIdAndName(center.getId(), request.name()))
      .thenReturn(Optional.of(other));

    assertThrows(InvalidAttributeException.class, () ->
      service.execute(new UpdateLocationUseCase.Input(id, request))
    );
  }
}
