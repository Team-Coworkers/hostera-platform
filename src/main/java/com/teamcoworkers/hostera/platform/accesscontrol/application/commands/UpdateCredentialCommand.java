package com.teamcoworkers.hostera.platform.accesscontrol.application.commands;

public record UpdateCredentialCommand(Long credentialId, CredentialData data) {
}
