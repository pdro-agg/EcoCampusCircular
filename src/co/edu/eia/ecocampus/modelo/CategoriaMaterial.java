package co.edu.eia.ecocampus.modelo;

import java.util.Objects;

public class CategoriaMaterial {
	private String codigo;
	private String nombre;

	public CategoriaMaterial(String codigo, String nombre) {
		this.codigo = codigo;
		this.nombre = nombre;
	}

	public String getCodigo() {
		return codigo;
	}

	public String getNombre() {
		return nombre;
	}

	// Dos categorias son la misma si tienen el mismo codigo (lo usa el Set del punto ecologico)
	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof CategoriaMaterial)) {
			return false;
		}
		CategoriaMaterial otra = (CategoriaMaterial) obj;
		return codigo.equals(otra.codigo);
	}

	@Override
	public int hashCode() {
		return Objects.hash(codigo);
	}

}
