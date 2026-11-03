package com.tableturn.party;

import java.time.Clock;
import java.time.temporal.ChronoUnit;

import org.springframework.stereotype.Service;

import com.tableturn.error.NoSuchParty;
import com.tableturn.error.PartyTooBig;
import com.tableturn.table.BranchRepository;
import com.tableturn.table.TableRepository;

/** The waitlist: parties join it and are read back. */
@Service
public class PartyService {

    private final BranchRepository branches;
    private final TableRepository tables;
    private final PartyRepository parties;
    private final Clock clock;

    public PartyService(BranchRepository branches, TableRepository tables, PartyRepository parties, Clock clock) {
        this.branches = branches;
        this.tables = tables;
        this.parties = parties;
        this.clock = clock;
    }

    /** A party no table in the branch could ever seat is refused before anything is written. */
    public JoinResponse join(String branch, String name, int covers, String phone) {
        long branchId = branchId(branch);
        int maxCovers = tables.largest(branchId);
        if (covers > maxCovers) {
            throw new PartyTooBig(covers, maxCovers);
        }
        long id = parties.insert(branchId, name, covers, phone, clock.instant().truncatedTo(ChronoUnit.SECONDS));
        PartyRepository.Row row = parties.find(branchId, id).orElseThrow();
        return new JoinResponse(PartyIds.format(id), row.name(), row.covers(), row.position(), row.joinedAt());
    }

    public PartyView party(String branch, String partyId) {
        long branchId = branchId(branch);
        PartyRepository.Row row = PartyIds.parse(partyId).flatMap(id -> parties.find(branchId, id))
                .orElseThrow(() -> new NoSuchParty(partyId));
        return new PartyView(partyId, row.name(), row.covers(), row.state(), row.position(), row.joinedAt());
    }

    /** The waitlist has no branch in its path: the one branch is seeded at start-up, so it is always there. */
    private long branchId(String branch) {
        return branches.idOf(branch).orElseThrow();
    }
}