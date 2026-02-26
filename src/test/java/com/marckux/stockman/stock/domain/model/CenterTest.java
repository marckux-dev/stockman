package com.marckux.stockman.stock.domain.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.Instant;
import java.util.UUID;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.marckux.stockman.shared.BaseTest;
import com.marckux.stockman.shared.domain.exceptions.InvalidAttributeException;

/**
 * Tests del dominio Center.
 */
public class CenterTest extends BaseTest {

  /**
   * Verifica creación válida.
   */
  @Test
  @DisplayName("Debe crear un Center válido")
  void shouldCreateCenter() {
    UUID id = UUID.randomUUID();
    Instant now = Instant.now();
    UUID creatorId = UUID.randomUUID();

    Center center = Center.builder()
      .id(id)
      .name("Central")
      .isActive(true)
      .createdAt(now)
      .updatedAt(now)
      .createdBy(creatorId)
      .build();

    assertEquals(id, center.getId());
    assertEquals("Central", center.getName());
    assertEquals(true, center.isActive());
  }

  /**
   * Verifica validación de nombre obligatorio.
   */
  @Test
  @DisplayName("Debe rechazar nombre vacío")
  void shouldRejectBlankName() {
    assertThrows(InvalidAttributeException.class, () -> Center.builder()
      .name(" ")
      .isActive(true)
      .createdBy(UUID.randomUUID())
      .build());
  }

  /**
   * Verifica renombrado.
   */
  @Test
  @DisplayName("Debe permitir renombrar")
  void shouldRename() {
    Center center = Center.builder()
      .name("Centro A")
      .isActive(true)
      .createdBy(UUID.randomUUID())
      .build();

    Center updated = center.rename("Centro B");
    assertEquals("Centro B", updated.getName());
  }
}
