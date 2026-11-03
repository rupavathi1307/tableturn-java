package com.tableturn.party;

import java.time.Instant;

/** POST /waitlist 201: the party's id, its place in the queue and the wait it was quoted. */
public record JoinResponse(String partyId, String name, int covers, int position, Instant joinedAt, int quotedMinutes,
                           Instant quotedAt) {
}