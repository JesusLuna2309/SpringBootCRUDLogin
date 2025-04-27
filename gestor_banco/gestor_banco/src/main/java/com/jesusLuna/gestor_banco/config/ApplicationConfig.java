package com.jesusLuna.gestor_banco.config;


import lombok.extern.slf4j.Slf4j;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.jesusLuna.gestor_banco.repository.UserRepository;

/**
 * Configuración de la autenticación y la gestión de usuarios en la aplicación.
 * Esta clase configura los beans necesarios para la autenticación y el manejo
 * de contraseñas en la aplicación, incluyendo el proveedor de autenticación, el
 * servicio de detalles de usuario y el codificador de contraseñas.
 */
@Slf4j
@Configuration
public class ApplicationConfig {

    private final UserRepository userRepository;

    
    /**
     * Constructor para la configuración de la clase {@link ApplicationConfig}.
     * 
     * @param userRepository Repositorio de usuarios que se utilizará para cargar detalles de usuario durante la autenticación.
     */
    ApplicationConfig(UserRepository userRepository) {
        this.userRepository = userRepository;
    }
	
    /**
     * Crea un bean de {@link AuthenticationManager} utilizando la configuración de autenticación proporcionada.
     * Este bean es responsable de manejar la autenticación de los usuarios.
     * 
     * @param config Configuración de autenticación proporcionada por Spring Security.
     * @return Un bean de {@link AuthenticationManager}.
     * @throws Exception Si ocurre algún error al obtener el AuthenticationManager.
     */
	@Bean
	public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
		return config.getAuthenticationManager();
	}

	/**
     * Crea un bean de {@link AuthenticationProvider} que proporciona la autenticación a través de la validación de las credenciales.
     * Utiliza un {@link DaoAuthenticationProvider} que se encarga de autenticar a los usuarios basándose en sus credenciales.
     * 
     * @return Un bean de {@link AuthenticationProvider} configurado.
     */
	@Bean
	public AuthenticationProvider authenticationProvider() {
		
		DaoAuthenticationProvider authenticationProvider = new DaoAuthenticationProvider();
		authenticationProvider.setUserDetailsService(userDetailService());
		authenticationProvider.setPasswordEncoder(passwordEncoder());
		return authenticationProvider;
	}

	/**
     * Crea un bean de {@link UserDetailsService} que se utiliza para cargar los detalles del usuario durante el proceso de autenticación.
     * Si el nombre de usuario no se encuentra, lanza una excepción {@link UsernameNotFoundException}.
     * 
     * @return Un bean de {@link UserDetailsService} que consulta el repositorio de usuarios.
     */
	@Bean
	public UserDetailsService userDetailService() {
		return username -> userRepository.findByUsername(username)
				.orElseThrow(()-> {
					log.warn("Intento de login con usuario inexistente: {}", username);
					return new UsernameNotFoundException("User not found");
				});
	}

	/**
     * Crea un bean de {@link PasswordEncoder} que se utiliza para codificar las contraseñas.
     * En este caso, se utiliza {@link BCryptPasswordEncoder} para un hashing seguro de las contraseñas.
     * 
     * @return Un bean de {@link PasswordEncoder} configurado con {@link BCryptPasswordEncoder}.
     */
	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}
	
}
