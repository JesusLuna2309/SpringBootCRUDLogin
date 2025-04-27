package com.jesusLuna.gestor_banco.service;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.jesusLuna.gestor_banco.entity.Operacion;
import com.jesusLuna.gestor_banco.exception.OperacionNoEncontradaException;
import com.jesusLuna.gestor_banco.repository.OperacionesRepo;



/**
 * Implementación del servicio de operaciones bancarias. Proporciona métodos
 * para obtener, insertar y eliminar operaciones.
 * 
 * @author Jesús Luna
 */
@Service
public class OperacionesServiceImpl implements OperacionesService {

	@Autowired
	private OperacionesRepo operacionesRepo;

	/**
	 * Obtiene la lista de todas las operaciones ordenadas por fecha descendente.
	 * 
	 * @return una lista de operaciones ordenadas por fecha (más reciente primero)
	 */
	@Override
	public List<Operacion> obtenerOperaciones() {
		return operacionesRepo.findAll(Sort.by(Sort.Direction.DESC, "fecha"));
	}

	/**
	 * Busca una operación por su identificador.
	 * 
	 * @param id el ID de la operación a buscar
	 * @return la operación encontrada
	 * @throws OperacionNoEncontradaException si no se encuentra la operación
	 */
	@Override
	public Operacion obtenerPorCodigo(long id) {
		Optional<Operacion> operacion = operacionesRepo.findById(id);
		if (operacion.isPresent()) {
			return operacion.get();
		} else {
			throw new OperacionNoEncontradaException("La operación con el ID " + id + " no fue encontrada.");
		}
	}

	/**
	 * Inserta una nueva operación en la base de datos.
	 * 
	 * @param o la operación a insertar
	 */
	@Override
	public void insertarOperaciones(Operacion o) {
		operacionesRepo.save(o);
	}

	/**
	 * Elimina una operación de la base de datos por su identificador.
	 * 
	 * @param codigo el ID de la operación a eliminar
	 * @throws OperacionNoEncontradaException si no se encuentra la operación
	 */
	@Override
	public void eliminarOperacion(long codigo) {
		Optional<Operacion> operacion = operacionesRepo.findById(codigo);
		if (operacion.isPresent()) {
			operacionesRepo.deleteById(codigo);
		} else {
			throw new OperacionNoEncontradaException(
					"No se puede eliminar, la operación con el ID " + codigo + " no fue encontrada.");
		}
	}
}
