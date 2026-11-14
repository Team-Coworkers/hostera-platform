package com.teamcoworkers.hostera.platform.inventory.domain.model.entities;

import com.teamcoworkers.hostera.platform.inventory.domain.model.valueobjects.StockOperation;

import java.math.BigDecimal;
import java.time.Instant;

/**
 * Stock adjustment entity: one recorded movement of stock of an inventory item, in or out of a
 * storage location. It lives inside the inventory item aggregate and is never edited afterwards.
 *
 * @param id           identifier of the adjustment inside its inventory item
 * @param operation    whether the stock came in or went out
 * @param quantity     moved quantity, greater than zero
 * @param locationId   storage location where the stock moved
 * @param locationName name of that location when the adjustment was recorded
 * @param reason       reason code, for example {@code supplier-delivery}
 * @param note         optional free text
 * @param recordedAt   moment in which the adjustment was recorded
 * @param operator     person who recorded it
 * @param transferId   identifier shared by the two adjustments of a transfer, or {@code null}
 */
public record StockAdjustment(String id, StockOperation operation, BigDecimal quantity, Long locationId, String locationName,
                              String reason, String note, Instant recordedAt, String operator, String transferId) {
    public StockAdjustment {
        if (id == null || id.isBlank()) throw new IllegalArgumentException("Stock adjustment id is required");
        if (operation == null) throw new IllegalArgumentException("Stock operation is required");
        if (quantity == null || quantity.signum() <= 0)
            throw new IllegalArgumentException("Stock adjustment quantity must be greater than zero");
        if (locationId == null || locationId <= 0)
            throw new IllegalArgumentException("Stock adjustment location id must be a positive number");
        if (reason == null || reason.isBlank()) throw new IllegalArgumentException("Stock adjustment reason is required");
        if (recordedAt == null) throw new IllegalArgumentException("Stock adjustment date is required");
        id = id.trim();
        locationName = locationName == null ? "" : locationName.trim();
        reason = reason.trim();
        note = note == null ? "" : note.trim();
        operator = operator == null ? "" : operator.trim();
        transferId = transferId == null || transferId.isBlank() ? null : transferId.trim();
    }
}
