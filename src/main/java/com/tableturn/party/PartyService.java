package com.tableturn.party;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;

import org.springframework.stereotype.Service;

import com.tableturn.config.TableTurnSettings;
import com.tableturn.error.NoSuchParty;
import com.tableturn.error.PartyTooBig;
import com.tableturn.table.BranchRepository;
import com.tableturn.table.TableRepository;

/** The waitlist: parties join it, quoted a wait, and are read back. Every time comes from the injected clock. */
@Service
public class PartyService {

    private final BranchRepository branches;
    private final TableRepository tables;
    private final PartyRepository parties;
    private final Clock clock;
    private final TableTurnSettings settings;

    public PartyService(BranchRepository branches, TableRepository tables, PartyRepository parties, Clock clock,
                        TableTurnSettings settings) {
        this.branches = branches;
        this.tables = tables;
        this.parties = parties;
        this.clock = clock;
        this.settings = settings;
    }

    /**
     * A party no table in the branch could ever seat is refused before anything is written. The quote is
     * the default-quote-minutes setting, stored with the moment the party joined.
     */
    public JoinResponse join(String branch, String name, int covers, String phone) {
        long branchId = branchId(branch);
        int maxCovers = tables.largest(branchId);
        if (covers > maxCovers) {
            throw new PartyTooBig(covers, maxCovers);
        }
        Instant now = clock.instant().truncatedTo(ChronoUnit.SECONDS);
        long id = parties.insert(branchId, name, covers, phone, now, settings.defaultQuoteMinutes());
        PartyRepository.Row row = parties.find(branchId, id).orElseThrow();
        return new JoinResponse(PartyIds.format(id), row.name(), row.covers(), row.position(), row.joinedAt(),
                row.quotedMinutes(), row.quotedAt());
    }

    public PartyView party(String branch, String partyId) {
        long branchId = branchId(branch);
        PartyRepository.Row row = PartyIds.parse(partyId).flatMap(id -> parties.find(branchId, id))
                .orElseThrow(() -> new NoSuchParty(partyId));
        return new PartyView(partyId, row.name(), row.covers(), row.state(), row.position(), row.joinedAt(),
                row.quotedMinutes(), row.quotedAt(), row.waitedMinutes());
    }

    /**
     * The real wait, stamped once at seating: whole minutes since the
     * party joined, rounded down, and stored - a read never recomputes it.
     */
    public int stampWait(long partyId, Instant joinedAt, Instant seatedAt) {
        int waited = (int) Duration.between(joinedAt, seatedAt).toMinutes();
        parties.stampWait(partyId, waited);
        return waited;
    }

    /** The waitlist has no branch in its path: the one branch is seeded at start-up, so it is always there. */
    private long branchId(String branch) {
        return branches.idOf(branch).orElseThrow();
    }
}