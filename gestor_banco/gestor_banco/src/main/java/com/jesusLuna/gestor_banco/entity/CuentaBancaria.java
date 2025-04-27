package com.jesusLuna.gestor_banco.entity;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.Date;
import java.util.List;

import org.springframework.format.annotation.DateTimeFormat;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

/**
 * Representa una cuenta bancaria asociada a uno o más clientes.
 * 
 * Esta clase está mapeada a la tabla {@code dam_Cuenta_Bancaria} en la base de
 * datos. La cuenta bancaria tiene atributos como el número de cuenta, el tipo
 * de cuenta, la fecha de creación, el saldo actual y las operaciones asociadas.
 * Además, la cuenta puede tener múltiples clientes asociados.
 * 
 * La clase implementa {@code Serializable} para permitir la serialización de
 * objetos de tipo {@code CuentaBancaria}.
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
 * <li>{@code @NotNull}: asegura que los campos no sean nulos.</li>
 * <li>{@code @AssertTrue}: valida que la fecha de creación no sea futura.</li>
 * </ul>
 * 
 * @author Jesús
 */
@Entity
@Table(name = "dam_Cuenta_Bancaria")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CuentaBancaria implements Serializable {

	private static final long serialVersionUID = 1L;

	/**
	 * Identificador único de la cuenta bancaria. Este campo es la clave primaria en
	 * la base de datos y debe ser único.
	 */
	@Id
	@Column(name = "Numero_Cuenta", length = 34, nullable = false, unique = true)
	private String numeroCuenta;

	/**
	 * Tipo de cuenta bancaria. Se utiliza la enumeración {@code TipoCuenta} que
	 * puede ser de tipo Ahorro, Corriente o Empresarial.
	 * 
	 * @see TipoCuenta
	 */
	@Enumerated(EnumType.STRING) // Asegura que se almacene como un string en la base de datos
	@Column(name = "Tipo_Cuenta", nullable = false, length = 50)
	private TipoCuenta tipoCuenta;

	/**
	 * Fecha de creación de la cuenta bancaria. No puede estar vacía. Se valida que
	 * no sea una fecha futura.
	 */
	@NotNull(message = "La fecha de creación no puede estar vacía")
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	@Temporal(TemporalType.DATE)
	@Column(name = "Fecha_Creacion", nullable = false)
	private LocalDate fechaCreacion;

	/**
	 * Valida que la fecha de creación de la cuenta no sea futura.
	 * 
	 * @return {@code true} si la fecha de creación no es futura, {@code false} si
	 *         lo es.
	 */
	@AssertTrue(message = "La fecha de creación no puede ser futura")
	public boolean isFechaCreacionNoFutura() {
		return fechaCreacion == null || !fechaCreacion.isAfter(LocalDate.now());
	}

	/**
	 * Saldo actual de la cuenta bancaria. No puede ser nulo y debe tener un valor
	 * válido.
	 */
	@Column(name = "Saldo", nullable = false)
	private float saldo;

	@Version
	private int version;

	/**
	 * Relación Many-to-Many entre cuentas bancarias y clientes. Una cuenta bancaria
	 * puede estar asociada a múltiples clientes. Se utiliza una tabla intermedia
	 * {@code cliente_cuenta} para gestionar esta relación.
	 * 
	 * @see Cliente
	 */
	@ManyToMany(fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
	@JoinTable(name = "cliente_cuenta", joinColumns = @JoinColumn(name = "cuenta_id"), inverseJoinColumns = @JoinColumn(name = "cliente_id"))
	private List<Cliente> misClientes;

	/**
	 * Relación One-to-Many entre cuentas bancarias y operaciones. Una cuenta
	 * bancaria puede tener múltiples operaciones asociadas.
	 * 
	 * @see Operacion
	 */
	@OneToMany(mappedBy = "cuentaBancaria", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
	private List<Operacion> operaciones;

	/**
	 * Enumeración que define los tipos de cuentas bancarias.
	 * 
	 * Los tipos de cuenta disponibles son:
	 * <ul>
	 * <li>{@code AHORRO}: Cuenta de ahorro.</li>
	 * <li>{@code CORRIENTE}: Cuenta corriente.</li>
	 * <li>{@code EMPRESARIAL}: Cuenta empresarial.</li>
	 * </ul>
	 */
	public enum TipoCuenta {
		AHORRO("Ahorro"), CORRIENTE("Corriente"), EMPRESARIAL("Empresarial");

		private final String nombre;

		TipoCuenta(String nombre) {
			this.nombre = nombre;
		}

		/**
		 * Obtiene el nombre del tipo de cuenta.
		 * 
		 * @return El nombre del tipo de cuenta.
		 */
		public String getNombre() {
			return nombre;
		}

	}
}
