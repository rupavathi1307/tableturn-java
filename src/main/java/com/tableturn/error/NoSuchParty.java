package com.tableturn.error;

/** 404 /errors/no-such-party. */
public class NoSuchParty extends DomainException {

    public NoSuchParty(String partyId) {
        super(404, "/errors/no-such-party", "No such party", "There is no party " + partyId + " on this list");
    }
}