package com.marckux.stockman.shared.domain.utils;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

public final class SoftDeleteNameHelper {

  private static final DateTimeFormatter FORMATTER =
    DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");

  private SoftDeleteNameHelper() {
  }

  public static String buildRemovedName(String originalName, Instant now) {
    if (originalName == null) {
      return null;
    }
    String timestamp = FORMATTER.withZone(ZoneId.systemDefault()).format(now);
    return originalName + " - REMOVED - " + timestamp;
  }
}
