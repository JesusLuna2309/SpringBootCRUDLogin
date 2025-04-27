package com.jesusLuna.gestor_banco.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.jesusLuna.gestor_banco.authentication.AuthResponse;
import com.jesusLuna.gestor_banco.authentication.LoginRequest;
import com.jesusLuna.gestor_banco.authentication.RegisterRequest;
import com.jesusLuna.gestor_banco.entity.User;
import com.jesusLuna.gestor_banco.exception.UsuarioYaExisteException;
import com.jesusLuna.gestor_banco.repository.UserRepository;

import lombok.RequiredArgsConstructor;

/**
 * Servicio que implementa la lógica de autenticación y registro de usuarios.
 * Utiliza Spring Security para la autenticación y JWT para la generación de
 * tokens.
 */
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

	/**
	 * Repositorio para el acceso a datos de usuarios.
	 */
	@Autowired
	private final UserRepository userRepository;

	/**
	 * Servicio para la generación y validación de JWTs.
	 */
	private final JwtService jwtServiceI;

	/**
	 * Codificador de contraseñas (normalmente BCrypt).
	 */
	private final PasswordEncoder passwordEncoder;

	/**
	 * Administrador de autenticación proporcionado por Spring Security.
	 */
	private final AuthenticationManager authenticationManager;

	/**
	 * Autentica a un usuario y genera un token JWT si las credenciales son válidas.
	 *
	 * @param request objeto que contiene el nombre de usuario y la contraseña.
	 * @return respuesta con el token JWT.
	 * @throws RuntimeException si las credenciales son inválidas.
	 */
	@Override
	public AuthResponse login(LoginRequest request) {
		try {
			authenticationManager.authenticate(
					new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword()));
			UserDetails user = userRepository.findByUsername(request.getUsername()).orElseThrow();
			String token = jwtServiceI.getToken(user);
			return AuthResponse.builder().token(token).build();
		} catch (BadCredentialsException e) {
			throw new RuntimeException("Credenciales inválidas");
		}
	}

	/**
	 * Registra un nuevo usuario en el sistema si el nombre de usuario no está
	 * registrado.
	 *
	 * @param request objeto que contiene los datos del nuevo usuario.
	 * @return respuesta con el token JWT generado para el nuevo usuario.
	 * @throws UsuarioYaExisteException si el nombre de usuario ya existe.
	 */
	@Override
	public AuthResponse register(RegisterRequest request) {

		if (userRepository.findByUsername(request.getUsername()).isPresent()) {
			throw new UsuarioYaExisteException("El nombre de usuario ya está registrado");
		}

		User user = User.builder().username(request.getUsername()).nombre(request.getNombre())
				.apellidos(request.getApellidos()).email(request.getEmail())
				.password(passwordEncoder.encode(request.getPassword())).role(request.getRole()).build();

		userRepository.save(user);

		return AuthResponse.builder().token(jwtServiceI.getToken(user)).build();
	}
}
