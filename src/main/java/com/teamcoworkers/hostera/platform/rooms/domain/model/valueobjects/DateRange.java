package com.teamcoworkers.hostera.platform.rooms.domain.model.valueobjects;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;

/**
 * Range of calendar days, both included.
 *
 * @param startDate first day of the range
 * @param endDate   last day of the range, on or after the first day
 */
public record DateRange(LocalDate startDate, LocalDate endDate) {
    public DateRange {
        if (startDate == null || endDate == null)
            throw new IllegalArgumentException("Start date and end date are required");
        if (endDate.isBefore(startDate))
            throw new IllegalArgumentException("End date must be on or after the start date");
    }

    public static DateRange of(String startDate, String endDate) {
        return new DateRange(parseDay(startDate, "Start date"), parseDay(endDate, "End date"));
    }

    /**
     * @param value a day written as yyyy-MM-dd
     * @param label name of the field, used in the error message
     * @return the calendar day
     */
    public static LocalDate parseDay(String value, String label) {
        try {
            return LocalDate.parse(value == null ? "" : value.trim());
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException("%s must be a date in the format yyyy-MM-dd".formatted(label));
        }
    }

    public boolean covers(LocalDate date) {
        return date != null && !date.isBefore(startDate) && !date.isAfter(endDate);
    }

    public boolean overlaps(DateRange other) {
        return other != null && !startDate.isAfter(other.endDate) && !other.startDate.isAfter(endDate);
    }
}
