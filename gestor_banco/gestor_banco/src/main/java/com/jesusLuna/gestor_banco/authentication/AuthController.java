package com.jesusLuna.gestor_banco.authentication;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.jesusLuna.gestor_banco.entity.User.Role;
import com.jesusLuna.gestor_banco.exception.AuthenticationException;
import com.jesusLuna.gestor_banco.service.AuthService;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;

/**
 * Controlador que gestiona la autenticación y registro de usuarios. Incluye
 * operaciones de login, logout y registro seguro.
 */
@Controller
@RequestMapping
@RequiredArgsConstructor
public class AuthController {

	@Autowired
	private final AuthService authService;

	/**
	 * Procesa el formulario de inicio de sesión. Maneja intentos fallidos con
	 * control de sesión y crea una cookie JWT si es exitoso.
	 *
	 * @param username     Nombre de usuario introducido.
	 * @param password     Contraseña introducida.
	 * @param model        Modelo para pasar datos a la vista.
	 * @param httpResponse Respuesta HTTP para añadir cookies.
	 * @param request      Petición HTTP para gestionar sesión.
	 * @return Redirección a la vista principal o login con error.
	 */
	@PostMapping("actlogin")
	public String login(@RequestParam String username, @RequestParam String password, Model model,
			HttpServletResponse httpResponse, HttpServletRequest request) {

		// Obtener el contador de intentos fallidos de la sesión
		Integer failedAttempts = (Integer) request.getSession().getAttribute("failedAttempts");

		if (failedAttempts != null && failedAttempts >= 5) {
			return "redirect:/showLogin?error=too_many_attempts"; // Bloqueo por demasiados intentos
		}

		try {

			LoginRequest loginRequest = new LoginRequest(username, password);
			AuthResponse response = authService.login(loginRequest);

			// Si el login es exitoso, restablecer el contador de intentos fallidos
			request.getSession().setAttribute("failedAttempts", 0);

			Cookie cookie = new Cookie("jwt", response.getToken());
			cookie.setHttpOnly(true); // Protección contra JS malicioso
			cookie.setSecure(true);
			cookie.setPath("/"); // Disponible en toda la app
			httpResponse.addCookie(cookie);

			return "redirect:index"; // Redirige tras login exitoso
		} catch (AuthenticationException e) {
			// Incrementar el contador de intentos fallidos
			failedAttempts = (failedAttempts == null) ? 1 : failedAttempts + 1;
			request.getSession().setAttribute("failedAttempts", failedAttempts);

			// Redirige al login con un mensaje de error genérico
			return "redirect:/showLogin?error=true";
		} catch (Exception e) {
			// Para otros errores generales
			return "redirect:/showLogin?error=true";
		}
	}

	/**
	 * Registra un nuevo usuario si se proporciona una clave secreta válida. También
	 * valida la contraseña antes de procesar el registro.
	 *
	 * @param secret    Clave secreta para acceder al registro.
	 * @param username  Nombre de usuario.
	 * @param password  Contraseña del usuario.
	 * @param nombre    Nombre real del usuario.
	 * @param apellidos Apellidos del usuario.
	 * @param email     Correo electrónico del usuario.
	 * @param role      Rol del usuario (ej. USER, ADMIN).
	 * @param response  Respuesta HTTP para añadir cookie JWT.
	 * @return Respuesta HTTP indicando éxito o error del registro.
	 */
	@PostMapping("/register-secret")
	public ResponseEntity<String> register(@RequestParam String secret, @RequestParam String username,
			@RequestParam String password, @RequestParam String nombre, @RequestParam String apellidos,
			@RequestParam String email, @RequestParam String role, HttpServletResponse response) {
		if (!"claveRegister9090".equals(secret)) {
			return ResponseEntity.status(403).body("No autorizado");
		}

		if (!isValidPassword(password)) {
			return ResponseEntity.badRequest().body(
					"La contraseña debe tener al menos 8 caracteres, una mayúscula, un número y un carácter especial.");
		}

		try {
			Role userRole = Role.valueOf(role.toUpperCase()); // Convierte el string a enum

			RegisterRequest request = new RegisterRequest(username, password, nombre, apellidos, email, userRole);
			AuthResponse authResponse = authService.register(request);

			Cookie cookie = new Cookie("jwt", authResponse.getToken());
			cookie.setHttpOnly(true);
			cookie.setPath("/");
			response.addCookie(cookie);

			return ResponseEntity.ok("Registro correcto");
		} catch (IllegalArgumentException e) {
			return ResponseEntity.badRequest().body("Error: El rol proporcionado no es válido.");
		} catch (Exception e) {
			// Manejo de otros errores inesperados
			return ResponseEntity.status(500).body("Error interno del servidor: " + e.getMessage());
		}

	}

	/**
	 * Cierra la sesión del usuario actual e invalida su cookie JWT.
	 *
	 * @param request  Petición HTTP para invalidar sesión.
	 * @param response Respuesta HTTP para eliminar cookie.
	 * @return Redirección a la vista de login.
	 */
	@GetMapping("/logout")
	public String logout(HttpServletRequest request, HttpServletResponse response) {
		try {
			// Invalida la sesión, si existe
			HttpSession session = request.getSession(false);
			if (session != null) {
				session.invalidate();
			}

			// Elimina la cookie
			Cookie cookie = new Cookie("jwt", "");
			cookie.setMaxAge(0);
			cookie.setHttpOnly(true);
			cookie.setSecure(true);
			cookie.setPath("/");

			response.addCookie(cookie);

			return "redirect:/showLogin?logout=true";
		} catch (Exception e) {
			throw e;
		}
	}

	/**
	 * Valida la complejidad de una contraseña.
	 *
	 * @param password Contraseña a validar.
	 * @return true si cumple con los requisitos; false en caso contrario.
	 */
	private boolean isValidPassword(String password) {
		return password.length() >= 8 && password.matches(".*[A-Z].*") && // Al menos una mayúscula
				password.matches(".*[0-9].*") && // Al menos un número
				password.matches(".*[^a-zA-Z0-9].*"); // Al menos un carácter especial
	}

	/**
	 * Método auxiliar para cargar una página de error con detalles personalizados.
	 *
	 * @param model   Modelo donde se cargan los atributos de error.
	 * @param mensaje Mensaje general del error.
	 * @param detalle Detalle más técnico o informativo del error.
	 * @param ruta    Ruta o contexto en el que ocurrió el error.
	 * @return Nombre de la vista de error.
	 */
	private String manejarError(Model model, String mensaje, String detalle, String ruta) {
		model.addAttribute("mensaje", mensaje);
		model.addAttribute("detalle", detalle);
		model.addAttribute("ruta", ruta);
		return "errorPage";
	}
}
