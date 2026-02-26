package com.marckux.stockman.shared.utils;

import java.text.Normalizer;
import java.util.Locale;

public final class TextSearchHelper {
    private TextSearchHelper() {
    }

    public static String formatForSearch(String input) {
        if (input == null) {
            return "";
        }

        String normalized = Normalizer.normalize(input, Normalizer.Form.NFD);
        String withoutDiacritics = normalized.replaceAll("\\p{M}+", "");
        String withOrdinals = withoutDiacritics
                .replace("º", "o")
                .replace("ª", "a");
        String lower = withOrdinals.toLowerCase(Locale.ROOT);
        String cleaned = lower.replaceAll("[^\\p{Alnum}\\s]", " ");
        String collapsed = cleaned.replaceAll("\\s+", " ").trim();

        return collapsed;
    }
}
