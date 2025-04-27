package com.jesusLuna.gestor_banco.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.NOT_FOUND, reason = "Operación no encontrada")
public class OperacionNoEncontradaException extends RuntimeException {
	public OperacionNoEncontradaException(String mensaje) {
		super(mensaje);
	}
}