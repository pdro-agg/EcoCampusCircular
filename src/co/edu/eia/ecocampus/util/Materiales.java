package co.edu.eia.ecocampus.util;

import java.util.Arrays;
import java.util.List;

import co.edu.eia.ecocampus.modelo.CategoriaMaterial;

/** Catalogo unico de ca	tegorias de material, para no repetir codigos ("PL", "OR", "ES") por todo el codigo. */
public final class Materiales {
	public static final CategoriaMaterial PLASTICO = new CategoriaMaterial("PL", "Plástico");
	public static final CategoriaMaterial ORGANICO = new CategoriaMaterial("OR", "Orgánico");
	public static final CategoriaMaterial ESPECIAL = new CategoriaMaterial("ES", "Residuo especial");

	private Materiales() {
	}

	public static List<CategoriaMaterial> todos() {
		return Arrays.asList(PLASTICO, ORGANICO, ESPECIAL);
	}
}
