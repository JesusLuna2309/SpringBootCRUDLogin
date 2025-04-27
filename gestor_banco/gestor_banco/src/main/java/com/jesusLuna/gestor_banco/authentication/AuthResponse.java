package com.jesusLuna.gestor_banco.authentication;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Clase que representa la respuesta de autenticación tras un login o registro
 * exitoso.
 * 
 * Contiene un token JWT que se utilizará para autorizar futuras peticiones del
 * usuario.
 * 
 * Esta clase se construye y utiliza como objeto de transferencia de datos
 * (DTO). Se emplea principalmente en el controlador {@code AuthController} y en
 * el servicio {@code AuthService}.
 * 
 * Anotaciones de Lombok utilizadas:
 * <ul>
 * <li>{@code @Data}: genera getters, setters, equals, hashCode y toString
 * automáticamente.</li>
 * <li>{@code @Builder}: permite usar el patrón builder para crear
 * instancias.</li>
 * <li>{@code @AllArgsConstructor}: genera un constructor con todos los
 * campos.</li>
 * <li>{@code @NoArgsConstructor}: genera un constructor vacío (sin
 * parámetros).</li>
 * </ul>
 * 
 * @author Jesús
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AuthResponse {

	/**
	 * Token JWT generado tras un inicio de sesión o registro exitoso. Este token se
	 * guarda generalmente en una cookie para autenticar al usuario.
	 */
	String token;

}
