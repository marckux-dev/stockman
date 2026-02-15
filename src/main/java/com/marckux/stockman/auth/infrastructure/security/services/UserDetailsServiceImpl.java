package com.marckux.stockman.auth.infrastructure.security.services;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.marckux.stockman.auth.domain.ports.out.UserRepositoryPort;
import com.marckux.stockman.auth.infrastructure.security.mappers.SecurityUserMapper;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

  private final UserRepositoryPort repository;
  private final SecurityUserMapper mapper;

  @Override
  public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
    return repository.findByEmail(email)
        .map(mapper::toUserDetails)
        .orElseThrow(
            () -> new UsernameNotFoundException("Usuario con email " + email + " no encontrado"));

  }

}
