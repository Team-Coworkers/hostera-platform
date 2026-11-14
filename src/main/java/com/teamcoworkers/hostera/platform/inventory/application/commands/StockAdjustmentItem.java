package com.teamcoworkers.hostera.platform.inventory.application.commands;

import java.math.BigDecimal;

/** Stock adjustment of an inventory item, as it arrives in a command. */
public record StockAdjustmentItem(String id, String operation, BigDecimal quantity, Long locationId, String locationName,
                                  String reason, String note, String recordedAt, String operator, String transferId) {
}
