package com.tableturn.party;

import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/** A party's public id - W001, W031 - made from its database id, which is never shown itself. */
public final class PartyIds {

    private static final Pattern PUBLIC = Pattern.compile("W(\\d+)");

    private PartyIds() {
    }

    public static String format(long id) {
        return String.format("W%03d", id);
    }

    /** The database id behind a public id, or empty when it is not one. */
    public static Optional<Long> parse(String publicId) {
        Matcher m = PUBLIC.matcher(publicId);
        return m.matches() ? Optional.of(Long.parseLong(m.group(1))) : Optional.empty();
    }
}