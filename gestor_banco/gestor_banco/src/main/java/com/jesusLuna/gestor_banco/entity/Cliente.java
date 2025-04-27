package com.jesusLuna.gestor_banco.entity;

import java.io.Serializable;
import java.time.Year;
import java.util.Set;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

/**
 * Clase que representa un cliente del sistema.
 * 
 * Esta entidad está mapeada a la tabla {@code dam_cliente} en la base de datos.
 * Un cliente tiene atributos como su NIF, nombre, apellidos, fecha de
 * nacimiento, dirección, correo electrónico y número de contacto. Además, puede
 * tener varias cuentas bancarias asociadas.
 * 
 * La clase implementa {@code Serializable} para permitir la serialización de
 * objetos de tipo {@code Cliente}.
 * 
 * Anotaciones de Lombok utilizadas:
 * <ul>
 * <li>{@code @Data}: genera automáticamente los métodos getters, setters,
 * equals, hashCode y toString.</li>
 * <li>{@code @AllArgsConstructor}: genera un constructor que acepta todos los
 * campos como parámetros.</li>
 * <li>{@code @NoArgsConstructor}: genera un constructor sin parámetros.</li>
 * </ul>
 * 
 * Validaciones:
 * <ul>
 * <li>{@code @NotEmpty}: asegura que los campos no estén vacíos.</li>
 * <li>{@code @Pattern}: valida que los campos de texto coincidan con una
 * expresión regular específica.</li>
 * <li>{@code @Email}: valida que el correo electrónico tenga un formato
 * correcto.</li>
 * <li>{@code @AssertTrue}: valida que la edad del cliente esté dentro de un
 * rango permitido (18-120 años).</li>
 * </ul>
 * 
 * @author Jesús
 */
@Entity
@Table(name = "dam_cliente")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Cliente implements Serializable {

	private static final long serialVersionUID = 1L;
	private static final int EDAD_MAXIMA = 120;
	private static final int EDAD_MINIMA = 18;

	/**
	 * Identificador único del cliente. Es la clave primaria en la base de datos.
	 */
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "Id")
	private Long id;

	/**
	 * Número de Identificación Fiscal (NIF) del cliente. Debe tener 8 dígitos
	 * seguidos de una letra. Este campo es único y no puede estar vacío.
	 */
	@Pattern(regexp = "^\\d{8}[A-Za-z]$", message = "Debe tener 8 números seguidos de una letra")
	@NotEmpty(message = "El DNI no puede estar vacío")
	@Column(name = "NIF", nullable = false, unique = true, length = 9)
	private String nif;

	/**
	 * Nombre del cliente. No puede estar vacío.
	 */
	@NotEmpty(message = "El nombre no puede estar vacío")
	@Column(name = "Nombre", nullable = false, length = 50)
	private String nombre;

	/**
	 * Apellidos del cliente. No puede estar vacío.
	 */
	@NotEmpty(message = "El apellido no puede estar vacío")
	@Column(name = "Apellidos", nullable = false, length = 100)
	private String apellidos;

	/**
	 * Año de nacimiento del cliente. No puede ser nulo.
	 */
	@NotNull(message = "El año de nacimiento no puede estar vacío")
	@Column(name = "Anyo_Nacimiento", nullable = false)
	private Integer anyoNacimiento;

	/**
	 * Valida que la edad del cliente esté entre 18 y 120 años. La edad se calcula a
	 * partir del año de nacimiento del cliente.
	 * 
	 * @return {@code true} si la edad del cliente es válida, {@code false} en caso
	 *         contrario.
	 */
	@AssertTrue(message = "La edad debe estar entre " + EDAD_MINIMA + " y " + EDAD_MAXIMA + " años")
	public boolean isEdadValida() {
		if (anyoNacimiento == null) {
			return false;
		}
		int edad = Year.now().getValue() - anyoNacimiento;
		return edad >= EDAD_MINIMA && edad <= EDAD_MAXIMA;
	}

	/**
	 * Dirección de residencia del cliente. No puede estar vacía.
	 */
	@NotEmpty(message = "La dirección no puede estar vacía")
	@Column(name = "Direccion", nullable = false, length = 150)
	private String direccion;

	/**
	 * Correo electrónico del cliente. Debe tener un formato válido de dirección de
	 * correo electrónico. No puede estar vacío y debe ser único.
	 */
	@Email(message = "No es una dirección de correo válida")
	@NotEmpty(message = "El email no puede estar vacío")
	@Column(name = "Email", nullable = false, unique = true, length = 100)
	private String email;

	/**
	 * Número de contacto del cliente. Debe contener entre 9 y 15 dígitos. No puede
	 * estar vacío y debe ser único.
	 */
	@Pattern(regexp = "^[0-9]{9,15}$", message = "El número de contacto debe contener entre 9 y 15 dígitos")
	@NotEmpty(message = "El número de contacto no puede estar vacío")
	@Column(name = "Numero_Telefono", unique = true, nullable = false, length = 15)
	private String numeroContacto;

	/**
	 * Versión del cliente para control de concurrencia. Utilizado para el manejo de
	 * versiones de la entidad en el contexto de la base de datos.
	 */
	@Version
	private int version;

	/**
	 * Relación Many-to-Many entre clientes y cuentas bancarias. Un cliente puede
	 * tener múltiples cuentas bancarias asociadas. La relación se establece
	 * mediante la entidad {@link CuentaBancaria}.
	 */
	@ManyToMany(fetch = FetchType.LAZY, mappedBy = "misClientes")
	private Set<CuentaBancaria> misCuentas;
}
