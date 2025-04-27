package com.jesusLuna.gestor_banco.authentication;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Clase que representa la solicitud de inicio de sesión.
 * 
 * Contiene las credenciales del usuario: nombre de usuario y contraseña. Esta
 * clase es utilizada para enviar los datos de autenticación al servicio de
 * autenticación cuando el usuario intenta iniciar sesión en el sistema.
 * 
 * Anotaciones de Lombok utilizadas:
 * <ul>
 * <li>{@code @Data}: genera automáticamente los métodos getters, setters,
 * equals, hashCode y toString.</li>
 * <li>{@code @Builder}: permite construir una instancia de esta clase
 * utilizando el patrón Builder.</li>
 * <li>{@code @AllArgsConstructor}: genera un constructor que acepta todos los
 * campos como parámetros.</li>
 * <li>{@code @NoArgsConstructor}: genera un constructor sin parámetros.</li>
 * </ul>
 * 
 * @author Jesús
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class LoginRequest {

	/**
	 * Nombre de usuario del cliente que intenta iniciar sesión. Este valor es
	 * utilizado para identificar al usuario dentro del sistema.
	 */
	String username;

	/**
	 * Contraseña asociada al {@code username}. Esta contraseña es validada en el
	 * proceso de autenticación.
	 */
	String password;
}
