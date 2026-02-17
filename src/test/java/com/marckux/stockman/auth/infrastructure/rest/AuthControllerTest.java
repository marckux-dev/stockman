package com.marckux.stockman.auth.infrastructure.rest;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.marckux.stockman.auth.application.ports.in.LoginUseCase;
import com.marckux.stockman.auth.application.ports.in.RegisterUseCase;
import com.marckux.stockman.auth.infrastructure.security.config.SecurityConfig;
import com.marckux.stockman.auth.infrastructure.security.services.JwtService;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AuthController.class)
@Import(SecurityConfig.class)
public class AuthControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @MockitoBean
  private LoginUseCase loginUseCase;

  @MockitoBean
  private RegisterUseCase registerUseCase;

  @MockitoBean
  private UserDetailsService userDetailsService;

  @MockitoBean
  private JwtService jwtService;

  @Test
  void loginShouldBeAccessibleByPublic() throws Exception {
    String loginJson = "{\"email\":\"test@mail.com\", \"password\":\"Abcd1234\"}";
    mockMvc.perform(
        post("/api/auth/login")
            .contentType(MediaType.APPLICATION_JSON)
            .content(loginJson))
        .andExpect(status().isOk());
  }
}
