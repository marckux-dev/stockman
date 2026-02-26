package com.marckux.stockman.stock.infrastructure.persistence;

import java.util.List;
import java.util.Set;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import com.marckux.stockman.shared.infrastructure.persistence.auditing.AuditableJpaEntity;

@Entity
@Table(name = "products")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class ProductEntity extends AuditableJpaEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  private UUID id;

  @Column(nullable = false)
  private String name;

  @Builder.Default
  @Column(name = "is_active", nullable = false)
  private boolean isActive = true;

  @Column(name = "short_name")
  private String shortName;

  @Column(name = "synonims")
  private String synonims;

  @Column(name = "search_vector")
  private String searchVector;

  @ManyToOne(optional = true)
  @JoinColumn(name = "category_id")
  private CategoryEntity category;

  @ManyToMany
  @JoinTable(
    name = "product_active_principles",
    joinColumns = @JoinColumn(name = "product_id"),
    inverseJoinColumns = @JoinColumn(name = "active_principle_id")
  )
  private Set<ActivePrincipleEntity> activePrinciples;

  @OneToMany(mappedBy = "product")
  private List<ExpirationEntity> expirations;

}
