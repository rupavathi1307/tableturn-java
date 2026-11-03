package com.tableturn.error;

import java.util.Map;

/** 400 /errors/party-too-big: a valid body, but no table in the branch could ever seat that many. */
public class PartyTooBig extends DomainException {

    private final int maxCovers;

    public PartyTooBig(int covers, int maxCovers) {
        super(400, "/errors/party-too-big", "Party too big",
                "No table here seats " + covers + "; the largest seats " + maxCovers);
        this.maxCovers = maxCovers;
    }

    @Override
    public Map<String, Object> properties() {
        return Map.of("maxCovers", maxCovers);
    }
}