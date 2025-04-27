package com.jesusLuna.gestor_banco.service;

import java.util.Date;
import java.util.List;

import com.jesusLuna.gestor_banco.entity.Operacion;


public interface OperacionesService {

	public List<Operacion> obtenerOperaciones();

	public Operacion obtenerPorCodigo(long id);

	public void insertarOperaciones(Operacion o);

	public void eliminarOperacion(long codigo);

}
