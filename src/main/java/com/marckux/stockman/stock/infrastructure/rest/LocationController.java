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

import com.marckux.stockman.stock.application.dtos.CreateLocationRequest;
import com.marckux.stockman.stock.application.dtos.LocationResponse;
import com.marckux.stockman.stock.application.dtos.UpdateLocationRequest;
import com.marckux.stockman.stock.application.ports.in.usecases.CreateLocationUseCase;
import com.marckux.stockman.stock.application.ports.in.usecases.DeleteLocationByIdUseCase;
import com.marckux.stockman.stock.application.ports.in.usecases.FindLocationByIdUseCase;
import com.marckux.stockman.stock.application.ports.in.usecases.FindLocationsByCenterUseCase;
import com.marckux.stockman.stock.application.ports.in.usecases.UpdateLocationUseCase;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

/**
 * Controlador REST para la gestión de Locations.
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/stock")
public class LocationController {

  private final CreateLocationUseCase createLocation;
  private final UpdateLocationUseCase updateLocation;
  private final FindLocationByIdUseCase findLocationById;
  private final FindLocationsByCenterUseCase findLocationsByCenter;
  private final DeleteLocationByIdUseCase deleteLocationById;

  /**
   * Crea una Location dentro de un Center.
   */
  @PostMapping("/centers/{centerId}/locations")
  @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
  public ResponseEntity<LocationResponse> create(
      @PathVariable("centerId") String centerId,
      @RequestBody @Valid CreateLocationRequest request) {
    LocationResponse response = createLocation.execute(
      new CreateLocationUseCase.Input(UUID.fromString(centerId), request)
    );
    URI location = ServletUriComponentsBuilder
      .fromCurrentContextPath()
      .path("/api/stock/locations/{id}")
      .buildAndExpand(response.id())
      .toUri();
    return ResponseEntity.created(location).body(response);
  }

  /**
   * Lista Locations por Center.
   */
  @GetMapping("/centers/{centerId}/locations")
  @PreAuthorize("isAuthenticated()")
  public ResponseEntity<List<LocationResponse>> findByCenter(@PathVariable("centerId") String centerId) {
    return ResponseEntity.ok(findLocationsByCenter.execute(UUID.fromString(centerId)));
  }

  /**
   * Busca una Location por id.
   */
  @GetMapping("/locations/{id}")
  @PreAuthorize("isAuthenticated()")
  public ResponseEntity<LocationResponse> findById(@PathVariable("id") String id) {
    return ResponseEntity.ok(findLocationById.execute(UUID.fromString(id)));
  }

  /**
   * Actualiza una Location.
   */
  @PutMapping("/locations/{id}")
  @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
  public ResponseEntity<LocationResponse> update(
      @PathVariable("id") String id,
      @RequestBody @Valid UpdateLocationRequest request) {
    LocationResponse response = updateLocation.execute(new UpdateLocationUseCase.Input(UUID.fromString(id), request));
    return ResponseEntity.ok(response);
  }

  /**
   * Elimina una Location.
   */
  @DeleteMapping("/locations/{id}")
  @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
  public ResponseEntity<Void> delete(@PathVariable("id") String id) {
    deleteLocationById.execute(UUID.fromString(id));
    return ResponseEntity.noContent().build();
  }

}
