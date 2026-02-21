package com.marckux.stockman.shared.infrastructure.rest;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class AuthPagesController {

  @GetMapping("/")
  public String home() {
    return "forward:/configure-password.html";
  }

  @GetMapping("/configure-password")
  public String configurePasswordPage() {
    return "forward:/configure-password.html";
  }

  @GetMapping("/reset-password")
  public String resetPasswordPage() {
    return "forward:/reset-password.html";
  }
}
