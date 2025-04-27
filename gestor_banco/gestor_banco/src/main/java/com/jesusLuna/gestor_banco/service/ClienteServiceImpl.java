package com.jesusLuna.gestor_banco.service;

import java.text.Collator;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.jesusLuna.gestor_banco.entity.Cliente;
import com.jesusLuna.gestor_banco.exception.ClienteNoEncontradoException;
import com.jesusLuna.gestor_banco.exception.ClienteYaExistenteException;
import com.jesusLuna.gestor_banco.repository.ClienteRepo;

/**
 * Servicio que implementa la lógica de negocio para la gestión de clientes.
 */
@Service
public class ClienteServiceImpl implements ClienteService {

	@Autowired
	private ClienteRepo clienteRepo;

	/**
	 * Obtiene todos los clientes registrados.
	 * 
	 * @return lista de clientes.
	 */
	@Override
	public List<Cliente> obtenerClientes() {
		return clienteRepo.findAll();
	}

	/**
	 * Busca un cliente por su ID.
	 * 
	 * @param id identificador del cliente.
	 * @return cliente encontrado.
	 * @throws RuntimeException si el cliente no existe.
	 */
	@Override
	public Cliente obtenerPorId(long id) {
		return clienteRepo.findById(id).orElseThrow(() -> new RuntimeException("Cliente no encontrado"));
	}

	/**
	 * Busca un cliente por su NIF.
	 * 
	 * @param nif NIF del cliente.
	 * @return cliente encontrado o null si no existe.
	 */
	@Override
	public Cliente obtenerPorNif(String nif) {
		return clienteRepo.findByNif(nif);
	}

	/**
	 * Busca clientes por varios parámetros y ordena el resultado según se
	 * especifique.
	 * 
	 * @param nombre         nombre del cliente.
	 * @param apellido       apellido del cliente.
	 * @param email          correo del cliente.
	 * @param numeroContacto teléfono del cliente.
	 * @param dni            DNI del cliente.
	 * @param ordenarPor     campo por el que se ordenará.
	 * @param ordenTipo      tipo de orden ("ASC" o "DESC").
	 * @return lista de clientes filtrados y ordenados.
	 * @throws ParametroBusquedaException si no se proporciona ningún parámetro de
	 *                                    búsqueda.
	 */
	@Override
	public List<Cliente> buscarClientesConOrden(String nombre, String apellido, String email, String numeroContacto,
			String dni, String ordenarPor, String ordenTipo) {

		List<Cliente> clientes = clienteRepo.buscarClientes(nombre, apellido, email, numeroContacto, dni);

		if (ordenarPor.contains("fecha")) {
			if ("ASC".equals(ordenTipo)) {
				clientes.sort(Comparator.comparing(Cliente::getAnyoNacimiento));
			} else {
				clientes.sort(Comparator.comparing(Cliente::getAnyoNacimiento).reversed());
			}
		} else if (ordenarPor.contains("apellido")) {
		    Collator collator = Collator.getInstance(new Locale("es", "ES")); // Español de España
		    collator.setStrength(Collator.PRIMARY); // Ignorar tildes y mayúsculas/minúsculas
			if ("ASC".equals(ordenTipo)) {
				clientes.sort(Comparator.comparing(Cliente::getApellidos, collator));
			} else {
				clientes.sort(Comparator.comparing(Cliente::getApellidos, collator).reversed());
			}
		}

		return clientes;
	}

	/**
	 * Busca clientes por varios parámetros sin ordenarlos.
	 * 
	 * @param nombre         nombre del cliente.
	 * @param apellido       apellido del cliente.
	 * @param email          correo del cliente.
	 * @param numeroContacto teléfono del cliente.
	 * @param dni            DNI del cliente.
	 * @return lista de clientes filtrados.
	 */
	public List<Cliente> buscarClientesSinOrden(String nombre, String apellido, String email, String numeroContacto,
			String dni) {
		return clienteRepo.buscarClientes(nombre, apellido, email, numeroContacto, dni);
	}

	/**
	 * Inserta un nuevo cliente validando que no haya duplicados.
	 * 
	 * @param cliente cliente a insertar.
	 * @throws ClienteYaExistenteException si el NIF, correo o teléfono ya existen.
	 */
	@Override
	public void insertarCliente(Cliente cliente) {
		if (existeNif(cliente.getNif())) {
			throw new ClienteYaExistenteException("El DNI ya está registrado.");
		}
		if (existeEmail(cliente.getEmail())) {
			throw new ClienteYaExistenteException("El correo electrónico ya está registrado.");
		}
		if (existeTelefono(cliente.getNumeroContacto())) {
			throw new ClienteYaExistenteException("El número de teléfono ya está registrado.");
		}

		clienteRepo.save(cliente);
	}

	/**
	 * Actualiza los datos de un cliente existente.
	 * 
	 * @param cliente cliente a actualizar.
	 * @throws ClienteNoEncontradoException si el cliente no existe.
	 */
	@Override
	public void actualizarCliente(Cliente cliente) {
		if (!clienteRepo.existsById(cliente.getId())) {
			throw new ClienteNoEncontradoException("Cliente no encontrado");
		}

		clienteRepo.save(cliente);
	}

	/**
	 * Elimina un cliente por su NIF.
	 * 
	 * @param nif NIF del cliente.
	 * @throws ClienteNoEncontradoException si el cliente no existe.
	 */
	@Override
	public void eliminarClientePorDni(String nif) {
		Cliente cliente = clienteRepo.findByNif(nif);
		if (cliente != null) {
			clienteRepo.delete(cliente);
		} else {
			throw new ClienteNoEncontradoException("Cliente no encontrado");
		}
	}

	/**
	 * Obtiene los clientes asociados a una cuenta bancaria.
	 * 
	 * @param numCuenta número de cuenta bancaria.
	 * @return lista de clientes asociados.
	 */
	@Override
	public List<Cliente> obtenerClientesCuentaBancaria(String numCuenta) {
		return clienteRepo.findClientesByNumeroCuenta(numCuenta);
	}

	/**
	 * Obtiene los clientes que no están asociados a una cuenta bancaria.
	 * 
	 * @param numCuenta número de cuenta bancaria.
	 * @return lista de clientes no asociados.
	 */
	@Override
	public List<Cliente> obtenerClientesExcludeCuentaBancaria(String numCuenta) {
		return clienteRepo.findClientesExcludeNumeroCuenta(numCuenta);
	}

	/**
	 * Verifica si ya existe un cliente con el NIF proporcionado.
	 * 
	 * @param dni NIF a comprobar.
	 * @return true si existe, false si no.
	 */
	@Override
	public boolean existeNif(String dni) {
		return clienteRepo.existsByNif(dni);
	}

	/**
	 * Verifica si ya existe un cliente con el correo proporcionado.
	 * 
	 * @param email correo a comprobar.
	 * @return true si existe, false si no.
	 */
	@Override
	public boolean existeEmail(String email) {
		return clienteRepo.existsByEmail(email);
	}

	/**
	 * Verifica si ya existe un cliente con el número de teléfono proporcionado.
	 * 
	 * @param telefono número de teléfono a comprobar.
	 * @return true si existe, false si no.
	 */
	@Override
	public boolean existeTelefono(String telefono) {
		return clienteRepo.existsByNumeroContacto(telefono);
	}

}
