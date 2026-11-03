package com.tableturn.party;

import java.net.URI;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;

/** The waitlist. There is one branch, Baner. Parses the request and delegates; no rules live here. */
@RestController
public class PartyController {

    static final String BRANCH = "Baner";

    private final PartyService parties;

    public PartyController(PartyService parties) {
        this.parties = parties;
    }

    @PostMapping("/waitlist")
    public ResponseEntity<JoinResponse> join(@Valid @RequestBody JoinRequest body) {
        JoinResponse joined = parties.join(BRANCH, body.name(), body.covers(), body.phone());
        return ResponseEntity.created(URI.create("/parties/" + joined.partyId())).body(joined);
    }

    @GetMapping("/parties/{partyId}")
    public PartyView party(@PathVariable String partyId) {
        return parties.party(BRANCH, partyId);
    }
}