package com.jesusLuna.gestor_banco.controller;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.io.exceptions.IOException;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.UnitValue;
import com.jesusLuna.gestor_banco.entity.CuentaBancaria;
import com.jesusLuna.gestor_banco.entity.Operacion;
import com.jesusLuna.gestor_banco.entity.Operacion.TipoOperacion;
import com.jesusLuna.gestor_banco.exception.CuentaNoEncontradaException;
import com.jesusLuna.gestor_banco.exception.SaldoInsuficienteException;
import com.jesusLuna.gestor_banco.methods.Cifrado;
import com.jesusLuna.gestor_banco.methods.Methods;
import com.jesusLuna.gestor_banco.service.CuentaBancariaService;
import com.jesusLuna.gestor_banco.service.OperacionesService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;

/**
 * Controlador encargado de gestionar las operaciones bancarias asociadas a una
 * cuenta.
 * <p>
 * Proporciona funcionalidades para:
 * <ul>
 * <li>Visualizar operaciones de una cuenta.</li>
 * <li>Insertar nuevas operaciones (ingresos, retiros, transferencias,
 * etc.).</li>
 * <li>Consultar detalles de una operación concreta.</li>
 * <li>Eliminar operaciones (solo accesible por administradores).</li>
 * <li>Exportar las operaciones de una cuenta a un archivo PDF.</li>
 * </ul>
 * Este controlador también maneja los errores comunes mediante una vista de
 * error personalizada, y utiliza servicios para acceder y modificar la
 * información de cuentas y operaciones.
 * </p>
 * 
 * <p>
 * Requiere que los servicios {@link OperacionesService} y
 * {@link CuentaBancariaService} estén disponibles mediante inyección de
 * dependencias.
 * </p>
 *
 * @author Jesús
 */
@Controller
public class OperacionesController {

	@Autowired
	public OperacionesService operacionService;

	@Autowired
	public CuentaBancariaService cuentaBancariaService;

	/**
	 * Muestra la vista con todas las operaciones asociadas a una cuenta bancaria.
	 *
	 * @param numCuenta El IBAN cifrado de la cuenta bancaria.
	 * @param model     Modelo de Spring para pasar datos a la vista.
	 * @return Nombre de la vista para mostrar las operaciones o una vista de error
	 *         si falla.
	 */
	@GetMapping("/showOperacionesView")
	public String showOperacionesCuenta(@RequestParam String numCuenta, Model model) {
		String ibanDescifrado;
		try {
			ibanDescifrado = Cifrado.descifrar(numCuenta);
			List<Operacion> operaciones = cuentaBancariaService.obtenerPorNumeroCuenta(ibanDescifrado).getOperaciones();

			model.addAttribute("operacionesListView", operaciones);
			model.addAttribute("numCuentaCifrado", numCuenta);
			return "operacionView";
		} catch (EntityNotFoundException e) {
			return manejarError(model, "Error al cargar operaciones", e.getMessage(), "/showOperacionesView");

		} catch (Exception e) {
			return manejarError(model, "Error al cargar operaciones", e.getMessage(), "/showOperacionesView");

		}
	}

	/**
	 * Muestra el formulario para insertar una nueva operación bancaria.
	 *
	 * @param model     Modelo de Spring para pasar datos a la vista.
	 * @param numCuenta El IBAN cifrado de la cuenta a la que se vinculará la
	 *                  operación.
	 * @return Nombre de la vista para insertar operación o una vista de error si
	 *         falla.
	 */
	@GetMapping("/newOperacionView")
	public String mostrarFormularioOperacion(Model model, @RequestParam(required = false) String numCuenta) {
		String ibanDescifrado;
		try {
			if (numCuenta == null || numCuenta.trim().isEmpty()) {
				throw new IllegalArgumentException("El número de cuenta no puede estar vacío.");
			}

			ibanDescifrado = Cifrado.descifrar(numCuenta);

			CuentaBancaria cuentaBancaria = cuentaBancariaService.obtenerPorNumeroCuenta(ibanDescifrado);
			model.addAttribute("newOperacion", new Operacion());
			model.addAttribute("numCuenta", numCuenta);
			model.addAttribute("tiposOperacion", TipoOperacion.values());
			model.addAttribute("fechaCreacionCuenta", cuentaBancaria.getFechaCreacion());
		} catch (IllegalArgumentException e) {
			return manejarError(model, "Error al mostrar el formulario de operación", e.getMessage(),
					"/newOperacionView");

		} catch (Exception e) {
			return manejarError(model, "Error al mostrar el formulario de operación", e.getMessage(),
					"/newOperacionView");

		}
		return "operacionInsertar";
	}

