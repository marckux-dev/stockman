package com.marckux.stockman.stock.application.services.locations;

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
import com.marckux.stockman.stock.domain.model.Location;
import com.marckux.stockman.stock.domain.ports.out.LocationRepositoryPort;

/**
 * Tests unitarios para DeleteLocationById.
 */
@ExtendWith(MockitoExtension.class)
public class DeleteLocationByIdTest extends BaseTest {

  @Mock
  private LocationRepositoryPort locationRepository;

  @InjectMocks
  private DeleteLocationById service;

  /**
   * Verifica eliminación.
   */
  @Test
  @DisplayName("Elimina location por id")
  void shouldDeleteLocation() {
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

    when(locationRepository.findByIdIncludingInactive(id)).thenReturn(Optional.of(location));

    service.execute(id);

    verify(locationRepository).deleteById(id);
  }

  /**
   * Verifica error cuando no existe.
   */
  @Test
  @DisplayName("Lanza error si no existe")
  void shouldThrowWhenNotFound() {
    UUID id = UUID.randomUUID();
    when(locationRepository.findByIdIncludingInactive(id)).thenReturn(Optional.empty());

    assertThrows(ResourceNotFoundException.class, () -> service.execute(id));
  }
}
