package com.marckux.stockman.stock.application.services.locations;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.marckux.stockman.shared.BaseTest;
import com.marckux.stockman.stock.domain.model.Center;
import com.marckux.stockman.stock.domain.model.Location;
import com.marckux.stockman.stock.domain.ports.out.LocationRepositoryPort;

/**
 * Tests unitarios para FindLocationsByCenter.
 */
@ExtendWith(MockitoExtension.class)
public class FindLocationsByCenterTest extends BaseTest {

  @Mock
  private LocationRepositoryPort locationRepository;

  @InjectMocks
  private FindLocationsByCenter service;

  /**
   * Verifica listado por center.
   */
  @Test
  @DisplayName("Lista locations por center")
  void shouldListByCenter() {
    UUID centerId = UUID.randomUUID();
    UUID creatorId = UUID.randomUUID();

    Center center = Center.builder()
      .id(centerId)
      .name("Centro")
      .isActive(true)
      .createdBy(creatorId)
      .build();

    when(locationRepository.findAllByCenterId(centerId)).thenReturn(List.of(
      Location.builder().name("Almacén").center(center).isActive(true).createdBy(creatorId).build(),
      Location.builder().name("Nevera").center(center).isActive(false).createdBy(creatorId).build()
    ));

    var response = service.execute(centerId);
    assertEquals(2, response.size());
  }
}
