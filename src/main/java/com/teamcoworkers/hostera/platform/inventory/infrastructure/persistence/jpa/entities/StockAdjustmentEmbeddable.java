package com.teamcoworkers.hostera.platform.inventory.infrastructure.persistence.jpa.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.Instant;

/** Row of the stock adjustment history of an inventory item. */
@Embeddable
@Getter
@Setter
@NoArgsConstructor
public class StockAdjustmentEmbeddable {
    @Column(nullable = false, length = 80)
    private String adjustmentCode;

    @Column(nullable = false, length = 10)
    private String operation;

    @Column(nullable = false, precision = 14, scale = 3)
    private BigDecimal quantity;

    @Column(nullable = false)
    private Long locationId;

    @Column(nullable = false)
    private String locationName;

    @Column(nullable = false, length = 60)
    private String reason;

    @Column(nullable = false, length = 500)
    private String note;

    @Column(nullable = false)
    private Instant recordedAt;

    @Column(nullable = false)
    private String operatorName;

    @Column(length = 80)
    private String transferCode;
}
