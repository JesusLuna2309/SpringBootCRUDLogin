package com.jesusLuna.gestor_banco.config;

import java.io.IOException;
import java.util.Collection;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.filter.OncePerRequestFilter;

import com.jesusLuna.gestor_banco.jwt.JwtAuthenticationFilter;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

/**
 * Configuración de seguridad de la aplicación que habilita la seguridad web
 * utilizando Spring Security. Esta clase se encarga de definir las políticas de
 * acceso, autenticación y autorización, así como de configurar los filtros
 * necesarios, como el filtro de autenticación JWT.
 */
@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

	private final JwtAuthenticationFilter jwtAuthenticationFilter;
	private final AuthenticationProvider authProvider;
	private final AuthenticationEntryPoint authenticationEntryPoint;

	/**
	 * Configura la cadena de filtros de seguridad. Define las políticas de
	 * autorización, el comportamiento de inicio y cierre de sesión, la gestión de
	 * sesiones y los filtros de seguridad, como el filtro de autenticación JWT.
	 *
	 * @param http La configuración de seguridad HTTP que permite configurar el
	 *             comportamiento de la aplicación.
	 * @return La cadena de filtros de seguridad.
	 * @throws Exception Si ocurre algún error en la configuración de la seguridad.
	 */
	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
		return http

				.exceptionHandling(e -> e.authenticationEntryPoint(authenticationEntryPoint))
				.csrf(csrf -> csrf.disable())
				.authorizeHttpRequests(authRequest -> authRequest
						.requestMatchers("/showLogin").permitAll().requestMatchers("/actlogin").permitAll()
						.requestMatchers("/logout").permitAll().requestMatchers("/register-secret").permitAll()
						.requestMatchers("/").permitAll().anyRequest().authenticated())
				.formLogin(login -> login.loginPage("/showLogin").permitAll())
				.logout(logout -> logout.logoutUrl("/logout") // URL que se invoca para cerrar sesión
						.logoutSuccessUrl("/showLogin?logout=true") // URL de redirección tras el logout
						.invalidateHttpSession(true) // Invalida la sesión HTTP
						.deleteCookies("jwt", "JSESSIONID") // Elimina la cookie JWT y cualquier cookie de sesión
						.permitAll())
				.sessionManagement(
						sessionManager -> sessionManager.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
				.authenticationProvider(authProvider)
				.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
				.addFilterAfter(sameSiteCookieFilter(), UsernamePasswordAuthenticationFilter.class)

				.build();
	}

	/**
	 * Filtro personalizado que agrega la política SameSite=Lax a las cookies JWT.
	 * Esto ayuda a prevenir vulnerabilidades de CSRF (Cross-Site Request Forgery)
	 * en el navegador.
	 *
	 * @return Un filtro que aplica la política SameSite a las cookies.
	 */
	@Bean
	public Filter sameSiteCookieFilter() {
		return new OncePerRequestFilter() {
			@Override
			protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
					FilterChain filterChain) throws ServletException, IOException {

				filterChain.doFilter(request, response);

				Collection<String> headers = response.getHeaders("Set-Cookie");
				boolean firstHeader = true;
				for (String header : headers) {
					if (header.startsWith("jwt=")) {
						String newHeader = header + "; SameSite=Lax";
						if (firstHeader) {
							response.setHeader("Set-Cookie", newHeader);
							firstHeader = false;
						} else {
							response.addHeader("Set-Cookie", newHeader);
						}
					}
				}
			}
		};
	}

}