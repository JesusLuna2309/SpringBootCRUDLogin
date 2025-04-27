package com.jesusLuna.gestor_banco.controller;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.jesusLuna.gestor_banco.entity.Cliente;
import com.jesusLuna.gestor_banco.entity.CuentaBancaria;
import com.jesusLuna.gestor_banco.entity.CuentaBancaria.TipoCuenta;
import com.jesusLuna.gestor_banco.methods.Cifrado;
import com.jesusLuna.gestor_banco.methods.Methods;
import com.jesusLuna.gestor_banco.service.ClienteService;
import com.jesusLuna.gestor_banco.service.CuentaBancariaService;
import com.jesusLuna.gestor_banco.service.OperacionesService;

import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;

/**
 * Controlador encargado de manejar las vistas y acciones relacionadas con las
 * cuentas bancarias.
 */
@Controller
public class CuentaBancariaController {

	/** Servicio para gestionar cuentas bancarias. */

	@Autowired
	private CuentaBancariaService cuentaBancariaService;

	/** Servicio para gestionar clientes. */
	@Autowired
	private ClienteService clienteService;

	/** Servicio para gestionar operaciones bancarias. */
	@Autowired
	private OperacionesService operacionesService;

	/*
	 * VISTAS
	 */

	/**
	 * Muestra la vista con todas las cuentas bancarias registradas.
	 *
	 * @param model Modelo para enviar datos a la vista
	 * @return Nombre de la plantilla Thymeleaf
	 */
	@GetMapping("/showCuentasView")
	public String mostrarCuentas(Model model) {

		try {
			final List<CuentaBancaria> listaCuentaBancaria = cuentaBancariaService.obtenerCuentasBancarias();
			List<TipoCuenta> tiposCuenta = Arrays.asList(TipoCuenta.values());

			// Creamos una lista auxiliar con los IBAN cifrados
			Map<String, String> ibanCifrados = new HashMap<>();

			for (CuentaBancaria c : listaCuentaBancaria) {
				ibanCifrados.put(c.getNumeroCuenta(), Cifrado.cifrar(c.getNumeroCuenta()));
			}

			model.addAttribute("IBANCifrados", ibanCifrados);
			model.addAttribute("tiposCuenta", tiposCuenta);
			model.addAttribute("cuentaListView", listaCuentaBancaria);

		} catch (Exception e) {
			return manejarError(model, "Error al obtener las cuentas bancarias", e.getMessage(), "/showCuentasView");

		}
		return "cuentaView";
	}

	/**
	 * Muestra la vista para modificar una cuenta bancaria específica.
	 *
	 * @param cuentaId ID cifrado de la cuenta bancaria
	 * @param model    Modelo para enviar datos a la vista
	 * @return Nombre de la plantilla para modificar la cuenta
	 */
	@GetMapping("/showCuentasMod")
	public String mostrarModCuenta(@RequestParam String cuentaId, Model model, HttpSession session) {
		String ibanDescifrado;
		try {
			ibanDescifrado = Cifrado.descifrar(cuentaId);
			final CuentaBancaria cuentaToMod = cuentaBancariaService.obtenerPorNumeroCuenta(ibanDescifrado);
			if (cuentaToMod == null) {
				throw new EntityNotFoundException("Cuenta bancaria no encontrada.");
			}
			List<Cliente> listCliente = (List<Cliente>) session.getAttribute("clientesTemporales");

			if (listCliente == null || listCliente.isEmpty()) {
				listCliente = clienteService.obtenerClientesCuentaBancaria(ibanDescifrado);
			}

			List<Cliente> listClienteNotAssigned = (List<Cliente>) session.getAttribute("listClienteNotAssigned");

			if (listClienteNotAssigned == null || listClienteNotAssigned.isEmpty()) {
				listClienteNotAssigned = clienteService.obtenerClientesExcludeCuentaBancaria(ibanDescifrado);
			}

			model.addAttribute("cuenta", cuentaToMod);
			model.addAttribute("clientesTemporales", listCliente);
			session.setAttribute("clientesTemporales", listCliente);
			model.addAttribute("listClienteNotAssigned", listClienteNotAssigned);
			session.setAttribute("listClienteNotAssigned", listClienteNotAssigned);
			model.addAttribute("TipoCuenta", CuentaBancaria.TipoCuenta.values());
			return "cuentaModificar";

		} catch (EntityNotFoundException e) {
			return manejarError(model, "Error al cargar datos para modificar cuenta", e.getMessage(),
					"/showCuentasMod");

		} catch (Exception e) {
			return manejarError(model, "Error al cargar datos para modificar cuenta", e.getMessage(),
					"/showCuentasMod");

		}
	}

