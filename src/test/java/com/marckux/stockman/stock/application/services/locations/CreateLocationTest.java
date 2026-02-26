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
import com.marckux.stockman.stock.application.dtos.CreateLocationRequest;
import com.marckux.stockman.stock.application.ports.in.usecases.CreateLocationUseCase;
import com.marckux.stockman.stock.domain.model.Center;
import com.marckux.stockman.stock.domain.model.Location;
import com.marckux.stockman.stock.domain.ports.out.CenterRepositoryPort;
import com.marckux.stockman.stock.domain.ports.out.LocationRepositoryPort;

/**
 * Tests unitarios para CreateLocation.
 */
@ExtendWith(MockitoExtension.class)
public class CreateLocationTest extends BaseTest {

  @Mock
  private CenterRepositoryPort centerRepository;

  @Mock
  private LocationRepositoryPort locationRepository;

  @InjectMocks
  private CreateLocation service;

  /**
   * Verifica creación exitosa.
   */
  @Test
  @DisplayName("Crea una location correctamente")
  void shouldCreateLocation() {
    UUID centerId = UUID.randomUUID();

    Center center = Center.builder()
      .id(centerId)
      .name("Centro")
      .isActive(true)
      .build();

    CreateLocationRequest request = new CreateLocationRequest("Almacén", "Zona A", null);

    when(centerRepository.findById(centerId)).thenReturn(Optional.of(center));
    when(locationRepository.existsByCenterIdAndName(centerId, request.name())).thenReturn(false);
    when(locationRepository.save(org.mockito.ArgumentMatchers.any(Location.class)))
      .thenAnswer(i -> i.getArgument(0));

    var response = service.execute(new CreateLocationUseCase.Input(centerId, request));

    assertEquals("Almacén", response.name());
  }

  /**
   * Verifica error cuando el center no existe.
   */
  @Test
  @DisplayName("Rechaza si el center no existe")
  void shouldRejectWhenCenterNotFound() {
    UUID centerId = UUID.randomUUID();
    CreateLocationRequest request = new CreateLocationRequest("Almacén", null, null);

    when(centerRepository.findById(centerId)).thenReturn(Optional.empty());

    assertThrows(ResourceNotFoundException.class, () ->
      service.execute(new CreateLocationUseCase.Input(centerId, request))
    );
  }

  /**
   * Verifica rechazo por nombre duplicado.
   */
  @Test
  @DisplayName("Rechaza nombre duplicado")
  void shouldRejectDuplicateName() {
    UUID centerId = UUID.randomUUID();

    Center center = Center.builder()
      .id(centerId)
      .name("Centro")
      .isActive(true)
      .build();

    CreateLocationRequest request = new CreateLocationRequest("Almacén", null, null);

    when(centerRepository.findById(centerId)).thenReturn(Optional.of(center));
    when(locationRepository.existsByCenterIdAndName(centerId, request.name())).thenReturn(true);

    assertThrows(InvalidAttributeException.class, () ->
      service.execute(new CreateLocationUseCase.Input(centerId, request))
    );
  }
}
