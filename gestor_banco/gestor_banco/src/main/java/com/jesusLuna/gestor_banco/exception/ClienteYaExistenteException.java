package com.jesusLuna.gestor_banco.exception;

public class ClienteYaExistenteException extends RuntimeException {
	public ClienteYaExistenteException(String mensaje) {
		super(mensaje);
	}
}