package com.jesusLuna.gestor_banco.authentication;


import com.jesusLuna.gestor_banco.entity.User.Role;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Clase que representa la solicitud de registro de un nuevo usuario.
 * 
 * Esta clase contiene los datos necesarios para crear un nuevo usuario en el
 * sistema: nombre de usuario, contraseña, nombre, apellidos, correo electrónico
 * y el rol asignado al usuario.
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
public class RegisterRequest {

	/**
	 * Nombre de usuario para el nuevo usuario. Este valor se utiliza para
	 * identificar al usuario dentro del sistema.
	 */
	private String username;

	/**
	 * Contraseña asociada al {@code username}. Esta contraseña será utilizada para
	 * autenticar al usuario durante el inicio de sesión.
	 */
	private String password;

	/**
	 * Nombre real del nuevo usuario. Este campo se utiliza para almacenar el nombre
	 * completo del usuario.
	 */
	private String nombre;

	/**
	 * Apellidos del nuevo usuario. Este campo almacena los apellidos del usuario.
	 */
	private String apellidos;

	/**
	 * Dirección de correo electrónico del usuario. Este campo se utiliza para
	 * contactar al usuario y también para validaciones de cuenta.
	 */
	private String email;

	/**
	 * Rol asignado al nuevo usuario. Este campo define el nivel de permisos y
	 * acceso dentro del sistema. Se espera que el rol sea uno de los valores
	 * definidos en la enumeración {@link Role}.
	 */
	private Role role;

}
