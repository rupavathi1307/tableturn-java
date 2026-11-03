package com.tableturn.party;

import java.time.Instant;

/** POST /waitlist 201: the party's id and its place in the queue. */
public record JoinResponse(String partyId, String name, int covers, int position, Instant joinedAt) {
}