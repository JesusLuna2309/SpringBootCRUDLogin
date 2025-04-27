package com.jesusLuna.gestor_banco.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.jesusLuna.gestor_banco.entity.Cliente;
import com.jesusLuna.gestor_banco.exception.ParametroBusquedaException;
import com.jesusLuna.gestor_banco.methods.Cifrado;
import com.jesusLuna.gestor_banco.service.ClienteService;
import com.jesusLuna.gestor_banco.service.CuentaBancariaService;

import jakarta.validation.Valid;

/**
 * Controlador encargado de gestionar las operaciones relacionadas con clientes.
 * Incluye vistas, acciones CRUD y gestión de relaciones con cuentas bancarias.
 */
@Controller
public class ClienteController {

	/**
	 * Servicio de gestión de clientes.
	 */
	@Autowired
	private ClienteService clienteServiceI;

	/**
	 * El servicio de CuentaBancaria.
	 */
	@Autowired
	private CuentaBancariaService cuentaBancariaServiceI;

	/*
	 * VISTAS
	 */

	/**
	 * Muestra la vista con todos los clientes disponibles en el sistema.
	 * 
	 * @param model Modelo de la vista.
	 * @return Nombre de la vista Thymeleaf.
	 */
	@GetMapping("/showClientesView")
	public String mostrarClientes(Model model) {
		try {
			List<Cliente> listaClientes = clienteServiceI.obtenerClientes();
			// Creamos una lista auxiliar con los NIFs cifrados
			Map<String, String> nifCifrados = new HashMap<>();

			for (Cliente c : listaClientes) {
				nifCifrados.put(c.getNif(), Cifrado.cifrar(c.getNif()));
			}

			model.addAttribute("clienteListView", listaClientes);
			model.addAttribute("nifCifrados", nifCifrados);

			return "clienteView";
		} catch (DataAccessException ex) {
			return manejarError(model, "Error al acceder a los datos de clientes.", ex.getMessage(),
					"/showClientesView");

		} catch (Exception ex) {
			return manejarError(model, "Error al mostrar la lista de clientes.", ex.getMessage(), "/showClientesView");
		}
	}

	/**
	 * Muestra el formulario para modificar los datos de un cliente.
	 *
	 * @param clienteDni NIF cifrado del cliente.
	 * @param model      Modelo de la vista.
	 * @return Vista con el formulario de modificación del cliente.
	 */
	@GetMapping("/showClienteMod")
	public String mostrarModCliente(@RequestParam String clienteDni, Model model) {
		String nifDescifrado;
		try {
			nifDescifrado = Cifrado.descifrar(clienteDni);
			// Obtención de empleado a modificar
			final Cliente clienteToMod = clienteServiceI.obtenerPorNif(nifDescifrado);

			// Carga de datos al modelo
			model.addAttribute("cliente", clienteToMod);

			return "clienteModificar";
		} catch (Exception ex) {
			return manejarError(model, "Error al procesar el cliente.", ex.getMessage(), "/showClienteMod");
		}

	}

	/*
	 * ACCIONES
	 */

	/**
	 * Busca clientes según los parámetros introducidos y opcionalmente los ordena.
	 *
	 * @param nombre         Nombre del cliente.
	 * @param apellido       Apellido del cliente.
	 * @param email          Correo electrónico.
	 * @param numeroContacto Número de contacto.
	 * @param dni            DNI del cliente.
	 * @param ordenarPor     Criterio de ordenación.
	 * @param model          Modelo de la vista.
	 * @return Vista con la lista filtrada de clientes.
	 */
	@GetMapping("/actSearchCliente")
	public String buscarClientes(@RequestParam(required = false) String nombre,
			@RequestParam(required = false) String apellido, @RequestParam(required = false) String email,
			@RequestParam(required = false) String numeroContacto, @RequestParam(required = false) String dni,
			@RequestParam(required = false) String ordenarPor, // Parámetro de orden (opcional)
			Model model) { // Parámetro de orden (opcional)

		try {
			nombre = (nombre != null && nombre.trim().isEmpty()) ? null : nombre;
			apellido = (apellido != null && apellido.trim().isEmpty()) ? null : apellido;
			email = (email != null && email.trim().isEmpty()) ? null : email;
			numeroContacto = (numeroContacto != null && numeroContacto.trim().isEmpty()) ? null : numeroContacto;
			dni = (dni != null && dni.trim().isEmpty()) ? null : dni;
			ordenarPor = (ordenarPor != null && ordenarPor.trim().isEmpty()) ? null : ordenarPor;
			List<Cliente> listaClientes;
			// Si se pasan los parámetros de orden, usar la búsqueda con orden
			if (ordenarPor != null) {
				String ordenTipo = null;
				if (ordenarPor != null) {
					if (ordenarPor.contains("Ascendente")) {
						ordenTipo = "ASC";
					} else {
						ordenTipo = "DESC";
					}
				}
				listaClientes = clienteServiceI.buscarClientesConOrden(nombre, apellido, email, numeroContacto, dni,
						ordenarPor, ordenTipo);
				
				Map<String, String> nifCifrados = new HashMap<>();

				for (Cliente c : listaClientes) {
					nifCifrados.put(c.getNif(), Cifrado.cifrar(c.getNif()));
				}
				
				model.addAttribute("clienteListView", listaClientes);
				model.addAttribute("nifCifrados", nifCifrados);

				return "clienteView";
			} else {
				// Si no se pasan los parámetros de orden, usar la búsqueda sin orden
				listaClientes = clienteServiceI.buscarClientesSinOrden(nombre, apellido, email, numeroContacto, dni);
				model.addAttribute("clienteListView", listaClientes);

				return "clienteView";

			}
		} catch (ParametroBusquedaException ex) {
			return manejarError(model, "Parámetros de búsqueda no válidos.", ex.getMessage(), "/actSearchCliente");

		} catch (Exception ex) {
			return manejarError(model, "Hubo un problema al buscar clientes.", ex.getMessage(), "/actSearchCliente");

		}

	}

