package com.marckux.stockman.stock.application.services.centers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.marckux.stockman.shared.BaseTest;
import com.marckux.stockman.shared.domain.exceptions.InvalidAttributeException;
import com.marckux.stockman.stock.application.dtos.CreateCenterRequest;
import com.marckux.stockman.stock.application.ports.in.usecases.CreateCenterUseCase;
import com.marckux.stockman.stock.domain.model.Center;
import com.marckux.stockman.stock.domain.ports.out.CenterRepositoryPort;

/**
 * Tests unitarios para CreateCenter.
 */
@ExtendWith(MockitoExtension.class)
public class CreateCenterTest extends BaseTest {

  @Mock
  private CenterRepositoryPort centerRepository;

  @InjectMocks
  private CreateCenter service;

  /**
   * Verifica creación exitosa.
   */
  @Test
  @DisplayName("Crea un center correctamente")
  void shouldCreateCenter() {
    CreateCenterRequest request = new CreateCenterRequest("Centro 1");

    when(centerRepository.existsByName(request.name())).thenReturn(false);
    when(centerRepository.save(org.mockito.ArgumentMatchers.any(Center.class)))
      .thenAnswer(i -> i.getArgument(0));

    var response = service.execute(new CreateCenterUseCase.Input(request));
    assertEquals("Centro 1", response.name());
    assertEquals(true, response.isActive());
  }

  /**
   * Verifica rechazo por nombre duplicado.
   */
  @Test
  @DisplayName("Rechaza nombre duplicado")
  void shouldRejectDuplicateName() {
    CreateCenterRequest request = new CreateCenterRequest("Centro 1");

    when(centerRepository.existsByName(request.name())).thenReturn(true);

    assertThrows(InvalidAttributeException.class, () ->
      service.execute(new CreateCenterUseCase.Input(request))
    );
  }
}
