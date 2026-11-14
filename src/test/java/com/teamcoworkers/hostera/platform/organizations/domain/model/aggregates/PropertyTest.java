package com.teamcoworkers.hostera.platform.organizations.domain.model.aggregates;

import com.teamcoworkers.hostera.platform.organizations.domain.model.valueobjects.CurrencyCode;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class PropertyTest {

    @Test
    void createsAPropertyWithTrimmedValues() {
        var property = new Property(1L, " Lima 01 ", "Lima", new CurrencyCode("PEN"));
        assertThat(property.getName()).isEqualTo("Lima 01");
        assertThat(property.getCurrency().code()).isEqualTo("PEN");
    }

    @Test
    void rejectsABlankNameOrAMissingOrganization() {
        var pen = new CurrencyCode("PEN");
        assertThatThrownBy(() -> new Property(1L, " ", "Lima", pen)).isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> new Property(null, "Lima 01", "Lima", pen)).isInstanceOf(IllegalArgumentException.class);
    }
}
