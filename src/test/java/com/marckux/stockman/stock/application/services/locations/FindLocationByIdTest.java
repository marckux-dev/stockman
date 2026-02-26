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
import com.marckux.stockman.shared.domain.exceptions.ResourceNotFoundException;
import com.marckux.stockman.stock.domain.model.Center;
import com.marckux.stockman.stock.domain.model.Location;
import com.marckux.stockman.stock.domain.ports.out.LocationRepositoryPort;

/**
 * Tests unitarios para FindLocationById.
 */
@ExtendWith(MockitoExtension.class)
public class FindLocationByIdTest extends BaseTest {

  @Mock
  private LocationRepositoryPort locationRepository;

  @InjectMocks
  private FindLocationById service;

  /**
   * Verifica búsqueda por id.
   */
  @Test
  @DisplayName("Encuentra una location por id")
  void shouldFindById() {
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

    when(locationRepository.findById(id)).thenReturn(Optional.of(location));

    var response = service.execute(id);
    assertEquals("Almacén", response.name());
  }

  /**
   * Verifica error cuando no existe.
   */
  @Test
  @DisplayName("Lanza error si no existe")
  void shouldThrowWhenNotFound() {
    UUID id = UUID.randomUUID();
    when(locationRepository.findById(id)).thenReturn(Optional.empty());

    assertThrows(ResourceNotFoundException.class, () -> service.execute(id));
  }
}
