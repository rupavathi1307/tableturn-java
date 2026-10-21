package com.tableturn.table;

import java.util.List;

/** The floor of a branch: the tables shown and how many of them are free. */
public record FloorView(String branch, int free, List<TableView> tables) {
}