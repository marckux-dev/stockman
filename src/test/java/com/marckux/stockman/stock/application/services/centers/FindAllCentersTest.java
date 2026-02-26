package com.marckux.stockman.stock.application.services.centers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.marckux.stockman.shared.BaseTest;
import com.marckux.stockman.stock.domain.model.Center;
import com.marckux.stockman.stock.domain.ports.out.CenterRepositoryPort;

/**
 * Tests unitarios para FindAllCenters.
 */
@ExtendWith(MockitoExtension.class)
public class FindAllCentersTest extends BaseTest {

  @Mock
  private CenterRepositoryPort centerRepository;

  @InjectMocks
  private FindAllCenters service;

  /**
   * Verifica listado de centers.
   */
  @Test
  @DisplayName("Lista centers")
  void shouldListCenters() {
    when(centerRepository.findAll()).thenReturn(List.of(
      Center.builder()
        .name("Centro 1")
        .isActive(true)
        .createdBy(java.util.UUID.randomUUID())
        .build(),
      Center.builder()
        .name("Centro 2")
        .isActive(false)
        .createdBy(java.util.UUID.randomUUID())
        .build()
    ));

    var response = service.execute(null);
    assertEquals(2, response.size());
  }
}
