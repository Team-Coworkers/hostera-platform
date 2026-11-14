package com.teamcoworkers.hostera.platform.inventory.application.commands;

import java.math.BigDecimal;

/** Stock of an inventory item at one storage location, as it arrives in a command. */
public record StockItem(Long locationId, BigDecimal quantity) {
}
