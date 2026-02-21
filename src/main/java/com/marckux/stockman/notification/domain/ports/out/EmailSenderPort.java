package com.marckux.stockman.notification.domain.ports.out;

public interface EmailSenderPort {

  void sendHtml(String from, String to, String subject, String htmlBody);
}
