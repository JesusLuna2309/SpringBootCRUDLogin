package com.jesusLuna.gestor_banco.service;

import com.jesusLuna.gestor_banco.authentication.AuthResponse;
import com.jesusLuna.gestor_banco.authentication.LoginRequest;
import com.jesusLuna.gestor_banco.authentication.RegisterRequest;

public interface AuthService {

	public AuthResponse login(LoginRequest request);

	public AuthResponse register(RegisterRequest request);

}
