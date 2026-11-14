package com.teamcoworkers.hostera.platform.accesscontrol.domain.model.valueobjects;

/** Result of presenting a credential to a reader, with the reason when it is denied. */
public record AccessDecision(AccessResult result, DenialReason denialReason) {

    public static AccessDecision granted() {
        return new AccessDecision(AccessResult.GRANTED, null);
    }

    public static AccessDecision denied(DenialReason reason) {
        return new AccessDecision(AccessResult.DENIED, reason);
    }
}
