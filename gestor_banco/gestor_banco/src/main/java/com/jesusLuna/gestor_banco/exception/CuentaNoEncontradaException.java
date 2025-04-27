package com.jesusLuna.gestor_banco.exception;

public class CuentaNoEncontradaException extends RuntimeException {
	public CuentaNoEncontradaException(String mensaje) {
		super(mensaje);
	}
}