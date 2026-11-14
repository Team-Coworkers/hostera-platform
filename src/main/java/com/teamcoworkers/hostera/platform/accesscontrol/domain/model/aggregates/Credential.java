package com.teamcoworkers.hostera.platform.accesscontrol.domain.model.aggregates;

import com.teamcoworkers.hostera.platform.accesscontrol.domain.model.valueobjects.*;
import com.teamcoworkers.hostera.platform.shared.domain.model.aggregates.AbstractDomainAggregateRoot;
import lombok.Getter;

import java.time.Instant;

/**
 * A key card of a guest or a credential of a staff member. A guest key card opens the main
 * entrance, the elevator and the door of its own room; a staff credential opens what its scope
 * allows. Once revoked, a credential stays revoked: later updates keep the first revocation.
 */
@Getter
public class Credential extends AbstractDomainAggregateRoot<Credential> {

    private Long id;
    private Long propertyId;
    private String cardId;
    private CredentialType type;
    private String holderName;
    private Long staffMemberId;
    private Long bookingId;
    private String bookingCode;
    private Long roomId;
    private StaffScope scope;
    private Instant validFrom;
    private Instant validUntil;
    private Instant issuedAt;
    private String issuedBy;
    private Revocation revocation;

    public Credential(Long propertyId, String cardId, CredentialType type, String holderName, Long staffMemberId,
                      Long bookingId, String bookingCode, Long roomId, StaffScope scope, Instant validFrom,
                      Instant validUntil, Instant issuedAt, String issuedBy, Revocation revocation) {
        updateInformation(propertyId, cardId, type, holderName, staffMemberId, bookingId, bookingCode, roomId, scope,
                validFrom, validUntil, issuedAt, issuedBy, revocation);
    }

    public static Credential reconstitute(Long id, Long propertyId, String cardId, CredentialType type,
                                          String holderName, Long staffMemberId, Long bookingId, String bookingCode,
                                          Long roomId, StaffScope scope, Instant validFrom, Instant validUntil,
                                          Instant issuedAt, String issuedBy, Revocation revocation) {
        var credential = new Credential(propertyId, cardId, type, holderName, staffMemberId, bookingId, bookingCode,
                roomId, scope, validFrom, validUntil, issuedAt, issuedBy, revocation);
        credential.id = id;
        return credential;
    }

    public Credential updateInformation(Long propertyId, String cardId, CredentialType type, String holderName,
                                        Long staffMemberId, Long bookingId, String bookingCode, Long roomId,
                                        StaffScope scope, Instant validFrom, Instant validUntil, Instant issuedAt,
                                        String issuedBy, Revocation revocation) {
        if (propertyId == null || propertyId <= 0)
            throw new IllegalArgumentException("Property id must be a positive number");
        if (cardId == null || cardId.isBlank()) throw new IllegalArgumentException("Card id is required");
        if (type == null) throw new IllegalArgumentException("Credential type is required");
        if (holderName == null || holderName.isBlank()) throw new IllegalArgumentException("Holder name is required");
        if (validFrom == null) throw new IllegalArgumentException("Credential start is required");
        if (validUntil != null && !validUntil.isAfter(validFrom))
            throw new IllegalArgumentException("Credential end must be after its start");
        if (type == CredentialType.GUEST_KEY_CARD) {
            if (bookingId == null) throw new IllegalArgumentException("A guest key card needs its booking");
            if (roomId == null) throw new IllegalArgumentException("A guest key card needs its room");
        } else {
            if (staffMemberId == null) throw new IllegalArgumentException("A staff credential needs its staff member");
            if (scope == null) throw new IllegalArgumentException("A staff credential needs its scope");
        }

        this.propertyId = propertyId;
        this.cardId = cardId.trim();
        this.type = type;
        this.holderName = holderName.trim();
        this.staffMemberId = type == CredentialType.STAFF_CREDENTIAL ? staffMemberId : null;
        this.bookingId = type == CredentialType.GUEST_KEY_CARD ? bookingId : null;
        this.bookingCode = bookingCode == null ? "" : bookingCode;
        this.roomId = type == CredentialType.GUEST_KEY_CARD ? roomId : null;
        this.scope = type == CredentialType.STAFF_CREDENTIAL ? scope : null;
        this.validFrom = validFrom;
        this.validUntil = validUntil;
        this.issuedAt = issuedAt == null ? Instant.now() : issuedAt;
        this.issuedBy = issuedBy == null ? "" : issuedBy;
        if (this.revocation == null) this.revocation = revocation;
        return this;
    }

    /**
     * Revokes the credential. A credential already revoked keeps its first revocation.
     *
     * @return whether the credential changed
     */
    public boolean revoke(Instant at, String by, RevocationReason reason, String note) {
        if (isRevoked()) return false;
        this.revocation = new Revocation(at == null ? Instant.now() : at, by, reason, note);
        return true;
    }

    public boolean isRevoked() {
        return revocation != null;
    }

    public boolean isGuestKeyCard() {
        return type == CredentialType.GUEST_KEY_CARD;
    }

    /** @return the status at the given moment: lost or revoked first, then scheduled, expired or active */
    public CredentialStatus statusAt(Instant now) {
        if (revocation != null)
            return revocation.reason() == RevocationReason.LOST_CARD ? CredentialStatus.LOST : CredentialStatus.REVOKED;
        if (now.isBefore(validFrom)) return CredentialStatus.SCHEDULED;
        if (validUntil != null && !now.isBefore(validUntil)) return CredentialStatus.EXPIRED;
        return CredentialStatus.ACTIVE;
    }

    /**
     * Decides whether the credential opens a reader. Only an active credential can open anything,
     * and only inside its scope: a guest key card never opens another room or a staff area.
     */
    public AccessDecision evaluateAccess(AccessPoint accessPoint, Long targetRoomId, Instant at) {
        return switch (statusAt(at)) {
            case REVOKED, LOST -> AccessDecision.denied(DenialReason.REVOKED);
            case SCHEDULED -> AccessDecision.denied(DenialReason.NOT_ACTIVE_YET);
            case EXPIRED -> AccessDecision.denied(DenialReason.EXPIRED);
            case ACTIVE -> opens(accessPoint, targetRoomId)
                    ? AccessDecision.granted()
                    : AccessDecision.denied(DenialReason.OUTSIDE_SCOPE);
        };
    }

    private boolean opens(AccessPoint accessPoint, Long targetRoomId) {
        if (isGuestKeyCard()) return switch (accessPoint) {
            case MAIN_ENTRANCE, ELEVATOR -> true;
            case ROOM_DOOR -> roomId.equals(targetRoomId);
            case SERVICE_CORRIDOR, STORAGE_ROOM -> false;
        };
        return scope != StaffScope.SERVICE_AREAS || accessPoint != AccessPoint.ROOM_DOOR;
    }
}
