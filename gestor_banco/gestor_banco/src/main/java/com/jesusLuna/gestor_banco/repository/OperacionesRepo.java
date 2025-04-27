package com.jesusLuna.gestor_banco.repository;

import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.jesusLuna.gestor_banco.entity.Operacion;

/**
 * Repositorio para la entidad {@link Operacion}.
 * <p>
 * Proporciona métodos para acceder a operaciones almacenadas en la base de
 * datos según diferentes criterios de búsqueda (tipo, fecha y cantidad).
 * </p>
 * 
 * Extiende de {@link JpaRepository} para obtener funcionalidades CRUD estándar.
 * 
 * @author Jesús
 */
public interface OperacionesRepo extends JpaRepository<Operacion, Long> {

	/**
	 * Busca operaciones por su tipo.
	 * 
	 * @param tipo el tipo de operación (por ejemplo: "ingreso", "retiro", etc.)
	 * @return una lista de operaciones que coinciden con el tipo proporcionado
	 */
	List<Operacion> findByTipo(String tipo);

	/**
	 * Busca operaciones realizadas en una fecha específica.
	 * 
	 * @param fecha la fecha de la operación
	 * @return una lista de operaciones realizadas en la fecha indicada
	 */
	List<Operacion> findByFecha(Date fecha);

	/**
	 * Busca operaciones por una cantidad específica.
	 * 
	 * @param cantidad el valor monetario exacto de la operación
	 * @return una lista de operaciones que coinciden con la cantidad proporcionada
	 */
	List<Operacion> findByCantidad(float cantidad);
}
