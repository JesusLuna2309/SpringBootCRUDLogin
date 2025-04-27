package com.jesusLuna.gestor_banco.exception;

public class CredencialesInvalidasException extends RuntimeException {
	public CredencialesInvalidasException(String mensaje) {
		super(mensaje);
	}
}
