package com.jesusLuna.gestor_banco.service;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import com.jesusLuna.gestor_banco.exception.JwtTokenException;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.SignatureException;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

/**
 * Servicio que proporciona funcionalidades para la generación, validación y
 * análisis de tokens JWT.
 * 
 * Utiliza la biblioteca `jjwt` para manejar los tokens.
 * 
 * Esta clase implementa la interfaz {@link JwtService}.
 * 
 * @author Jesús Luna Romero
 */
@Service
public class JwtServiceImpl implements JwtService {

	/**
	 * Clave secreta para la firma y verificación de tokens JWT. Se inyecta desde el
	 * archivo de configuración `application.properties`.
	 */
	@Value("${jwt.secret-key}")
	private String secretKey;

	/**
	 * Genera un token JWT a partir de los datos del usuario.
	 *
	 * @param user Detalles del usuario.
	 * @return Token JWT generado.
	 */
	@Override
	public String getToken(UserDetails user) {
		return getToken(new HashMap<>(), user);
	}

	/**
	 * Verifica si un token es válido para un usuario específico.
	 * 
	 * @param token       Token JWT a validar.
	 * @param userDetails Detalles del usuario autenticado.
	 * @return {@code true} si el token es válido, de lo contrario lanza excepción.
	 * @throws JwtTokenException Si el token está expirado o es inválido.
	 */
	@Override
	public boolean isTokenValid(String token, UserDetails userDetails) {
		try {
			final String username = getUsernameFromToken(token);
			return username.equals(userDetails.getUsername()) && !isTokenExpired(token);
		} catch (Exception e) {
			throw new JwtTokenException("Token inválido o expirado.");
		}
	}

	/**
	 * Verifica si un token JWT es válido (no ha expirado).
	 * 
	 * @param token Token JWT a verificar.
	 * @return {@code true} si el token es válido.
	 * @throws JwtTokenException Si el token está expirado o su firma es inválida.
	 */
	@Override
	public boolean isTokenValid(String token) {
		try {
			Claims claims = getClaimsFromToken(token);
			return !claims.getExpiration().before(new Date());
		} catch (SignatureException | ExpiredJwtException e) {
			throw new JwtTokenException("Token inválido o expirado.");
		}
	}

	/**
	 * Extrae el nombre de usuario (subject) contenido en el token JWT.
	 * 
	 * @param token Token JWT.
	 * @return Nombre de usuario extraído del token.
	 */
	@Override
	public String getUsernameFromToken(String token) {
		return getClaim(token, Claims::getSubject);
	}

	/**
	 * Verifica si el token ha expirado.
	 * 
	 * @param token Token JWT.
	 * @return {@code true} si el token ha expirado.
	 */
	private boolean isTokenExpired(String token) {
		return getExpiration(token).before(new Date());
	}

	/**
	 * Obtiene la fecha de expiración de un token.
	 * 
	 * @param token Token JWT.
	 * @return Fecha de expiración del token.
	 */
	private Date getExpiration(String token) {
		return getClaim(token, Claims::getExpiration);
	}

	/**
	 * Obtiene todos los claims (datos) almacenados en el token.
	 * 
	 * @param token Token JWT.
	 * @return Objeto {@link Claims} con los datos del token.
	 * @throws JwtTokenException Si el token es inválido o ha expirado.
	 */
	private Claims getAllClaims(String token) {
		try {
			return Jwts.parserBuilder().setSigningKey(getKey()).build().parseClaimsJws(token).getBody();
		} catch (ExpiredJwtException | SignatureException e) {
			throw new JwtTokenException("Token inválido o expirado.");
		}
	}

	/**
	 * Obtiene un claim específico del token usando una función resolutora.
	 * 
	 * @param <T>            Tipo de dato a devolver.
	 * @param token          Token JWT.
	 * @param claimsResolver Función que extrae el claim deseado.
	 * @return Claim extraído del token.
	 */
	public <T> T getClaim(String token, Function<Claims, T> claimsResolver) {
		final Claims claims = getAllClaims(token);
		return claimsResolver.apply(claims);
	}

	/**
	 * Genera un token JWT a partir de claims adicionales y los datos del usuario.
	 * 
	 * @param extraClaims Claims adicionales a incluir.
	 * @param user        Detalles del usuario.
	 * @return Token JWT generado.
	 */
	private String getToken(Map<String, Object> extraClaims, UserDetails user) {
		return Jwts.builder().setClaims(extraClaims).setSubject(user.getUsername())
				.setIssuedAt(new Date(System.currentTimeMillis()))
				.setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 24)) // 1 día
				.signWith(getKey(), SignatureAlgorithm.HS256).compact();
	}

	/**
	 * Extrae los claims de un token.
	 * 
	 * @param token Token JWT.
	 * @return Objeto {@link Claims} con los datos del token.
	 */
	private Claims getClaimsFromToken(String token) {
		return Jwts.parserBuilder().setSigningKey(getKey()).build().parseClaimsJws(token).getBody();
	}

	/**
	 * Genera la clave secreta utilizada para firmar y verificar los tokens JWT.
	 * 
	 * @return Clave secreta generada a partir de la cadena codificada en Base64.
	 */
	private Key getKey() {
		byte[] keyBytes = Decoders.BASE64.decode(secretKey);
		return Keys.hmacShaKeyFor(keyBytes);
	}
}
