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

@Entity
@Table(
  name = "stocks",
  uniqueConstraints = {
    @UniqueConstraint(name = "uk_stock_product_location_active", columnNames = {"product_id", "location_id", "is_active"})
  }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class StockEntity extends AuditableJpaEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  private UUID id;

  @Column(name = "current_quantity")
  private Integer currentQuantity;

  @Column(name = "ideal_quantity")
  private Integer idealQuantity;

  @Column(name = "threshold")
  private Integer threshold;

  @Builder.Default
  @Column(name = "is_active", nullable = false)
  private boolean isActive = true;

  @ManyToOne(optional = false)
  @JoinColumn(name = "product_id", nullable = false)
  private ProductEntity product;

  @ManyToOne(optional = false)
  @JoinColumn(name = "location_id", nullable = false)
  private LocationEntity location;

}
