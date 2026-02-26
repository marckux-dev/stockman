package com.marckux.stockman.stock.infrastructure.rest;

import java.net.URI;
import java.util.List;
import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.marckux.stockman.stock.application.dtos.CenterResponse;
import com.marckux.stockman.stock.application.dtos.CreateCenterRequest;
import com.marckux.stockman.stock.application.dtos.UpdateCenterRequest;
import com.marckux.stockman.stock.application.ports.in.usecases.CreateCenterUseCase;
import com.marckux.stockman.stock.application.ports.in.usecases.DeleteCenterByIdUseCase;
import com.marckux.stockman.stock.application.ports.in.usecases.FindAllCentersUseCase;
import com.marckux.stockman.stock.application.ports.in.usecases.FindCenterByIdUseCase;
import com.marckux.stockman.stock.application.ports.in.usecases.UpdateCenterUseCase;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

/**
 * Controlador REST para la gestión de Centers.
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/stock/centers")
public class CenterController {

  private final CreateCenterUseCase createCenter;
  private final UpdateCenterUseCase updateCenter;
  private final FindCenterByIdUseCase findCenterById;
  private final FindAllCentersUseCase findAllCenters;
  private final DeleteCenterByIdUseCase deleteCenterById;

  /**
   * Crea un Center.
   */
  @PostMapping
  @PreAuthorize("hasRole('SUPER_ADMIN')")
  public ResponseEntity<CenterResponse> create(@RequestBody @Valid CreateCenterRequest request) {
    CenterResponse response = createCenter.execute(new CreateCenterUseCase.Input(request));
    URI location = ServletUriComponentsBuilder
      .fromCurrentRequest()
      .path("/{id}")
      .buildAndExpand(response.id())
      .toUri();
    return ResponseEntity.created(location).body(response);
  }

  /**
   * Lista todos los Centers.
   */
  @GetMapping
  @PreAuthorize("isAuthenticated()")
  public ResponseEntity<List<CenterResponse>> findAll() {
    return ResponseEntity.ok(findAllCenters.execute(null));
  }

  /**
   * Busca un Center por id.
   */
  @GetMapping("/{id}")
  @PreAuthorize("isAuthenticated()")
  public ResponseEntity<CenterResponse> findById(@PathVariable("id") String id) {
    return ResponseEntity.ok(findCenterById.execute(UUID.fromString(id)));
  }

  /**
   * Actualiza un Center.
   */
  @PutMapping("/{id}")
  @PreAuthorize("hasRole('SUPER_ADMIN')")
  public ResponseEntity<CenterResponse> update(
      @PathVariable("id") String id,
      @RequestBody @Valid UpdateCenterRequest request) {
    CenterResponse response = updateCenter.execute(new UpdateCenterUseCase.Input(UUID.fromString(id), request));
    return ResponseEntity.ok(response);
  }

  /**
   * Elimina un Center.
   */
  @DeleteMapping("/{id}")
  @PreAuthorize("hasRole('SUPER_ADMIN')")
  public ResponseEntity<Void> delete(@PathVariable("id") String id) {
    deleteCenterById.execute(UUID.fromString(id));
    return ResponseEntity.noContent().build();
  }

}
