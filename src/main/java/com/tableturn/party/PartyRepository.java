package com.tableturn.party;

import java.time.Instant;
import java.util.Optional;

import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

/** The parties table. */
@Repository
public class PartyRepository {

    /** One party as the screens show it (the phone number is never shown). */
    public record Row(long id, String name, int covers, String state, int position, Instant joinedAt) {
    }

    private final JdbcClient jdbc;

    public PartyRepository(JdbcClient jdbc) {
        this.jdbc = jdbc;
    }

    /** A new waiting party; its position (1 + the parties waiting in the branch) is counted by the same INSERT. */
    public long insert(long branchId, String name, int covers, String phone, Instant joinedAt) {
        KeyHolder key = new GeneratedKeyHolder();
        jdbc.sql("""
                        INSERT INTO parties (branch_id, name, covers, phone, state, joined_at, position)
                        SELECT ?, ?, ?, ?, 'waiting', ?, 1 + COUNT(*)
                        FROM parties WHERE branch_id = ? AND state = 'waiting'""")
                .params(branchId, name, covers, phone, joinedAt, branchId).update(key, "id");
        return key.getKeyAs(Long.class);
    }

    public Optional<Row> find(long branchId, long id) {
        return jdbc.sql("""
                        SELECT id, name, covers, state, position, joined_at
                        FROM parties WHERE branch_id = ? AND id = ?""")
                .params(branchId, id).query(Row.class).optional();
    }
}