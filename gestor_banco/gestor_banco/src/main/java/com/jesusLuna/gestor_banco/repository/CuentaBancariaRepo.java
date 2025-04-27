package com.jesusLuna.gestor_banco.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.jesusLuna.gestor_banco.entity.CuentaBancaria;


/**
 * Repositorio de acceso a datos para la entidad {@link CuentaBancaria}.
 * Extiende de {@link JpaRepository} proporcionando operaciones CRUD básicas.
 * También contiene métodos personalizados para la búsqueda de cuentas
 * bancarias.
 * 
 * @author Jesús
 */
public interface CuentaBancariaRepo extends JpaRepository<CuentaBancaria, String> {

	/**
	 * Busca una lista de cuentas bancarias que coincidan con los criterios de
	 * número de cuenta y tipo de cuenta. Si uno de los parámetros es null, ese
	 * criterio no se aplica.
	 * 
	 * @param numeroCuenta el número de cuenta a buscar (puede ser una coincidencia
	 *                     parcial). Puede ser {@code null}.
	 * @param tipoCuenta   el tipo de cuenta a buscar. Puede ser {@code null}.
	 * @return una lista de cuentas bancarias que cumplen con los criterios
	 *         especificados.
	 */
	@Query("SELECT c FROM CuentaBancaria c WHERE "
			+ "(:numeroCuenta IS NULL OR c.numeroCuenta LIKE %:numeroCuenta%) AND "
			+ "(:tipoCuenta IS NULL OR c.tipoCuenta = :tipoCuenta)")
	List<CuentaBancaria> buscarCuentas(@Param("numeroCuenta") String numeroCuenta,
			@Param("tipoCuenta") CuentaBancaria.TipoCuenta tipoCuenta);

	/**
	 * Busca una cuenta bancaria por su número de cuenta exacto.
	 * 
	 * @param numeroCuenta el número de cuenta.
	 * @return la cuenta bancaria correspondiente, o {@code null} si no se
	 *         encuentra.
	 */
	CuentaBancaria findByNumeroCuenta(String numeroCuenta);
}