	/**
	 * Muestra las cuentas bancarias de un cliente específico.
	 *
	 * @param clienteNif NIF cifrado del cliente
	 * @param model      Modelo para enviar datos a la vista
	 * @return Vista de cuentas
	 */
	@GetMapping("/showClienteCuentas")
	public String mostrarClienteCuentas(@RequestParam String clienteNif, Model model) {
		String nifDescifrado;
		try {
			nifDescifrado = Cifrado.descifrar(clienteNif);

			final Cliente cliente = clienteService.obtenerPorNif(nifDescifrado);
			if (cliente == null) {
				throw new EntityNotFoundException("Cliente no encontrado con el NIF: " + nifDescifrado);
			}

			List<CuentaBancaria> cuentasBancarias = new ArrayList<>(cliente.getMisCuentas());

			// Creamos una lista auxiliar con los IBAN cifrados
			Map<String, String> ibanCifrados = new HashMap<>();

			for (CuentaBancaria c : cuentasBancarias) {
				ibanCifrados.put(c.getNumeroCuenta(), Cifrado.cifrar(c.getNumeroCuenta()));
			}

			model.addAttribute("IBANCifrados", ibanCifrados);

			model.addAttribute("cuentaListView", cuentasBancarias);
		} catch (EntityNotFoundException e) {
			return manejarError(model, "Error al mostrar cuentas del cliente", e.getMessage(), "/showClienteCuentas");

		} catch (Exception e) {
			return manejarError(model, "Error al mostrar cuentas del cliente", e.getMessage(), "/showClienteCuentas");

		}
		return "cuentaView";
	}

	/*
	 * ACCIONES
	 */

	/**
	 * Realiza la búsqueda de cuentas según parámetros proporcionados.
	 *
	 * @param numeroCuenta Número de cuenta
	 * @param tipoCuenta   Tipo de cuenta
	 * @param ordenarPor   Criterio de ordenación
	 * @param model        Modelo para enviar datos a la vista
	 * @return Vista con resultados filtrados
	 */
	@GetMapping("/actSearchCuenta")
	public String submitBuscarCuentaForm(@RequestParam(required = false) String numeroCuenta,
			@RequestParam(required = false) String tipoCuenta, @RequestParam(required = false) String ordenarPor,
			Model model) {

		try {
			numeroCuenta = (numeroCuenta != null && numeroCuenta.trim().isEmpty()) ? null : numeroCuenta;
			tipoCuenta = (tipoCuenta != null && tipoCuenta.trim().isEmpty()) ? null : tipoCuenta;
			ordenarPor = (ordenarPor != null && ordenarPor.trim().isEmpty()) ? null : ordenarPor;
			String ordenTipo = null;

			if (ordenarPor != null) {
				if (ordenarPor.equalsIgnoreCase("fechaAscendente")) {
					ordenarPor = "fechaCreacion";
					ordenTipo = "ASC";
				} else {
					ordenTipo = "DESC";
					ordenarPor = "fechaCreacion";
				}
			}

			List<CuentaBancaria> listaCuentas = cuentaBancariaService.buscarCuentas(numeroCuenta, tipoCuenta,
					ordenarPor, ordenTipo);
			// Creamos una lista auxiliar con los IBAN cifrados
						Map<String, String> ibanCifrados = new HashMap<>();

						for (CuentaBancaria c : listaCuentas) {
							ibanCifrados.put(c.getNumeroCuenta(), Cifrado.cifrar(c.getNumeroCuenta()));
						}

						model.addAttribute("IBANCifrados", ibanCifrados);
			model.addAttribute("cuentaListView", listaCuentas);
			model.addAttribute("tiposCuenta", CuentaBancaria.TipoCuenta.values());

		} catch (Exception e) {
			return manejarError(model, "Error al buscar cuentas bancarias", e.getMessage(), "/actSearchCuenta");

		}
		return "cuentaView";
	}