	/**
	 * Muestra los detalles de una operación específica.
	 *
	 * @param codigo Código identificador de la operación.
	 * @param model  Modelo de Spring para pasar datos a la vista.
	 * @return Nombre de la vista de detalles o vista de error si la operación no se
	 *         encuentra.
	 */
	@GetMapping("/operacionDetails")
	public String operacionDetails(@RequestParam int codigo, Model model) {
		try {
			Operacion operacion = operacionService.obtenerPorCodigo(codigo);
			if (operacion == null) {
				throw new EntityNotFoundException("Operación no encontrada.");
			}
			String numCuenta = operacion.getCuentaBancaria().getNumeroCuenta();
			model.addAttribute("numCuentaCifrado", Cifrado.cifrar(numCuenta));
			model.addAttribute("numCuenta", numCuenta);
			model.addAttribute("operacion", operacion);
		} catch (EntityNotFoundException e) {
			return manejarError(model, "Error al obtener los detalles de la operación", e.getMessage(),
					"/operacionDetails");

		} catch (Exception e) {
			return manejarError(model, "Error al obtener los detalles de la operación", e.getMessage(),
					"/operacionDetails");

		}
		return "operacionDetails";
	}

	/**
	 * Añade una nueva operación bancaria (ingreso, retirada o transferencia) a una
	 * cuenta.
	 *
	 * @param newOperacion Objeto con los datos de la operación a insertar.
	 * @param numCuenta    IBAN cifrado de la cuenta sobre la que se realiza la
	 *                     operación.
	 * @param result       Resultado de validaciones del formulario.
	 * @param model        Modelo de Spring para pasar datos a la vista.
	 * @return Redirección a la vista de operaciones o una vista de error si algo
	 *         falla.
	 * @throws Exception Si ocurre un error durante la validación o proceso de la
	 *                   operación.
	 */
	@PostMapping("/addOperacion")
	public String addOperacion(@Valid @ModelAttribute Operacion newOperacion,
			@RequestParam(required = false) String numCuenta, BindingResult result, Model model) throws Exception {
		String ibanDescifrado;
		try {
			if (result.hasErrors()) {
				throw new Exception("Parámetros de operación erróneos");
			}
			if (newOperacion.getTipo().equals(TipoOperacion.EntradaTransferencia)
					|| newOperacion.getTipo().equals(TipoOperacion.RetiradaTransferencia)) {
				if (!Methods.validarIBAN(newOperacion.getNumCuentaTransferencia())) {
					return manejarError(model, "Error al añadir peracion", "El IBAN introducido no es válido.",
							"/addOperacion");
				}
			}

			ibanDescifrado = Cifrado.descifrar(numCuenta);

			CuentaBancaria cuenta = cuentaBancariaService.obtenerPorNumeroCuenta(ibanDescifrado);
			if (cuenta == null) {
				throw new CuentaNoEncontradaException("Cuenta bancaria no encontrada.");
			}

			float cantidad = newOperacion.getCantidad();
			switch (newOperacion.getTipo()) {
			case IngresarDinero, EntradaTransferencia -> {
				cuenta.setSaldo(cuenta.getSaldo() + cantidad);
			}
			case RetirarDinero, RetiradaTransferencia -> {
				if (cuenta.getSaldo() < cantidad) {
					throw new SaldoInsuficienteException("Saldo insuficiente para realizar la operación.");
				}
				cuenta.setSaldo(cuenta.getSaldo() - cantidad);
			}
			}

			cuentaBancariaService.actualizarCuentaBancaria(cuenta);
			newOperacion.setCuentaBancaria(cuenta);
			operacionService.insertarOperaciones(newOperacion);

			return "redirect:/showOperacionesView?numCuenta=" + numCuenta;

		} catch (Exception e) {
			return manejarError(model, "Error al realizar la operación", e.getMessage(), "/addOperacion");

		}

	}

