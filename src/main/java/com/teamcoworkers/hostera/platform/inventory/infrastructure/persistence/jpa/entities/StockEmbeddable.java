package com.teamcoworkers.hostera.platform.inventory.infrastructure.persistence.jpa.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

/** Row of the stock of an inventory item at one storage location. */
@Embeddable
@Getter
@Setter
@NoArgsConstructor
public class StockEmbeddable {
    @Column(nullable = false)
    private Long locationId;

    @Column(nullable = false, precision = 14, scale = 3)
    private BigDecimal quantity;
}