	/**
	 * Muestra la vista para crear una nueva cuenta bancaria.
	 *
	 * @param model Modelo para enviar datos a la vista
	 * @return Vista de inserción de cuenta
	 */
	@GetMapping("/newCuentasView")
	public String redirectToNewCuentaTemplate(Model model) {
		List<Cliente> clientes = clienteService.obtenerClientes();

		model.addAttribute("tiposCuenta", TipoCuenta.values());
		model.addAttribute("newCuenta", new CuentaBancaria());
		model.addAttribute("clientesList", clientes);

		return "cuentaInsertar";
	}

	/**
	 * Procesa la inserción de una nueva cuenta bancaria.
	 *
	 * @param model      Modelo para enviar datos a la vista
	 * @param newCuenta  Datos de la nueva cuenta validados
	 * @param clienteDni NIF del cliente asociado
	 * @param result     Resultado de validaciones
	 * @return Redirección o vista de error
	 * @throws Exception En caso de error inesperado
	 */
	@PostMapping("/actAddCuenta")
	private String insertarCuenta(Model model, @Valid @ModelAttribute CuentaBancaria newCuenta,
			@RequestParam String clienteDni, BindingResult result) throws Exception {
		try {
			if (result.hasErrors()) {
				model.addAttribute("mensaje", "Parámetros de la cuenta incorrectos.");
				return "errorPage"; // Redirige a la página de error con el mensaje
			}

			Cliente cliente = clienteService.obtenerPorNif(clienteDni);
			if (cliente == null) {
				throw new EntityNotFoundException("Cliente no encontrado.");
			}

			List<Cliente> clientes = new ArrayList<>();
			clientes.add(cliente);
			newCuenta.setMisClientes(clientes);
			newCuenta.setNumeroCuenta(Methods.generateIBAN("ES", 8, 12));

			cuentaBancariaService.insertarCuentaBancaria(newCuenta);
		} catch (EntityNotFoundException e) {
			return manejarError(model, "Error al insertar cuenta bancaria", e.getMessage(), "/actAddCuenta");

		} catch (Exception e) {
			return manejarError(model, "Error al insertar cuenta bancaria", e.getMessage(), "/actAddCuenta");

		}
		return "cuentaView";
	}

	/**
	 * Procesa la modificación de una cuenta existente.
	 *
	 * @param model  Modelo para enviar datos a la vista
	 * @param cuenta Cuenta modificada validada
	 * @param result Resultado de validaciones
	 * @return Redirección o vista de error
	 * @throws Exception En caso de error inesperado
	 */
	@PostMapping("/actModCuenta")
	private String modificarCuenta(Model model, @Valid @ModelAttribute("cuenta") CuentaBancaria cuenta,
			BindingResult result, HttpSession session) throws Exception {
		try {
			if (result.hasErrors()) {
				model.addAttribute("mensaje", "Parámetros de la cuenta incorrectos.");
				return "errorPage"; // Redirige a la página de error con el mensaje
			}

			// Obtener la cuenta y los clientes temporales
			CuentaBancaria cuentaToMod = cuentaBancariaService.obtenerPorNumeroCuenta(cuenta.getNumeroCuenta());
			if (cuentaToMod == null) {
				throw new EntityNotFoundException("Cuenta bancaria no encontrada.");
			}

			// Obtener los clientes temporales del modelo
			List<Cliente> clientesTemporales = (List<Cliente>) session.getAttribute("clientesTemporales");

			if (clientesTemporales != null) {
				// Establecer la relación entre los clientes y la cuenta
				cuenta.setMisClientes(clientesTemporales);
			}

			cuentaToMod.setTipoCuenta(cuenta.getTipoCuenta());
			cuentaToMod.setFechaCreacion(cuenta.getFechaCreacion());
			cuentaToMod.setSaldo(cuenta.getSaldo());

			cuentaBancariaService.actualizarCuentaBancaria(cuentaToMod);
		} catch (EntityNotFoundException e) {
			return manejarError(model, "Error al modificar cuenta bancaria", e.getMessage(), "/actModCuenta");

		} catch (Exception e) {
			return manejarError(model, "Error al modificar cuenta bancaria", e.getMessage(), "/actModCuenta");

		}
		return "redirect:showCuentasView";
	}

