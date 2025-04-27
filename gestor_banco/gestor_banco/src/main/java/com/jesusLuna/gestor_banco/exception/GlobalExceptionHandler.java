package com.jesusLuna.gestor_banco.exception;

import java.nio.file.AccessDeniedException;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.NoHandlerFoundException;
import jakarta.persistence.OptimisticLockException;
import jakarta.servlet.http.HttpServletRequest;

/**
 * Clase encargada de manejar globalmente las excepciones que puedan ocurrir
 * durante la ejecución de la aplicación. Utiliza los métodos de la anotación
 * {@link ControllerAdvice} para capturar y manejar excepciones específicas,
 * proporcionando respuestas apropiadas o vistas de error.
 */
@ControllerAdvice
public class GlobalExceptionHandler {

	/**
	 * Maneja la excepción {@link OptimisticLockException} y muestra una página de
	 * error con un mensaje de conflicto de edición.
	 * 
	 * @param model El modelo utilizado para pasar los atributos a la vista de
	 *              error.
	 * @return El nombre de la vista de error.
	 */
	@ExceptionHandler(OptimisticLockException.class)
	public String manejarConflictoOptimista(Model model) {
		model.addAttribute("mensaje", "Conflicto de edición: otra persona ha modificado este registro.");
		model.addAttribute("detalle", "Actualiza la página antes de volver a intentar.");
		model.addAttribute("ruta", "/clientes");
		return "errorPage";
	}

	/**
	 * Maneja la excepción {@link OperacionNoEncontradaException} y devuelve un
	 * mensaje con el estado 404 (NOT FOUND).
	 *
	 * @param ex La excepción capturada.
	 * @return La respuesta con el mensaje de la excepción y el estado HTTP 404.
	 */
	@ExceptionHandler(OperacionNoEncontradaException.class)
	@ResponseBody
	public ResponseEntity<String> handleOperacionNotFound(OperacionNoEncontradaException ex) {
		return new ResponseEntity<>(ex.getMessage(), HttpStatus.NOT_FOUND);
	}

