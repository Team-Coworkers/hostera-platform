package com.teamcoworkers.hostera.platform.accesscontrol.domain.model.aggregates;

import com.teamcoworkers.hostera.platform.accesscontrol.domain.model.valueobjects.*;
import com.teamcoworkers.hostera.platform.shared.domain.model.aggregates.AbstractDomainAggregateRoot;
import lombok.Getter;

import java.time.Instant;

/** One attempt to open a reader with a credential, granted or denied. */
@Getter
public class AccessEvent extends AbstractDomainAggregateRoot<AccessEvent> {

    private Long id;
    private Long propertyId;
    private Instant occurredAt;
    private Long credentialId;
    private String cardId;
    private String holderName;
    private HolderType holderType;
    private AccessPoint accessPoint;
    private Long roomId;
    private AccessResult result;
    private DenialReason denialReason;

    public AccessEvent(Long propertyId, Instant occurredAt, Long credentialId, String cardId, String holderName,
                       HolderType holderType, AccessPoint accessPoint, Long roomId, AccessResult result,
                       DenialReason denialReason) {
        if (propertyId == null || propertyId <= 0)
            throw new IllegalArgumentException("Property id must be a positive number");
        if (accessPoint == null) throw new IllegalArgumentException("Access point is required");
        if (accessPoint == AccessPoint.ROOM_DOOR && roomId == null)
            throw new IllegalArgumentException("A room door attempt needs its room");
        if (result == null) throw new IllegalArgumentException("Access result is required");
        if (result == AccessResult.DENIED && denialReason == null)
            throw new IllegalArgumentException("A denied attempt needs its reason");
        this.propertyId = propertyId;
        this.occurredAt = occurredAt == null ? Instant.now() : occurredAt;
        this.credentialId = credentialId;
        this.cardId = cardId == null ? "" : cardId;
        this.holderName = holderName == null ? "" : holderName;
        this.holderType = holderType;
        this.accessPoint = accessPoint;
        this.roomId = roomId;
        this.result = result;
        this.denialReason = result == AccessResult.GRANTED ? null : denialReason;
    }

    public static AccessEvent reconstitute(Long id, Long propertyId, Instant occurredAt, Long credentialId,
                                           String cardId, String holderName, HolderType holderType,
                                           AccessPoint accessPoint, Long roomId, AccessResult result,
                                           DenialReason denialReason) {
        var event = new AccessEvent(propertyId, occurredAt, credentialId, cardId, holderName, holderType,
                accessPoint, roomId, result, denialReason);
        event.id = id;
        return event;
    }

    /**
     * Records an attempt with the decision of the credential itself, so a revoked, expired or
     * not yet active credential, or one presented at another room, is always recorded as denied.
     */
    public static AccessEvent recordAttempt(Credential credential, AccessPoint accessPoint, Long roomId,
                                            Instant occurredAt) {
        var at = occurredAt == null ? Instant.now() : occurredAt;
        var decision = credential.evaluateAccess(accessPoint, roomId, at);
        return new AccessEvent(credential.getPropertyId(), at, credential.getId(), credential.getCardId(),
                credential.getHolderName(), credential.isGuestKeyCard() ? HolderType.GUEST : HolderType.STAFF,
                accessPoint, roomId, decision.result(), decision.denialReason());
    }

    /** Replaces the attempt with a new evaluation of the given credential. */
    public AccessEvent updateAttempt(Credential credential, AccessPoint accessPoint, Long roomId, Instant occurredAt) {
        var attempt = recordAttempt(credential, accessPoint, roomId, occurredAt);
        this.propertyId = attempt.propertyId;
        this.occurredAt = attempt.occurredAt;
        this.credentialId = attempt.credentialId;
        this.cardId = attempt.cardId;
        this.holderName = attempt.holderName;
        this.holderType = attempt.holderType;
        this.accessPoint = attempt.accessPoint;
        this.roomId = attempt.roomId;
        this.result = attempt.result;
        this.denialReason = attempt.denialReason;
        return this;
    }
}
