package com.tableturn.table;

/** One table on the floor. */
public record TableView(String no, int seats, Section section, TableState state) {
}