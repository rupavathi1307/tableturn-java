package com.tableturn.table;

import java.util.List;
import java.util.Optional;

import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;

/** The tables table. */
@Repository
public class TableRepository {

    /** One tables row: its number, its seats, its section and the seating on it (null when none). */
    public record Row(String no, int seats, Section section, Long currentSeatingId) {
    }

    private final JdbcClient jdbc;

    public TableRepository(JdbcClient jdbc) {
        this.jdbc = jdbc;
    }

    /** Every table of a branch, lowest number first. */
    public List<Row> all(long branchId) {
        return jdbc.sql("SELECT no, seats, section, current_seating_id FROM tables WHERE branch_id = ? ORDER BY no")
                .param(branchId).query(Row.class).list();
    }

    public Optional<Row> one(long branchId, String no) {
        return jdbc.sql("SELECT no, seats, section, current_seating_id FROM tables WHERE branch_id = ? AND no = ?")
                .params(branchId, no).query(Row.class).optional();
    }

    public void insert(long branchId, String no, int seats, Section section) {
        jdbc.sql("INSERT INTO tables (branch_id, no, seats, section) VALUES (?, ?, ?, ?)")
                .params(branchId, no, seats, section.name()).update();
    }
}