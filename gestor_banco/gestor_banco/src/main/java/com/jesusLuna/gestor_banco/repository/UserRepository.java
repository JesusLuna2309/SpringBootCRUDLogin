package com.jesusLuna.gestor_banco.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.jesusLuna.gestor_banco.entity.User;


/**
 * Repositorio para la entidad {@link User}. Proporciona métodos para realizar
 * operaciones CRUD sobre la tabla de usuarios.
 * 
 * Extiende {@link JpaRepository}, lo que permite el acceso a métodos estándar
 * como save, findById, findAll, delete, entre otros.
 * 
 * También define un método personalizado para buscar usuarios por su nombre de
 * usuario.
 * 
 * @author Jesús
 */
public interface UserRepository extends JpaRepository<User, Integer> {

	/**
	 * Busca un usuario por su nombre de usuario.
	 * 
	 * @param username Nombre de usuario del usuario a buscar.
	 * @return Un {@link Optional} que contiene el usuario si se encuentra, o vacío
	 *         si no.
	 */
	Optional<User> findByUsername(String username);

}
