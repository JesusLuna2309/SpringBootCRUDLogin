package com.jesusLuna.gestor_banco.jwt;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import com.jesusLuna.gestor_banco.exception.JwtTokenException;
import com.jesusLuna.gestor_banco.service.JwtService;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

/**
 * Filtro de autenticación JWT que intercepta las solicitudes HTTP y valida el token JWT presente en las cookies.
 * Este filtro se asegura de que el token sea válido y esté asociado al usuario correspondiente antes de permitir
 * el acceso a los recursos protegidos.
 * 
 * @author Jesús Luna Romero
 */
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    // Servicio para manejar la lógica de JWT
    private final JwtService jwtServiceI;
    
    // Servicio para cargar detalles de usuario por nombre de usuario
    private final UserDetailsService userDetailsService;
    
    private final AuthenticationEntryPoint authenticationEntryPoint;
    
    private static final Logger logger = LoggerFactory.getLogger(JwtAuthenticationFilter.class);



    /**
     * Método que filtra las solicitudes HTTP para verificar la autenticación basada en un token JWT.
     * 
     * Este método extrae el token JWT de las cookies de la solicitud, valida su autenticidad y, si es válido,
     * establece la autenticación del usuario en el contexto de seguridad de Spring.
     * 
     * @param request La solicitud HTTP
     * @param response La respuesta HTTP
     * @param filterChain El filtro de la cadena de filtros
     * @throws ServletException Si ocurre un error al procesar la solicitud
     * @throws IOException Si ocurre un error de entrada/salida
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        try {
            // Obtener el token JWT de las cookies
            final String token = getTokenFromCookie(request);

            // Si no hay token, se continúa con la cadena de filtros
            if (token == null) {
                filterChain.doFilter(request, response);
                return;
            }

            // Obtener el nombre de usuario asociado al token
            final String username = jwtServiceI.getUsernameFromToken(token);

            // Si el nombre de usuario es válido y no hay autenticación en el contexto, validamos el token
            if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                UserDetails userDetails = userDetailsService.loadUserByUsername(username);

                // Validar que el token coincide con el usuario
                if (!jwtServiceI.isTokenValid(token, userDetails)) {
                    throw new JwtTokenException("El token JWT no es válido o no coincide con el usuario.");
                }

                // Crear el token de autenticación para el usuario
                UsernamePasswordAuthenticationToken authToken =
                        new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());

                // Establecer los detalles de la autenticación
                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }

        } catch (ExpiredJwtException e) {
        	logger.warn("JWT inválido o expirado para la IP: {}", request.getRemoteAddr());
            throw new JwtTokenException("El token JWT ha expirado." + e.getMessage());
        } catch (JwtException e) {
        	logger.warn("JWT inválido o expirado para la IP: {}", request.getRemoteAddr());
        	 // Delegar el error al entry point de autenticación
            authenticationEntryPoint.commence(request, response, 
                new InsufficientAuthenticationException(e.getMessage()));
            return;
        } catch (Exception e) {

            throw new JwtTokenException("Error inesperado durante la autenticación JWT: " + e.getMessage());
        }

        // Continuar con la cadena de filtros
        filterChain.doFilter(request, response);
    }

    /**
     * Método auxiliar que extrae el token JWT de las cookies de la solicitud HTTP.
     * 
     * @param request La solicitud HTTP
     * @return El token JWT si se encuentra en las cookies, o null si no se encuentra
     */
    private String getTokenFromCookie(HttpServletRequest request) {
        if (request.getCookies() != null) {
            // Recorrer las cookies para encontrar el token
            for (Cookie cookie : request.getCookies()) {
                if (cookie.getName().equals("jwt")) {
                    return cookie.getValue();
                }
            }
        }
        return null;
    }
}
