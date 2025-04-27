package com.jesusLuna.gestor_banco.service;

import org.springframework.security.core.userdetails.UserDetails;

public interface JwtService {

	public String getToken(UserDetails user);

	public boolean isTokenValid(String token, UserDetails userDetails);

	public boolean isTokenValid(String token);

	public String getUsernameFromToken(String token);
}
