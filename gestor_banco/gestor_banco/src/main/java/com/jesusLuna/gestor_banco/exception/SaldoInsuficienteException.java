package com.jesusLuna.gestor_banco.exception;

public class SaldoInsuficienteException extends RuntimeException {
	public SaldoInsuficienteException(String mensaje) {
		super(mensaje);
	}
}