package com.jesusLuna.gestor_banco.entity;

import java.util.Collection;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import jakarta.annotation.Generated;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Representa un usuario del sistema. Implementa la interfaz {@code UserDetails}
 * de Spring Security, proporcionando la información de autenticación y
 * autorización del usuario.
 * 
 * Esta clase está mapeada a la tabla {@code dam_User} en la base de datos.
 * 
 * Anotaciones de Lombok utilizadas:
 * <ul>
 * <li>{@code @Data}: genera automáticamente los métodos getters, setters,
 * equals, hashCode y toString.</li>
 * <li>{@code @Builder}: permite crear objetos de esta clase utilizando el
 * patrón de diseño Builder.</li>
 * <li>{@code @NoArgsConstructor}: genera un constructor sin parámetros.</li>
 * <li>{@code @AllArgsConstructor}: genera un constructor que acepta todos los
 * campos como parámetros.</li>
 * </ul>
 * 
 * Las validaciones se aplican a los campos de la clase para garantizar que el
 * nombre de usuario, correo electrónico y contraseña cumplan con los requisitos
 * establecidos.
 * 
 * @author Jesús
 */
@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "dam_User")
public class User implements UserDetails {

	/**
	 * Serial version UID para la clase {@code User}.
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Identificador único del usuario. Este campo es la clave primaria en la base
	 * de datos y se genera automáticamente.
	 */
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	/**
	 * Nombre de usuario único que se utiliza para autenticar al usuario en el
	 * sistema. Debe tener entre 4 y 50 caracteres y no puede estar vacío.
	 */
	@NotBlank(message = "El nombre de usuario no puede estar vacío")
	@Size(min = 4, max = 50, message = "El nombre de usuario debe tener entre 4 y 50 caracteres")
	@Column(nullable = false, unique = true, length = 50)
	private String username;

	/**
	 * Nombre del usuario. Este campo no puede estar vacío.
	 */
	@NotBlank(message = "El nombre no puede estar vacío")
	@Column(nullable = false, length = 100)
	private String nombre;

	/**
	 * Apellidos del usuario. Este campo no puede estar vacío.
	 */
	@NotBlank(message = "Los apellidos no pueden estar vacíos")
	@Column(nullable = false, length = 100)
	private String apellidos;

	/**
	 * Contraseña del usuario. Debe tener al menos 8 caracteres y no puede estar
	 * vacía.
	 */
	@NotBlank(message = "La contraseña no puede estar vacía")
	@Size(min = 8, message = "La contraseña debe tener al menos 8 caracteres")
	@Column(nullable = false)
	private String password;

	/**
	 * Dirección de correo electrónico del usuario. Debe ser una dirección de correo
	 * válida y no puede estar vacía.
	 */
	@Email(message = "Debe ser una dirección de correo válida")
	@NotBlank(message = "El email no puede estar vacío")
	@Column(nullable = false, unique = true, length = 100)
	private String email;

	/**
	 * Rol del usuario. Los roles disponibles son {@code Admin} o {@code User}. El
	 * rol determina los privilegios de acceso del usuario en el sistema.
	 */
	@Enumerated(EnumType.STRING) // Guarda el enum como String en la BD
	@Column(nullable = false)
	private Role role;

	/**
	 * Enum que representa los roles disponibles para el usuario. Los roles son
	 * {@code Admin} para administradores y {@code User} para usuarios comunes.
	 */
	public enum Role {
		Admin, User
	}

	/**
	 * Retorna las autoridades del usuario (es decir, los roles de acceso).
	 * Implementa el método {@code getAuthorities} de la interfaz
	 * {@code UserDetails}.
	 * 
	 * @return Una colección de autoridades que contienen el rol del usuario.
	 */
	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		return List.of(new SimpleGrantedAuthority(role.name()));
	}

	/**
	 * Indica si la cuenta del usuario ha expirado. En este caso, la cuenta nunca
	 * expira, por lo que siempre retorna {@code true}. Implementa el método
	 * {@code isAccountNonExpired} de la interfaz {@code UserDetails}.
	 * 
	 * @return {@code true}, ya que la cuenta nunca expira.
	 */
	@Override
	public boolean isAccountNonExpired() {
		return true;
	}

	/**
	 * Indica si la cuenta del usuario está bloqueada. En este caso, la cuenta nunca
	 * está bloqueada, por lo que siempre retorna {@code true}. Implementa el método
	 * {@code isAccountNonLocked} de la interfaz {@code UserDetails}.
	 * 
	 * @return {@code true}, ya que la cuenta nunca está bloqueada.
	 */
	@Override
	public boolean isAccountNonLocked() {
		return true;
	}

	/**
	 * Indica si las credenciales del usuario han expirado. En este caso, las
	 * credenciales nunca expiran, por lo que siempre retorna {@code true}.
	 * Implementa el método {@code isCredentialsNonExpired} de la interfaz
	 * {@code UserDetails}.
	 * 
	 * @return {@code true}, ya que las credenciales nunca expiran.
	 */
	@Override
	public boolean isCredentialsNonExpired() {
		return true;
	}

	/**
	 * Indica si el usuario está habilitado. En este caso, siempre se devuelve
	 * {@code true}, ya que el usuario está habilitado por defecto. Implementa el
	 * método {@code isEnabled} de la interfaz {@code UserDetails}.
	 * 
	 * @return {@code true}, ya que el usuario está habilitado.
	 */
	@Override
	public boolean isEnabled() {
		return true;
	}
}