	/**
	 * Elimina una cuenta bancaria por su número de cuenta.
	 *
	 * @param numCuenta Número de cuenta
	 * @param model     Modelo para enviar datos a la vista
	 * @return Redirección a la vista principal o vista de error
	 */
	@GetMapping("/actDropCuenta")
	public String eliminarCuenta(@RequestParam String numCuenta, Model model) {
		try {
			if (cuentaBancariaService.obtenerPorNumeroCuenta(numCuenta) == null) {
				throw new EntityNotFoundException("La cuenta bancaria no existe.");
			}
			cuentaBancariaService.eliminarCuentaBancariaPorNumCuenta(numCuenta);
		} catch (EntityNotFoundException e) {
			return manejarError(model, "Error al eliminar cuenta bancaria", e.getMessage(), "/actDropCuenta");

		} catch (Exception e) {
			return manejarError(model, "Error al eliminar cuenta bancaria", e.getMessage(), "/actDropCuenta");

		}
		return "redirect:showCuentasView";

	}

	/**
	 * Elimina un cliente asociado a una cuenta bancaria.
	 *
	 * @param cuentaId  ID de la cuenta bancaria
	 * @param clienteId ID del cliente
	 * @param model     Modelo para enviar datos a la vista
	 * @return Redirección o vista de error
	 */
	@GetMapping("/actDropClienteCuenta")
	public String eliminarClientedelaCuenta(@RequestParam String cuentaId, @RequestParam String clienteId, Model model,
			HttpSession session) {
		try {
			CuentaBancaria cuenta = cuentaBancariaService.obtenerPorNumeroCuenta(cuentaId);
			if (cuenta == null) {
				throw new EntityNotFoundException("Cuenta bancaria no encontrada.");
			}

			String ibanCifrado = Cifrado.cifrar(cuentaId);

			// Obtener la lista de clientes temporales del modelo
			List<Cliente> clientesTemporales = (List<Cliente>) session.getAttribute("clientesTemporales");
			if (clientesTemporales == null) {
				clientesTemporales = new ArrayList<>();
			}
			List<Cliente> listClienteNotAssigned = (List<Cliente>) session.getAttribute("listClienteNotAssigned");
			if (listClienteNotAssigned == null) {
				listClienteNotAssigned = new ArrayList<>();
			}

			Cliente cliente = clienteService.obtenerPorNif(clienteId);
			if (cliente == null) {
				throw new EntityNotFoundException("Cliente no encontrado.");
			}
			// Eliminar el cliente de la lista temporal
			if (!clientesTemporales.removeIf(c -> c.getNif().equals(clienteId))) {
				throw new Exception("Error al intentar borrar el cliente la lista de clientes");

			}
			listClienteNotAssigned.add(cliente);
			// Actualizar la lista de clientes temporales en el modelo
			session.setAttribute("clientesTemporales", clientesTemporales);
			model.addAttribute("clientesTemporales", clientesTemporales);
			session.setAttribute("listClienteNotAssigned", listClienteNotAssigned);
			model.addAttribute("listClienteNotAssigned", listClienteNotAssigned);
			model.addAttribute("cuenta", cuenta);

			// Redirigir a la vista de modificación de cuenta con la lista actualizada
			return "redirect:showCuentasMod?cuentaId=" + ibanCifrado;

		} catch (EntityNotFoundException e) {
			return manejarError(model, "Verifica que el cliente y la cuenta estén correctamente seleccionados.",
					e.getMessage(), "/actDropClienteCuenta");

		} catch (Exception e) {
			return manejarError(model, "Hubo un problema al eliminar el cliente de la cuenta.", e.getMessage(),
					"/actDropClienteCuenta");

		}
	}

