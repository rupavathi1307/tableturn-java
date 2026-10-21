package com.tableturn.table;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/** The floor. Parses the request and delegates; no rules live here. */
@RestController
@RequestMapping("/branches/{branch}/tables")
public class TableController {

    private final TableService tables;

    public TableController(TableService tables) {
        this.tables = tables;
    }

    @GetMapping
    public FloorView floor(@PathVariable String branch,
                           @RequestParam(required = false) Section section,
                           @RequestParam(required = false) TableState state) {
        return tables.floor(branch, section, state);
    }

    @GetMapping("/{no}")
    public TableView table(@PathVariable String branch, @PathVariable String no) {
        return tables.table(branch, no);
    }
}