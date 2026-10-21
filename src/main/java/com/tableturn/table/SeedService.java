package com.tableturn.table;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.tableturn.config.TableTurnSettings;

/**
 * Loads the Baner branch and its 22 tables (seed/tables.csv) - once. A second start finds the branch and adds
 * nothing.
 */
@Service
public class SeedService {

    static final String BRANCH = "Baner";

    private final BranchRepository branches;
    private final TableRepository tables;
    private final TableTurnSettings settings;

    public SeedService(BranchRepository branches, TableRepository tables, TableTurnSettings settings) {
        this.branches = branches;
        this.tables = tables;
        this.settings = settings;
    }

    @Transactional
    public void seed() {
        if (branches.idOf(BRANCH).isPresent()) {
            return;
        }
        long branch = branches.insert(BRANCH, BRANCH);
        for (String[] row : csv("tables.csv")) {                // no,seats,section
            tables.insert(branch, row[0], Integer.parseInt(row[1]), Section.valueOf(row[2]));
        }
    }

    /** The rows of a seed file, header skipped, each split on commas. */
    private List<String[]> csv(String file) {
        try {
            return Files.readAllLines(Path.of(settings.seedDir(), file)).stream().skip(1)
                    .filter(line -> !line.isBlank()).map(line -> line.trim().split("\\s*,\\s*")).toList();
        } catch (IOException e) {
            throw new UncheckedIOException("cannot read the seed file " + file, e);
        }
    }
}