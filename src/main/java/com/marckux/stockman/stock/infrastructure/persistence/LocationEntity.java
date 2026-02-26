package com.marckux.stockman.stock.infrastructure.persistence;

import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import com.marckux.stockman.shared.infrastructure.persistence.auditing.AuditableJpaEntity;

/**
 * Entidad JPA para persistir Locations.
 */
@Entity
@Table(
  name = "locations",
  uniqueConstraints = {
    @UniqueConstraint(name = "uk_location_center_name_active", columnNames = {"center_id", "name", "is_active"})
  }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class LocationEntity extends AuditableJpaEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  private UUID id;

  @Column(nullable = false)
  private String name;

  @Column(nullable = true)
  private String description;

  @ManyToOne(optional = false)
  @JoinColumn(name = "center_id", nullable = false)
  private CenterEntity center;

  @ManyToOne(optional = true)
  @JoinColumn(name = "parent_location_id")
  private LocationEntity parentLocation;

  @Column(name = "is_active", nullable = false)
  @Builder.Default
  private boolean isActive = true;

}
