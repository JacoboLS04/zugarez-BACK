package com.zugarez.zugarez_BACK.global.utils;

import com.zugarez.zugarez_BACK.global.entity.EntityId;

import java.util.Comparator;
import java.util.List;

/**
 * Utility class for common operations used across the application.
 * Provides helper methods for string manipulation and auto-increment logic.
 */
public class Operations {
    /**
     * Removes leading and trailing brackets from a string.
     * @param message The string to trim
     * @return The string without leading/trailing brackets
     */
    public static String trimBrackets(String message) {
        return message.replaceAll("^\\[|\\]$", "");
    }

    /**
     * Returns the next auto-incremented ID for a list of entities.
     * @param list List of entities implementing EntityId
     * @return The next available ID (1 if the list is empty)
     */
    public static int autoIncrement(List<? extends EntityId > list ) {
        if(list.isEmpty())
            return 1;
        return list.stream().max(Comparator.comparing(EntityId::getId))
                .get().getId() + 1;
    }
}
