package com.jesusLuna.gestor_banco.service;

import java.util.Date;
import java.util.List;

import com.jesusLuna.gestor_banco.entity.Cliente;


public interface ClienteService {

	public List<Cliente> obtenerClientes();

	public Cliente obtenerPorNif(String nif);

	public Cliente obtenerPorId(long id);

	public List<Cliente> obtenerClientesExcludeCuentaBancaria(String numCuenta);

	public List<Cliente> obtenerClientesCuentaBancaria(String numCuenta);

	public List<Cliente> buscarClientesConOrden(String nombre, String apellido, String email, String numeroContacto,
			String dni, String ordenarPor, String ordenTipo);

	public List<Cliente> buscarClientesSinOrden(String nombre, String apellido, String email, String numeroContacto,
			String dni);

	public boolean existeNif(String nif);

	public boolean existeEmail(String email);

	public boolean existeTelefono(String telefono);

	public void insertarCliente(Cliente e);

	public void actualizarCliente(Cliente e);

	public void eliminarClientePorDni(String id);

}
