package com.jesusLuna.gestor_banco.methods;

import java.math.BigInteger;
import java.util.Random;

/**
 * Clase que proporciona métodos para generar y validar números IBAN. Los
 * métodos incluyen la generación de un IBAN aleatorio basado en el código de
 * país, el código bancario y el número de cuenta, así como la validación de un
 * IBAN siguiendo las especificaciones de la normativa ISO 13616-1.
 */
public class Methods {

	/**
	 * Genera un número IBAN aleatorio basado en el código de país, el código
	 * bancario y el número de cuenta.
	 *
	 * @param countryCode         Código de país (2 letras) para el IBAN.
	 * @param bankCodeLength      Longitud del código bancario.
	 * @param accountNumberLength Longitud del número de cuenta.
	 * @return El IBAN generado como una cadena de texto.
	 */
	public static String generateIBAN(String countryCode, int bankCodeLength, int accountNumberLength) {
		Random random = new Random();

		// Generar código del banco
		StringBuilder bankCode = new StringBuilder();
		for (int i = 0; i < bankCodeLength; i++) {
			bankCode.append(random.nextInt(10)); // Números aleatorios 0-9
		}

		// Generar número de cuenta
		StringBuilder accountNumber = new StringBuilder();
		for (int i = 0; i < accountNumberLength; i++) {
			accountNumber.append(random.nextInt(10));
		}

		// Concatenar sin el código IBAN inicial
		String partialIBAN = bankCode.toString() + accountNumber.toString() + countryCode + "00";

		// Convertir letras a números según ISO 13616-1
		String numericIBAN = partialIBAN.chars()
				.mapToObj(c -> Character.isLetter(c) ? String.valueOf(c - 'A' + 10) : String.valueOf((char) c))
				.reduce("", String::concat);

		// Calcular el dígito de control
		BigInteger ibanNumber = new BigInteger(numericIBAN);
		int checkDigits = 98 - ibanNumber.mod(BigInteger.valueOf(97)).intValue();

		// Formatear el IBAN completo
		String iban = countryCode + String.format("%02d", checkDigits) + bankCode + accountNumber;
		return iban;
	}

	/**
	 * Valida un número IBAN siguiendo la normativa ISO 13616-1.
	 *
	 * @param iban El número IBAN a validar.
	 * @return True si el IBAN es válido, false si no lo es.
	 */
	public static boolean validarIBAN(String iban) {
		iban = iban.replaceAll("\\s+", "").toUpperCase();
		if (iban.length() < 15 || iban.length() > 34)
			return false;

		// Reorganizar el IBAN para el cálculo del dígito de control
		String rearranged = iban.substring(4) + iban.substring(0, 4);

		// Convertir caracteres a números
		StringBuilder numericIBAN = new StringBuilder();
		for (char ch : rearranged.toCharArray()) {
			if (Character.isLetter(ch)) {
				numericIBAN.append((int) ch - 55); // Convertir letras en números según ISO 13616-1
			} else {
				numericIBAN.append(ch);
			}
		}

		// Convertir el IBAN a BigInteger y calcular el módulo 97
		BigInteger ibanNumber = new BigInteger(numericIBAN.toString());
		return ibanNumber.mod(BigInteger.valueOf(97)).intValue() == 1;
	}
}
