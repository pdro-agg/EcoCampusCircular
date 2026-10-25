package co.edu.eia.ecocampus.modelo;

import java.util.HashSet;
import java.util.Set;

import co.edu.eia.ecocampus.core.Identificable;

public class PuntoEcologico implements Identificable {
	private String codigo;
	private String ubicacion;
	private int capacidad;
	private String estado;
	// Set: un punto no puede admitir dos veces la misma categoria (usa equals/hashCode de CategoriaMaterial)
	private Set<CategoriaMaterial> categorias;

	public PuntoEcologico(String codigo, String ubicacion, int capacidad) {
		this.codigo = codigo;
		this.ubicacion = ubicacion;
		this.capacidad = capacidad;
		this.estado = "Activo";
		this.categorias = new HashSet<>();
	}

	// add() del Set retorna false si la categoria ya estaba: no hace falta buscar antes
	public boolean agregarCategoria(CategoriaMaterial categoria) {
		return categoria != null && categorias.add(categoria);
	}

	public boolean admiteCategoria(CategoriaMaterial categoria) {
		return categoria != null && categorias.contains(categoria);
	}

	public Set<CategoriaMaterial> getCategorias() {
		return new HashSet<>(categorias); // copia
	}

	public void activar() {
		this.estado = "Activo";
	}

	public void desactivar() {
		this.estado = "Inactivo";
	}

	@Override
	public String getId() {
		return codigo;
	}

	public String getCodigo() {
		return codigo;
	}

	public String getUbicacion() {
		return ubicacion;
	}

	public int getCapacidad() {
		return capacidad;
	}

	public String getEstado() {
		return estado;
	}

}