	/**
	 * Inserta un nuevo cliente en la base de datos.
	 *
	 * @param newCliente Cliente a insertar.
	 * @param result     Resultado de la validación.
	 * @param model      Modelo de la vista.
	 * @return Redirección a la vista de clientes.
	 */
	@PostMapping("/actAddCliente")
	private String insertarCliente(@Valid @ModelAttribute("newCliente") Cliente newCliente, BindingResult result,
			Model model) {
		try {
			if (result.hasErrors()) {
				throw new IllegalArgumentException("Parámetros de cliente erróneos: " + result.getAllErrors());
			}

			// Verificar si el NIF ya existe antes de insertar
			if (clienteServiceI.existeNif(newCliente.getNif())) {
				model.addAttribute("NIFExist", true);
				return "clienteInsertar";
			}

			// Verificar si el Email ya existe antes de insertar
			if (clienteServiceI.existeEmail(newCliente.getEmail())) {
				model.addAttribute("EmailExist", true);
				return "clienteInsertar";
			}

			// Verificar si el Numero telefono ya existe antes de insertar
			if (clienteServiceI.existeTelefono(newCliente.getNumeroContacto())) {
				model.addAttribute("NumberExist", true);
				return "clienteInsertar";
			}

			clienteServiceI.insertarCliente(newCliente);

		} catch (IllegalArgumentException ex) {
			return manejarError(model, "Error al añadir cliente.", ex.getMessage(), "/actAddCliente");

		} catch (DataAccessException ex) {
			return manejarError(model, "Problema al acceder a la base de datos.", ex.getMessage(), "/actAddCliente");

		} catch (Exception ex) {
			return manejarError(model, "Error inesperado al añadir cliente.", ex.getMessage(), "/actAddCliente");

		}
		return "redirect:showClientesView";
	}

	/**
	 * Modifica un cliente existente con los nuevos datos proporcionados.
	 *
	 * @param cliente Cliente modificado.
	 * @param result  Resultado de la validación.
	 * @param model   Modelo de la vista.
	 * @return Redirección a la vista de clientes.
	 */
	@PostMapping("/actModCliente")
	private String modificarCliente(@Valid @ModelAttribute("cliente") Cliente cliente, BindingResult result,
			Model model) {
		try {
			Cliente clienteToMod = clienteServiceI.obtenerPorId(cliente.getId());
			clienteToMod.setNombre(cliente.getNombre());
			clienteToMod.setApellidos(cliente.getApellidos());
			clienteToMod.setAnyoNacimiento(cliente.getAnyoNacimiento());
			clienteToMod.setDireccion(cliente.getDireccion());
			clienteToMod.setEmail(cliente.getEmail());
			clienteToMod.setNumeroContacto(cliente.getNumeroContacto());
			clienteToMod.setNif(cliente.getNif());

			if (result.hasErrors()) {
				throw new Exception("Parámetros de cliente erróneos");
			} else {
				clienteServiceI.actualizarCliente(clienteToMod);
			}

		} catch (Exception ex) {
			return manejarError(model, "Error al modificar cliente.", ex.getMessage(), "/actModCliente");

		}
		return "redirect:showClientesView";
	}

	/**
	 * Elimina un cliente a partir de su ID.
	 *
	 * @param clienteId DNI del cliente.
	 * @param model     Modelo de la vista.
	 * @return Redirección a la vista de clientes.
	 */
	@GetMapping("/actDropCliente")
	public String eliminarCliente(@RequestParam String clienteId, Model model) {
		try {
			clienteServiceI.eliminarClientePorDni(clienteId);
		} catch (DataAccessException ex) {
			return manejarError(model, "Hubo un problema al eliminar el cliente.", ex.getMessage(), "/actDropCliente");
		} catch (Exception ex) {
			return manejarError(model, "Error al eliminar cliente.", ex.getMessage(), "/actDropCliente");

		}
		return "redirect:showClientesView";
	}

	private String manejarError(Model model, String mensaje, String detalle, String ruta) {
		model.addAttribute("mensaje", mensaje);
		model.addAttribute("detalle", detalle);
		model.addAttribute("ruta", ruta);
		return "errorPage";
	}

}
