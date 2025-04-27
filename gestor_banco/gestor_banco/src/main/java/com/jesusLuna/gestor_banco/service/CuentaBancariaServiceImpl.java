package com.jesusLuna.gestor_banco.service;

import java.util.Comparator;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.jesusLuna.gestor_banco.entity.CuentaBancaria;
import com.jesusLuna.gestor_banco.exception.CuentaNoEncontradaException;
import com.jesusLuna.gestor_banco.repository.CuentaBancariaRepo;


/**
 * Implementación del servicio para gestionar operaciones relacionadas con
 * cuentas bancarias. Proporciona métodos para obtener, insertar, actualizar y
 * eliminar cuentas.
 * 
 * @author Jesús
 */
@Service
public class CuentaBancariaServiceImpl implements CuentaBancariaService {

	@Autowired
	private CuentaBancariaRepo cuentaBancRepo;

	/**
	 * Obtiene todas las cuentas bancarias almacenadas en la base de datos.
	 * 
	 * @return una lista de todas las cuentas bancarias.
	 */
	@Override
	public List<CuentaBancaria> obtenerCuentasBancarias() {
		return cuentaBancRepo.findAll();
	}

	/**
	 * Busca una cuenta bancaria por su número de cuenta.
	 * 
	 * @param numeroCuenta el número de cuenta a buscar.
	 * @return la cuenta bancaria encontrada.
	 * @throws CuentaNoEncontradaException si no se encuentra la cuenta.
	 */
	@Override
	public CuentaBancaria obtenerPorNumeroCuenta(String numeroCuenta) {
		return cuentaBancRepo.findById(numeroCuenta)
				.orElseThrow(() -> new CuentaNoEncontradaException("Cuenta bancaria no encontrada"));
	}

	/**
	 * Busca cuentas bancarias filtrando por número de cuenta y tipo de cuenta, y
	 * permite ordenar por fecha de creación.
	 * 
	 * @param numeroCuenta  número de cuenta parcial o completo a buscar (puede ser
	 *                      null).
	 * @param tipoCuentaStr tipo de cuenta en formato String (puede ser null).
	 * @param ordenarPor    campo por el cual se desea ordenar (ej.
	 *                      "fechaCreacion").
	 * @param ordenTipo     tipo de orden ("ASC" para ascendente o "DESC" para
	 *                      descendente).
	 * @return una lista de cuentas que cumplen con los criterios de búsqueda.
	 */
	@Override
	public List<CuentaBancaria> buscarCuentas(String numeroCuenta, String tipoCuentaStr, String ordenarPor,
			String ordenTipo) {
		CuentaBancaria.TipoCuenta tipoCuenta = null;
		if (tipoCuentaStr != null && !tipoCuentaStr.isEmpty()) {
			tipoCuenta = CuentaBancaria.TipoCuenta.valueOf(tipoCuentaStr);
		}

		List<CuentaBancaria> cuentas = cuentaBancRepo.buscarCuentas(numeroCuenta, tipoCuenta);

		if ("fechaCreacion".equals(ordenarPor)) {
			if ("ASC".equalsIgnoreCase(ordenTipo)) {
				cuentas.sort(Comparator.comparing(CuentaBancaria::getFechaCreacion));
			} else {
				cuentas.sort(Comparator.comparing(CuentaBancaria::getFechaCreacion).reversed());
			}
		}

		return cuentas;
	}

	/**
	 * Inserta una nueva cuenta bancaria en la base de datos.
	 * 
	 * @param cb la cuenta bancaria a insertar.
	 */
	@Override
	public void insertarCuentaBancaria(CuentaBancaria cb) {
		cuentaBancRepo.save(cb);
	}

	/**
	 * Actualiza una cuenta bancaria existente.
	 * 
	 * @param cb la cuenta bancaria con los nuevos datos.
	 * @throws CuentaNoEncontradaException si no existe una cuenta con el número
	 *                                     indicado.
	 */
	@Override
	public void actualizarCuentaBancaria(CuentaBancaria cb) {
		if (!cuentaBancRepo.existsById(cb.getNumeroCuenta())) {
			throw new CuentaNoEncontradaException("Cuenta bancaria no encontrada");
		}
		cuentaBancRepo.save(cb);
	}

	/**
	 * Elimina una cuenta bancaria dado su número de cuenta.
	 * 
	 * @param numCuenta el número de cuenta a eliminar.
	 * @throws CuentaNoEncontradaException si no se encuentra la cuenta.
	 */
	@Override
	public void eliminarCuentaBancariaPorNumCuenta(String numCuenta) {
		if (!cuentaBancRepo.existsById(numCuenta)) {
			throw new CuentaNoEncontradaException("Cuenta bancaria no encontrada");
		}
		cuentaBancRepo.deleteById(numCuenta);
	}
}
