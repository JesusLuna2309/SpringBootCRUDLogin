package com.jesusLuna.gestor_banco.methods;

import java.util.Base64;
import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

/**
 * Esta clase proporciona métodos para cifrar y descifrar texto utilizando el
 * algoritmo AES. El texto se cifra y descifra utilizando una clave secreta
 * definida en la constante {@link #KEY}. El cifrado utiliza el modo AES en
 * combinación con Base64 para garantizar que los resultados sean adecuados para
 * su uso en URLs.
 */
public class Cifrado {

	// Algoritmo de cifrado utilizado (AES)
	private static final String ALGORITHM = "AES";

	// Clave secreta de 16 caracteres (debe mantenerse segura)
	private static final String KEY = "MySuperSecretKey"; // Debe tener 16 caracteres

	/**
	 * Cifra un texto usando el algoritmo AES.
	 * 
	 * @param texto El texto en formato {@link String} a cifrar.
	 * @return El texto cifrado en formato Base64, adecuado para su uso en URLs.
	 * @throws Exception Si ocurre un error durante el proceso de cifrado.
	 */
	public static String cifrar(String texto) throws Exception {
		// Crear la clave secreta a partir de la clave definida
		SecretKeySpec keySpec = new SecretKeySpec(KEY.getBytes(), ALGORITHM);

		// Crear el objeto Cipher para el algoritmo AES
		Cipher cipher = Cipher.getInstance(ALGORITHM);

		// Inicializar el Cipher en modo de cifrado (ENCRYPT_MODE)
		cipher.init(Cipher.ENCRYPT_MODE, keySpec);

		// Cifrar el texto y obtener los bytes cifrados
		byte[] encrypted = cipher.doFinal(texto.getBytes("UTF-8"));

		// Codificar los bytes cifrados en Base64 para su uso en URLs y devolverlo como
		// String
		return Base64.getUrlEncoder().encodeToString(encrypted);
	}

	/**
	 * Descifra un texto previamente cifrado utilizando el algoritmo AES.
	 * 
	 * @param textoCifrado El texto cifrado en formato Base64 a descifrar.
	 * @return El texto original en formato {@link String}.
	 * @throws Exception Si ocurre un error durante el proceso de descifrado.
	 */
	public static String descifrar(String textoCifrado) throws Exception {
		// Crear la clave secreta a partir de la clave definida
		SecretKeySpec keySpec = new SecretKeySpec(KEY.getBytes(), ALGORITHM);

		// Crear el objeto Cipher para el algoritmo AES
		Cipher cipher = Cipher.getInstance(ALGORITHM);

		// Inicializar el Cipher en modo de descifrado (DECRYPT_MODE)
		cipher.init(Cipher.DECRYPT_MODE, keySpec);

		// Decodificar el texto cifrado desde Base64
		byte[] decoded = Base64.getUrlDecoder().decode(textoCifrado);

		// Descifrar los datos y obtener el texto original
		byte[] decrypted = cipher.doFinal(decoded);

		// Convertir los bytes descifrados en texto y devolverlo
		return new String(decrypted, "UTF-8");
	}

}
