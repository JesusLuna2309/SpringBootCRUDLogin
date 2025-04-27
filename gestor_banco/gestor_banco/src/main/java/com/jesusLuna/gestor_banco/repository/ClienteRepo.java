package com.jesusLuna.gestor_banco.repository;

import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.jesusLuna.gestor_banco.entity.Cliente;


/**
 * Interfaz de repositorio para la entidad {@link Cliente}.
 * Proporciona operaciones de acceso a datos relacionadas con los clientes, extendiendo 
 * las interfaces {@link JpaRepository} y {@link JpaSpecificationExecutor} de Spring Data JPA.
 */
public interface ClienteRepo extends JpaRepository<Cliente, Long>, JpaSpecificationExecutor<Cliente> {
	
	/**
	 * Encuentra un cliente por su número de identificación fiscal (NIF).
	 *
	 * @param nif El número de identificación fiscal (NIF) del cliente.
	 * @return El cliente con el NIF proporcionado.
	 */
	Cliente findByNif(String nif);
	
	/**
	 * Verifica si ya existe un cliente con el número de identificación fiscal (NIF) proporcionado.
	 *
	 * @param dni El NIF a verificar.
	 * @return true si ya existe un cliente con ese NIF, false en caso contrario.
	 */
	boolean existsByNif(String dni);

	/**
	 * Verifica si ya existe un cliente con el correo electrónico proporcionado.
	 *
	 * @param email El correo electrónico a verificar.
	 * @return true si ya existe un cliente con ese correo electrónico, false en caso contrario.
	 */
	boolean existsByEmail(String email);

	/**
	 * Verifica si ya existe un cliente con el número de contacto proporcionado.
	 *
	 * @param numeroContacto El número de contacto a verificar.
	 * @return true si ya existe un cliente con ese número de contacto, false en caso contrario.
	 */
	boolean existsByNumeroContacto(String numeroContacto);
    
	/**
	 * Busca clientes que coincidan con los parámetros proporcionados. Los parámetros pueden ser parcialmente nulos,
	 * y en caso de que sean nulos, no afectarán a la búsqueda.
	 *
	 * @param nombre El nombre del cliente a buscar (puede ser nulo).
	 * @param apellido El apellido del cliente a buscar (puede ser nulo).
	 * @param email El correo electrónico del cliente a buscar (puede ser nulo).
	 * @param numeroContacto El número de contacto del cliente a buscar (puede ser nulo).
	 * @param dni El NIF del cliente a buscar (puede ser nulo).
	 * @return Una lista de clientes que coinciden con los criterios de búsqueda.
	 */
	@Query("SELECT c FROM Cliente c WHERE " +
	       "(LOWER(c.nombre) LIKE LOWER(CONCAT('%', :nombre, '%')) OR :nombre IS NULL) AND " +
	       "(LOWER(c.apellidos) LIKE LOWER(CONCAT('%', :apellido, '%')) OR :apellido IS NULL) AND " +
	       "(LOWER(c.email) LIKE LOWER(CONCAT('%', :email, '%')) OR :email IS NULL) AND " +
	       "(c.numeroContacto LIKE CONCAT('%', :numeroContacto, '%') OR :numeroContacto IS NULL) AND " +
	       "(c.nif LIKE CONCAT('%', :dni, '%') OR :dni IS NULL)")
    List<Cliente> buscarClientes(@Param("nombre") String nombre,
                                 @Param("apellido") String apellido,
                                 @Param("email") String email,
                                 @Param("numeroContacto") String numeroContacto,
                                 @Param("dni") String dni);
    
	/**
	 * Encuentra todos los clientes que no estén asociados con una cuenta bancaria específica.
	 * 
	 * @param numeroCuenta El número de cuenta bancaria que no debe estar asociado con los clientes.
	 * @return Una lista de clientes que no están asociados a la cuenta bancaria proporcionada.
	 */
	@Query("SELECT c FROM Cliente c WHERE c NOT IN (SELECT cli FROM CuentaBancaria cb JOIN cb.misClientes cli WHERE cb.numeroCuenta = :numeroCuenta) ORDER BY c.nif")
	List<Cliente> findClientesExcludeNumeroCuenta(@Param("numeroCuenta") String numeroCuenta);
	
	/**
	 * Encuentra todos los clientes asociados a una cuenta bancaria específica.
	 * 
	 * @param numeroCuenta El número de cuenta bancaria a buscar.
	 * @return Una lista de clientes asociados a la cuenta bancaria proporcionada.
	 */
	@Query("SELECT c.misClientes FROM CuentaBancaria c WHERE c.numeroCuenta = :numeroCuenta")
	List<Cliente> findClientesByNumeroCuenta(@Param("numeroCuenta") String numeroCuenta);
}
