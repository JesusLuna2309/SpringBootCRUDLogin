package com.jesusLuna.gestor_banco.entity;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.Date;
import org.springframework.format.annotation.DateTimeFormat;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

/**
 * Representa una operación bancaria asociada a una cuenta bancaria.
 * 
 * Esta clase está mapeada a la tabla {@code dam_Operacion} en la base de datos.
 * Una operación bancaria puede ser un ingreso, un retiro o una transferencia.
 * 
 * La clase implementa {@code Serializable} para permitir la serialización de
 * objetos de tipo {@code Operacion}.
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
 * <li>{@code @NotEmpty}: asegura que los campos no sean nulos o vacíos.</li>
 * <li>{@code @NotNull}: asegura que los campos no sean nulos.</li>
 * <li>{@code @Positive}: valida que la cantidad sea mayor que 0.</li>
 * <li>{@code @PastOrPresent}: valida que la fecha de operación no sea
 * futura.</li>
 * </ul>
 * 
 * @author Jesús
 */
@Entity
@Table(name = "dam_Operacion")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Operacion implements Serializable {

	private static final long serialVersionUID = 1L;

	/**
	 * Identificador único de la operación. Este campo es la clave primaria en la
	 * base de datos y se genera automáticamente.
	 */
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "Id")
	private long codigo;

	/**
	 * Descripción de la operación. No puede estar vacía y describe la naturaleza de
	 * la operación (por ejemplo, "Ingreso de dinero").
	 */
	@NotEmpty(message = "La descripción no puede estar vacía")
	@Column(name = "Descripcion", nullable = false)
	private String descripcion;

	/**
	 * Tipo de operación bancaria. Se utiliza la enumeración {@code TipoOperacion}
	 * que define los diferentes tipos de operaciones como ingreso de dinero, retiro
	 * de dinero, entrada por transferencia, etc.
	 */
	@NotNull(message = "El tipo de operación no puede estar vacío")
	@Enumerated(EnumType.STRING) // Guarda el enum como String en la BD
	@Column(name = "Tipo", nullable = false)
	private TipoOperacion tipo;

	/**
	 * Fecha en la que se realizó la operación. No puede ser futura y debe ser una
	 * fecha válida en el pasado o presente.
	 */
	@NotNull(message = "La fecha no puede estar vacía")
	@PastOrPresent(message = "La fecha de operación no puede ser futura")
	@Temporal(TemporalType.DATE)
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	@Column(name = "Fecha", nullable = false)
	private LocalDate fecha;

	/**
	 * Cantidad de dinero involucrada en la operación. La cantidad debe ser mayor
	 * que 0.
	 */
	@NotNull(message = "La cantidad no puede estar vacía")
	@Positive(message = "La cantidad debe ser mayor que 0")
	@Column(name = "Cantidad", nullable = false)
	private Float cantidad;

	/**
	 * Número de cuenta de transferencia asociado a la operación (si aplica). Este
	 * campo puede ser nulo si la operación no involucra una transferencia.
	 */
	@Column(name = "Num_Cuenta_Transferencia")
	private String numCuentaTransferencia;

	/**
	 * Relación Many-to-One entre operaciones y cuentas bancarias. Una operación
	 * está asociada a una única cuenta bancaria.
	 */
	@ManyToOne
	@JoinColumn(name = "cuenta_id", nullable = false)
	private CuentaBancaria cuentaBancaria;

	/**
	 * Enumeración que define los tipos de operaciones bancarias.
	 * 
	 * Los tipos de operación disponibles son:
	 * <ul>
	 * <li>{@code IngresarDinero}: Operación de ingreso de dinero.</li>
	 * <li>{@code RetirarDinero}: Operación de retiro de dinero.</li>
	 * <li>{@code EntradaTransferencia}: Operación de ingreso por
	 * transferencia.</li>
	 * <li>{@code RetiradaTransferencia}: Operación de retiro por
	 * transferencia.</li>
	 * </ul>
	 */
	public enum TipoOperacion {
		IngresarDinero("Ingreso de dinero"), RetirarDinero("Retirada de dinero"),
		EntradaTransferencia("Ingreso via transferencia"), RetiradaTransferencia("Retriada via transferencia");

		private final String nombre;

		TipoOperacion(String nombre) {
			this.nombre = nombre;
		}

		/**
		 * Obtiene el nombre de la operación.
		 * 
		 * @return El nombre de la operación.
		 */
		public String getNombre() {
			return nombre;
		}

	}
}
