package com.jesusLuna.gestor_banco.service;

import java.util.List;

import com.jesusLuna.gestor_banco.entity.CuentaBancaria;


public interface CuentaBancariaService {

	public List<CuentaBancaria> obtenerCuentasBancarias();

	public CuentaBancaria obtenerPorNumeroCuenta(String numeroCuenta);

	public List<CuentaBancaria> buscarCuentas(String numeroCuenta, String tipoCuentaStr, String ordenarPor,
			String ordenTipo);

	public void insertarCuentaBancaria(CuentaBancaria cb);

	public void actualizarCuentaBancaria(CuentaBancaria cb);

	public void eliminarCuentaBancariaPorNumCuenta(String numCuenta);

}
