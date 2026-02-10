package com.marckux.stockman.shared.infrastructure.rest;

import java.util.Map;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HomeController {


  @GetMapping("/api/public/check-health")
  public Map<String, String> checkHealthPublic() {
    return Map.of(
      "status", "ok",
      "message", "Stockman is running! You are in a public route"
    );
  }

  @GetMapping("/api/private/check-health")
  public Map<String, String> checkHealthPrivate(@AuthenticationPrincipal UserDetails user) {
    String message;
    if (user == null) {
      message = "Eres un usuario anónimo.";
    } else {
      message = "Hola, " + user.getUsername() + "! " + "tus roles: " + user.getAuthorities();
    }
    return Map.of("status", "ok", "message", message);
  }
  
}
