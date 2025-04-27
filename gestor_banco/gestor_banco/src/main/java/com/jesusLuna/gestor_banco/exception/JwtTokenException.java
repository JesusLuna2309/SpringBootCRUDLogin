package com.jesusLuna.gestor_banco.exception;

public class JwtTokenException extends RuntimeException {
	public JwtTokenException(String message) {
		super(message);
	}
}