package com.marckux.stockman.stock.domain.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.UUID;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.marckux.stockman.shared.BaseTest;
import com.marckux.stockman.shared.domain.exceptions.InvalidAttributeException;

/**
 * Tests del dominio Location.
 */
public class LocationTest extends BaseTest {

  /**
   * Verifica creación válida.
   */
  @Test
  @DisplayName("Debe crear una Location válida")
  void shouldCreateLocation() {
    UUID creatorId = UUID.randomUUID();

    Center center = Center.builder()
      .id(UUID.randomUUID())
      .name("Centro")
      .isActive(true)
      .createdBy(creatorId)
      .build();

    Location location = Location.builder()
      .id(UUID.randomUUID())
      .name("Almacén")
      .description("Zona A")
      .center(center)
      .isActive(true)
      .createdBy(creatorId)
      .build();

    assertEquals("Almacén", location.getName());
    assertEquals("Zona A", location.getDescription());
    assertEquals(center.getId(), location.getCenter().getId());
  }

  /**
   * Verifica validación de nombre obligatorio.
   */
  @Test
  @DisplayName("Debe rechazar nombre vacío")
  void shouldRejectBlankName() {
    UUID creatorId = UUID.randomUUID();

    Center center = Center.builder()
      .id(UUID.randomUUID())
      .name("Centro")
      .isActive(true)
      .createdBy(creatorId)
      .build();

    assertThrows(InvalidAttributeException.class, () -> Location.builder()
      .name(" ")
      .center(center)
      .isActive(true)
      .createdBy(creatorId)
      .build());
  }
}
