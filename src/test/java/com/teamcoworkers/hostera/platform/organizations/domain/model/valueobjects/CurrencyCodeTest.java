package com.teamcoworkers.hostera.platform.organizations.domain.model.valueobjects;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class CurrencyCodeTest {

    @Test
    void normalizesTheCodeToUppercase() {
        assertThat(new CurrencyCode(" pen ").code()).isEqualTo("PEN");
    }

    @Test
    void rejectsCodesThatAreNotThreeLetters() {
        assertThatThrownBy(() -> new CurrencyCode("SOLES")).isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> new CurrencyCode(null)).isInstanceOf(IllegalArgumentException.class);
    }
}
