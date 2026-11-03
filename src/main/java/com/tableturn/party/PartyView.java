package com.tableturn.party;

import java.time.Instant;

/** GET /parties/{partyId}. */
public record PartyView(String partyId, String name, int covers, String state, int position, Instant joinedAt) {
}