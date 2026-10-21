package com.tableturn.table;

import java.util.Optional;

import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;

/** The branches table. */
@Repository
public class BranchRepository {

    private final JdbcClient jdbc;

public BranchRepository(JdbcClient jdbc) {
        this.jdbc = jdbc;
    }

/** The database id of the branch with this code. */
    public Optional<Long> idOf(String code) {
        return jdbc.sql("SELECT id FROM branches WHERE code = ?").param(code).query(Long.class).optional();
    }
    
public long insert(String code, String name) {
        jdbc.sql("INSERT INTO branches (code, name) VALUES (?, ?)").params(code, name).update();
        return idOf(code).orElseThrow();
    }
}