	/**
	 * Asocia un cliente existente a una cuenta bancaria existente.
	 * <p>
	 * Este método maneja una petición POST para añadir un cliente a una cuenta
	 * bancaria. Valida que tanto la cuenta como el cliente existan, y si es así,
	 * asocia el cliente a la cuenta. En caso de éxito, redirige a la vista de
	 * modificación de la cuenta con el IBAN cifrado. Si ocurre un error, se
	 * redirige a una vista de error personalizada con detalles.
	 * </p>
	 *
	 * @param model        el modelo de la vista para enviar atributos a la
	 *                     plantilla Thymeleaf
	 * @param numeroCuenta el número de cuenta bancaria en texto plano recibido
	 *                     desde el formulario
	 * @param clienteDni   el DNI/NIF del cliente que se desea asociar a la cuenta
	 * @return una redirección a la vista de modificación de cuenta si todo va bien;
	 *         de lo contrario, devuelve una vista de error con mensaje, detalle y
	 *         ruta
	 */
	@PostMapping("/actAddClienteCuenta")
	public String agregarCliente(Model model, @RequestParam String numeroCuenta, @RequestParam String clienteDni,
			HttpSession session) {
		try {
			// Obtener la cuenta y el cliente, igual que antes
			CuentaBancaria cuenta = cuentaBancariaService.obtenerPorNumeroCuenta(numeroCuenta);
			if (cuenta == null) {
				throw new EntityNotFoundException("Cuenta bancaria no encontrada.");
			}

			String ibanCifrado = Cifrado.cifrar(numeroCuenta);

			Cliente cliente = clienteService.obtenerPorNif(clienteDni);
			if (cliente == null) {
				throw new EntityNotFoundException("Cliente no encontrado.");
			}

			// Obtener la lista de clientes temporales desde el modelo, si no existe crearla
			List<Cliente> clientesTemporales = (List<Cliente>) session.getAttribute("clientesTemporales");
			if (clientesTemporales == null) {
				clientesTemporales = new ArrayList<>();
			}
			List<Cliente> listClienteNotAssigned = (List<Cliente>) session.getAttribute("listClienteNotAssigned");
			if (listClienteNotAssigned == null) {
				listClienteNotAssigned = new ArrayList<>();
			}
			// Añadir el cliente temporalmente
			clientesTemporales.add(cliente);
			if (!listClienteNotAssigned.removeIf(c -> c.getNif().equals(cliente.getNif()))) {
				throw new Exception("Error al intentar borrar el cliente de la listas los clientes no asignados");
			}
			// Actualizar el modelo para que la lista de clientes temporales esté disponible
			// en la vista
			session.setAttribute("clientesTemporales", clientesTemporales);
			model.addAttribute("clientesTemporales", clientesTemporales);
			session.setAttribute("listClienteNotAssigned", listClienteNotAssigned);
			model.addAttribute("listClienteNotAssigned", listClienteNotAssigned);
			model.addAttribute("cuenta", cuenta);

			return "redirect:/showCuentasMod?cuentaId=" + ibanCifrado;

		} catch (EntityNotFoundException e) {
			return manejarError(model, "Error al añadir cliente a cuenta bancaria", e.getMessage(),
					"/actAddClienteCuenta");
		} catch (Exception e) {
			return manejarError(model, "Error inesperado al añadir cliente a cuenta bancaria", e.getMessage(),
					"/actAddClienteCuenta");
		}
	}

	/**
	 * Método reutilizable para manejar errores y redirigir a la página de error con
	 * datos útiles.
	 *
	 * @param model   Modelo para la vista
	 * @param mensaje Mensaje breve del error
	 * @param detalle Detalle técnico del error
	 * @param ruta    Ruta donde ocurrió el error
	 * @return Nombre de la plantilla de error
	 */
	private String manejarError(Model model, String mensaje, String detalle, String ruta) {
		model.addAttribute("mensaje", mensaje);
		model.addAttribute("detalle", detalle);
		model.addAttribute("ruta", ruta);
		return "errorPage";
	}
}