	/**
	 * Maneja la excepción {@link JwtTokenException} y devuelve un mensaje con el
	 * estado 401 (UNAUTHORIZED).
	 *
	 * @param ex La excepción capturada.
	 * @return La respuesta con el mensaje de la excepción y el estado HTTP 401.
	 */
	@ExceptionHandler(JwtTokenException.class)
	public ResponseEntity<String> handleJwtTokenException(JwtTokenException ex) {
		return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ex.getMessage());
	}

	/**
	 * Maneja la excepción {@link ClienteYaExistenteException} y devuelve un mensaje
	 * con el estado 400 (BAD REQUEST).
	 *
	 * @param ex La excepción capturada.
	 * @return La respuesta con el mensaje de la excepción y el estado HTTP 400.
	 */
	@ExceptionHandler(ClienteYaExistenteException.class)
	public ResponseEntity<String> handleClienteYaExistente(ClienteYaExistenteException ex) {
		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
	}

	/**
	 * Maneja la excepción {@link ClienteNoEncontradoException} y devuelve un
	 * mensaje con el estado 404 (NOT FOUND).
	 *
	 * @param ex La excepción capturada.
	 * @return La respuesta con el mensaje de la excepción y el estado HTTP 404.
	 */
	@ExceptionHandler(ClienteNoEncontradoException.class)
	public ResponseEntity<String> handleClienteNoEncontrado(ClienteNoEncontradoException ex) {
		return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
	}

	/**
	 * Maneja la excepción {@link ParametroBusquedaException} y devuelve un mensaje
	 * con el estado 400 (BAD REQUEST).
	 *
	 * @param ex La excepción capturada.
	 * @return La respuesta con el mensaje de la excepción y el estado HTTP 400.
	 */
	@ExceptionHandler(ParametroBusquedaException.class)
	public ResponseEntity<String> handleParametroBusqueda(ParametroBusquedaException ex) {
		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
	}

	/**
	 * Maneja la excepción {@link CredencialesInvalidasException} y devuelve un
	 * mensaje con el estado 401 (UNAUTHORIZED).
	 *
	 * @param ex La excepción capturada.
	 * @return La respuesta con el mensaje de la excepción y el estado HTTP 401.
	 */
	@ExceptionHandler(CredencialesInvalidasException.class)
	public ResponseEntity<String> handleCredencialesInvalidas(CredencialesInvalidasException ex) {
		return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ex.getMessage());
	}

	/**
	 * Maneja la excepción {@link UsuarioYaExisteException} y devuelve un mensaje
	 * con el estado 400 (BAD REQUEST).
	 *
	 * @param ex La excepción capturada.
	 * @return La respuesta con el mensaje de la excepción y el estado HTTP 400.
	 */
	@ExceptionHandler(UsuarioYaExisteException.class)
	public ResponseEntity<String> handleUsuarioYaExiste(UsuarioYaExisteException ex) {
		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
	}

	/**
	 * Maneja la excepción {@link CuentaNoEncontradaException} y muestra una página
	 * de error con el mensaje correspondiente.
	 *
	 * @param ex    La excepción capturada.
	 * @param model El modelo utilizado para pasar los atributos a la vista de
	 *              error.
	 * @return El nombre de la vista de error.
	 */
	@ExceptionHandler(CuentaNoEncontradaException.class)
	public String handleCuentaNoEncontrada(CuentaNoEncontradaException ex, Model model) {
		model.addAttribute("mensaje", "Cuenta no encontrada");
		model.addAttribute("detalle", ex.getMessage());
		return "errorPage"; // Vista generalizada de error
	}

	/**
	 * Maneja la excepción {@link SaldoInsuficienteException} y muestra una página
	 * de error con el mensaje correspondiente.
	 *
	 * @param ex    La excepción capturada.
	 * @param model El modelo utilizado para pasar los atributos a la vista de
	 *              error.
	 * @return El nombre de la vista de error.
	 */
	@ExceptionHandler(SaldoInsuficienteException.class)
	public String handleSaldoInsuficiente(SaldoInsuficienteException ex, Model model) {
		model.addAttribute("mensaje", "Saldo de cuenta bancaria insuficiente");
		model.addAttribute("detalle", ex.getMessage());
		return "errorPage"; // Vista generalizada de error
	}

	/**
	 * Maneja la excepción {@link AccessDeniedException} y muestra una página de
	 * error con el mensaje correspondiente, con el estado 403 (FORBIDDEN).
	 *
	 * @param ex      La excepción capturada.
	 * @param model   El modelo utilizado para pasar los atributos a la vista de
	 *                error.
	 * @param request La solicitud HTTP para obtener la URI de la página.
	 * @return El nombre de la vista de error.
	 */
	@ExceptionHandler(AccessDeniedException.class)
	public String handleAccessDenied(AccessDeniedException ex, Model model, HttpServletRequest request) {
		model.addAttribute("mensaje", "No tienes permiso para acceder a esta página.");
		model.addAttribute("detalle", ex.getMessage());
		model.addAttribute("ruta", request.getRequestURI());
		return "errorPage"; // Vista generalizada de error
	}

	/**
	 * Maneja la excepción {@link AuthenticationCredentialsNotFoundException} y
	 * muestra una página de error con el mensaje correspondiente, con el estado 401
	 * (UNAUTHORIZED).
	 *
	 * @param ex      La excepción capturada.
	 * @param model   El modelo utilizado para pasar los atributos a la vista de
	 *                error.
	 * @param request La solicitud HTTP para obtener la URI de la página.
	 * @return El nombre de la vista de error.
	 */
	@ExceptionHandler(AuthenticationCredentialsNotFoundException.class)
	public String handleUnauthorized(AuthenticationCredentialsNotFoundException ex, Model model,
			HttpServletRequest request) {
		model.addAttribute("mensaje", "Debes iniciar sesión para acceder.");
		model.addAttribute("detalle", ex.getMessage());
		model.addAttribute("ruta", request.getRequestURI());
		return "errorPage"; // Vista generalizada de error
	}

	/**
	 * Maneja la excepción {@link NoHandlerFoundException} y muestra una página de
	 * error con el mensaje correspondiente, con el estado 404 (NOT FOUND).
	 *
	 * @param ex    La excepción capturada.
	 * @param model El modelo utilizado para pasar los atributos a la vista de
	 *              error.
	 * @return El nombre de la vista de error.
	 */
	@ExceptionHandler(NoHandlerFoundException.class)
	public String handleNotFound(NoHandlerFoundException ex, Model model) {
		model.addAttribute("mensaje", "La página solicitada no existe");
		model.addAttribute("detalle", ex.getMessage());
		return "errorPage"; // Vista generalizada de error
	}

	/**
	 * Maneja la excepción {@link Exception} y muestra una página de error con el
	 * mensaje correspondiente, con el estado 500 (INTERNAL SERVER ERROR).
	 *
	 * @param ex    La excepción capturada.
	 * @param model El modelo utilizado para pasar los atributos a la vista de
	 *              error.
	 * @return El nombre de la vista de error.
	 */
	@ExceptionHandler(Exception.class)
	public String handleGeneralException(Exception ex, Model model) {
		model.addAttribute("mensaje", "Ha ocurrido un error inesperado");
		model.addAttribute("detalle", ex.getMessage());
		return "errorPage"; // Vista generalizada de error
	}

	/**
	 * Maneja la excepción {@link IllegalArgumentException} y muestra una página de
	 * error con el mensaje correspondiente, con el estado 400 (BAD REQUEST).
	 *
	 * @param ex      La excepción capturada.
	 * @param model   El modelo utilizado para pasar los atributos a la vista de
	 *                error.
	 * @param request La solicitud HTTP para obtener la URI de la página.
	 * @return El nombre de la vista de error.
	 */
	@ExceptionHandler(IllegalArgumentException.class)
	public String handleIllegalArgument(IllegalArgumentException ex, Model model, HttpServletRequest request) {
		model.addAttribute("mensaje", "El argumento proporcionado no es válido.");
		model.addAttribute("detalle", ex.getMessage());
		model.addAttribute("ruta", request.getRequestURI());
		return "errorPage"; // Vista generalizada de error
	}

	/**
	 * Maneja la excepción {@link DataAccessException} y muestra una página de error
	 * con el mensaje correspondiente, con el estado 500 (INTERNAL SERVER ERROR).
	 *
	 * @param ex      La excepción capturada.
	 * @param model   El modelo utilizado para pasar los atributos a la vista de
	 *                error.
	 * @param request La solicitud HTTP para obtener la URI de la página.
	 * @return El nombre de la vista de error.
	 */
	@ExceptionHandler(DataAccessException.class)
	public String handleDataAccessError(DataAccessException ex, Model model, HttpServletRequest request) {
		model.addAttribute("mensaje", "Error al acceder a la base de datos.");
		model.addAttribute("detalle", ex.getMessage());
		model.addAttribute("ruta", request.getRequestURI());
		return "errorPage"; // Vista generalizada de error
	}

	/**
	 * Maneja la excepción {@link NullPointerException} y muestra una página de
	 * error con el mensaje correspondiente, con el estado 500 (INTERNAL SERVER
	 * ERROR).
	 *
	 * @param ex      La excepción capturada.
	 * @param model   El modelo utilizado para pasar los atributos a la vista de
	 *                error.
	 * @param request La solicitud HTTP para obtener la URI de la página.
	 * @return El nombre de la vista de error.
	 */
	@ExceptionHandler(NullPointerException.class)
	public String handleNullPointer(NullPointerException ex, Model model, HttpServletRequest request) {
		model.addAttribute("mensaje", "Ocurrió un error interno, el valor no puede ser nulo.");
		model.addAttribute("detalle", ex.getMessage());
		model.addAttribute("ruta", request.getRequestURI());
		return "errorPage"; // Vista generalizada de error
	}

	/**
	 * Maneja la excepción {@link UsernameNotFoundException} y devuelve una
	 * respuesta con el estado 401 (UNAUTHORIZED).
	 *
	 * @param ex La excepción capturada.
	 * @return La respuesta con el mensaje de la excepción y el estado HTTP 401.
	 */
	@ExceptionHandler(UsernameNotFoundException.class)
	public ResponseEntity<String> handleUsernameNotFound(UsernameNotFoundException ex) {
		return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ex.getMessage());
	}
}
