package com.jesusLuna.gestor_banco.config;

import java.io.IOException;

import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

/**
 * Implementación personalizada de {@link AuthenticationEntryPoint} que maneja
 * los accesos no autorizados. Este componente se activa cuando un usuario
 * intenta acceder a un recurso protegido sin estar autenticado. En caso de
 * acceso no autorizado, se registra la información del acceso y se devuelve una
 * respuesta con un mensaje de error en formato JSON y un código de estado 401
 * (No autorizado).
 */
@Slf4j
@Component
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {

	/**
	 * Método que se ejecuta cuando un usuario intenta acceder a un recurso
	 * protegido sin estar autenticado. Registra los detalles del acceso no
	 * autorizado y envía una respuesta con un mensaje de error y el código de
	 * estado HTTP correspondiente (401 - No autorizado).
	 * 
	 * @param request       La solicitud HTTP entrante que contiene la información
	 *                      del recurso solicitado y la IP del cliente.
	 * @param response      La respuesta HTTP que se enviará al cliente.
	 * @param authException La excepción de autenticación que contiene detalles
	 *                      sobre el error.
	 * @throws IOException      Si ocurre un error de entrada/salida al escribir la
	 *                          respuesta.
	 * @throws ServletException Si ocurre un error de servlet.
	 */
	@Override
	public void commence(HttpServletRequest request, HttpServletResponse response,
			AuthenticationException authException) throws IOException, ServletException {
		log.warn("Acceso no autorizado a: {} | IP: {} | Error: {}", request.getRequestURI(), request.getRemoteAddr(),
				authException.getMessage());

		response.setContentType("application/json");
		response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
		response.getWriter().write("{\"error\": \"No estás autorizado para acceder a este recurso.\"}");

	}
}