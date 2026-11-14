package com.teamcoworkers.hostera.platform.inventory.infrastructure.persistence.jpa.entities;

import com.teamcoworkers.hostera.platform.shared.infrastructure.persistence.jpa.entities.AuditableAbstractPersistenceEntity;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OrderColumn;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "inventory_items")
@Getter
@Setter
@NoArgsConstructor
public class InventoryItemPersistenceEntity extends AuditableAbstractPersistenceEntity {
    @Column(nullable = false)
    private Long propertyId;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, length = 40)
    private String code;

    @Column(nullable = false, length = 60)
    private String category;

    @Column(nullable = false, length = 20)
    private String unit;

    @Column(nullable = false)
    private Long primaryLocationId;

    @Column(nullable = false, precision = 14, scale = 3)
    private BigDecimal lowStockThreshold;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "inventory_item_stocks", joinColumns = @JoinColumn(name = "inventory_item_id"))
    @OrderColumn(name = "line_number")
    private List<StockEmbeddable> stocks = new ArrayList<>();

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "inventory_item_adjustments", joinColumns = @JoinColumn(name = "inventory_item_id"))
    @OrderColumn(name = "line_number")
    private List<StockAdjustmentEmbeddable> adjustments = new ArrayList<>();
}
