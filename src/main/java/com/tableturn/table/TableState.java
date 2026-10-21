package com.tableturn.table;

/** A table's state, derived on every read from its current seating - never stored. */
public enum TableState {
    free, seated, uncleared
}