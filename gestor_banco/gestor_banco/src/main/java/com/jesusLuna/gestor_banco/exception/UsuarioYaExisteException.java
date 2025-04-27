package com.jesusLuna.gestor_banco.exception;

public class UsuarioYaExisteException extends RuntimeException {
	public UsuarioYaExisteException(String mensaje) {
		super(mensaje);
	}
}