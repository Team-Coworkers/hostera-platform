package com.teamcoworkers.hostera.platform.bookings.domain.model.aggregates;

import com.teamcoworkers.hostera.platform.bookings.domain.model.valueobjects.PaymentMethod;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class PaymentTest {

    @Test
    void recordsAPaymentWithAPositiveAmount() {
        var payment = new Payment(1L, 2L, new BigDecimal("255"), PaymentMethod.CARD_TERMINAL, null, "POS-470075",
                "Demo operator");

        assertThat(payment.getPaidAt()).isNotNull();
        assertThat(payment.getMethod().value()).isEqualTo("card-terminal");
    }

    @Test
    void rejectsAZeroOrNegativeAmount() {
        assertThatThrownBy(() -> new Payment(1L, 2L, BigDecimal.ZERO, PaymentMethod.CASH, null, "", ""))
                .isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> new Payment(1L, 2L, new BigDecimal("-10"), PaymentMethod.CASH, null, "", ""))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void rejectsAnUnknownMethod() {
        assertThatThrownBy(() -> PaymentMethod.fromValue("crypto")).isInstanceOf(IllegalArgumentException.class);
    }
}
