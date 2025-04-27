package com.jesusLuna.gestor_banco.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.jesusLuna.gestor_banco.entity.Cliente;



/**
 * Controlador principal de vistas de la aplicación. Gestiona el enrutamiento
 * inicial y redirecciones a otras vistas o controladores.
 */
@Controller
@RequestMapping
public class SystemController {

	/**
	 * Redirige a la vista de login al acceder a la raíz del sitio.
	 *
	 * @return Redirección a la ruta "/showLogin".
	 */
	@GetMapping("/")
	public String redirectToLogin() {
		return "redirect:/showLogin";
	}

	/**
	 * Muestra la plantilla de login.
	 *
	 * @return Nombre de la vista del login ("loginView").
	 */
	@GetMapping("/showLogin")
	public String showLogin() {
		return "loginView";
	}

	/**
	 * Muestra la página principal de la aplicación.
	 *
	 * @return Nombre de la vista principal ("index").
	 */
	@GetMapping("/index")
	public String showIndex() {
		return "index";
	}

	/*
	 * CLIENTES
	 */

	/**
	 * Redirige al controlador de clientes para mostrar la lista de clientes.
	 *
	 * @return Redirección a la ruta "/showClientesView".
	 */
	@GetMapping("/clientesView")
	public String redirectToClienteController() {
		return "redirect:showClientesView";
	}

	/**
	 * Muestra la vista para insertar un nuevo cliente.
	 *
	 * @param model Modelo de Spring para pasar datos a la vista.
	 * @return Nombre de la vista de inserción de cliente ("clienteInsertar").
	 */
	@GetMapping("/newClientesView")
	public String redirectToNewClienteTemplate(Model model) {

		model.addAttribute("newCliente", new Cliente()); // Agregar un objeto vacío

		return "clienteInsertar";
	}

	/*
	 * CUENTAS
	 */

	/**
	 * Redirige al controlador de cuentas para mostrar la lista de cuentas
	 * bancarias.
	 *
	 * @return Redirección a la ruta "/showCuentasView".
	 */
	// Listado
	@GetMapping("/cuentasView")
	public String redirectToCuentaController() {
		return "redirect:showCuentasView";
	}

}