	/**
	 * Elimina una operación existente y revierte sus efectos sobre el saldo de la
	 * cuenta. Solo accesible por usuarios con rol ADMIN.
	 *
	 * @param codigo Código de la operación a eliminar.
	 * @param model  Modelo de Spring para pasar mensajes en caso de error.
	 * @return Redirección a la vista de operaciones de la cuenta o una vista de
	 *         error.
	 */
	@PreAuthorize("hasRole('Admin')")
	@GetMapping("/actDropOperacion")
	public String eliminarCuenta(@RequestParam long codigo, Model model) {

		try {
			Operacion operacion = operacionService.obtenerPorCodigo(codigo);
			if (operacion == null) {
				throw new EntityNotFoundException("Operación no encontrada.");
			}

			CuentaBancaria cuentaBancaria = operacion.getCuentaBancaria();
			if (cuentaBancaria != null) {
				switch (operacion.getTipo()) {
				case IngresarDinero, EntradaTransferencia ->
					cuentaBancaria.setSaldo(cuentaBancaria.getSaldo() - operacion.getCantidad());
				case RetirarDinero, RetiradaTransferencia ->
					cuentaBancaria.setSaldo(cuentaBancaria.getSaldo() + operacion.getCantidad());
				}
				cuentaBancariaService.actualizarCuentaBancaria(cuentaBancaria);
			}

			operacionService.eliminarOperacion(codigo);
			return "redirect:/showOperacionesView?numCuenta=" + Cifrado.cifrar(cuentaBancaria.getNumeroCuenta());

		} catch (EntityNotFoundException e) {
			return manejarError(model, "Error al eliminar la operación", e.getMessage(), "/actDropOperacion");

		} catch (Exception e) {
			return manejarError(model, "Error al eliminar la operación", e.getMessage(), "/actDropOperacion");

		}
	}

	/**
	 * Exporta a PDF el listado de operaciones de una cuenta bancaria.
	 *
	 * @param numCuenta IBAN en texto plano de la cuenta bancaria.
	 * @param response  Objeto HttpServletResponse para enviar el archivo PDF
	 *                  generado.
	 */
	@GetMapping("/exportOperacionesPdf")
	public String exportOperacionesToPdf(Model model, @RequestParam String numCuenta, HttpServletResponse response) {
		try {
			String numCuentaDescifrado = Cifrado.descifrar(numCuenta);
			List<Operacion> operaciones = cuentaBancariaService.obtenerPorNumeroCuenta(numCuentaDescifrado)
					.getOperaciones();

			response.setContentType("application/pdf");
			response.setHeader("Content-Disposition",
					"attachment; filename=operaciones_" + numCuentaDescifrado + ".pdf");

			PdfWriter writer = new PdfWriter(response.getOutputStream());
			PdfDocument pdfDoc = new PdfDocument(writer);
			Document document = new Document(pdfDoc);

			String fechaHoy = LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));

			document.add(new Paragraph("Listado de Operaciones para cuenta: " + numCuentaDescifrado).setBold()
					.setFontSize(14));
			document.add(new Paragraph("Fecha de expedición: " + fechaHoy).setItalic());
			document.add(new Paragraph(" "));

			// Añadimos una columna más si hay alguna transferencia
			boolean contieneTransferencias = operaciones.stream()
					.anyMatch(op -> op.getNumCuentaTransferencia() != null);

			Table table;
			if (contieneTransferencias) {
				table = new Table(UnitValue.createPercentArray(new float[] { 2, 4, 4, 3, 5 }));
				table.addHeaderCell("Código");
				table.addHeaderCell("Fecha");
				table.addHeaderCell("Tipo");
				table.addHeaderCell("Cantidad");
				table.addHeaderCell("Cuenta destino");
			} else {
				table = new Table(UnitValue.createPercentArray(new float[] { 2, 4, 4, 3 }));
				table.addHeaderCell("Código");
				table.addHeaderCell("Fecha");
				table.addHeaderCell("Tipo");
				table.addHeaderCell("Cantidad");
			}

			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

			for (Operacion op : operaciones) {
				table.addCell(String.valueOf(op.getCodigo()));
				table.addCell(op.getFecha().format(formatter));
				table.addCell(op.getTipo().name()); // O usa tu método personalizado tipo.getNombreBonito()
				table.addCell(String.format("%.2f €", op.getCantidad()));

				if (contieneTransferencias) {
					table.addCell(op.getNumCuentaTransferencia() != null ? op.getNumCuentaTransferencia() : "-");
				}
			}

			document.add(table);
			document.close();

			return "redirect:showOperacionesView?numCuenta=" + numCuenta;

		} catch (Exception e) {
			return manejarError(model, "No se pudo exportar el listado de operaciones.", e.getMessage(),
					"/exportOperacionesPdf");
		}
	}

	/**
	 * Método de utilidad para centralizar el manejo de errores. Agrega los
	 * atributos necesarios al modelo y devuelve el nombre de la vista de error.
	 *
	 * @param model   Modelo de Spring para pasar información del error a la vista.
	 * @param mensaje Mensaje de error general.
	 * @param detalle Detalle técnico del error.
	 * @param ruta    Ruta sugerida para regresar o reintentar.
	 * @return Nombre de la vista de error.
	 */
	private String manejarError(Model model, String mensaje, String detalle, String ruta) {
		model.addAttribute("mensaje", mensaje);
		model.addAttribute("detalle", detalle);
		model.addAttribute("ruta", ruta);
		return "errorPage";
	}
}
