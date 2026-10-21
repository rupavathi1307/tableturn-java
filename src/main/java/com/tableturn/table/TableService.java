package com.tableturn.table;

import java.util.List;

import org.springframework.stereotype.Service;

import com.tableturn.error.NoSuchBranch;
import com.tableturn.error.NoSuchTable;

/** The floor: every table with its state, derived from its current seating on every read. */
@Service
public class TableService {

    private final BranchRepository branches;
    private final TableRepository tables;

    public TableService(BranchRepository branches, TableRepository tables) {
        this.branches = branches;
        this.tables = tables;
    }

    /** The floor, optionally only one section and/or one state; `free` counts the free tables among those shown. */
    public FloorView floor(String branch, Section section, TableState state) {
        List<TableView> shown = tables.all(branchId(branch)).stream()
                .map(TableService::view)
                .filter(t -> section == null || t.section() == section)
                .filter(t -> state == null || t.state() == state)
                .toList();
                int free = (int) shown.stream().filter(t -> t.state() == TableState.free).count();
        return new FloorView(branch, free, shown);
    }
 
    public TableView table(String branch, String no) {
        return view(tables.one(branchId(branch), no).orElseThrow(() -> new NoSuchTable(branch, no)));
    }
 
    private long branchId(String branch) {
        return branches.idOf(branch).orElseThrow(() -> new NoSuchBranch(branch));
    }
 
    /** Derived on every read: free when no seating is current. */
    private static TableView view(TableRepository.Row row) {
        TableState state = row.currentSeatingId() == null ? TableState.free : TableState.seated;
        return new TableView(row.no(), row.seats(), row.section(), state);
    }
}