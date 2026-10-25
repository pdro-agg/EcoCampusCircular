package co.edu.eia.ecocampus.patron;

import java.util.HashMap;
import java.util.Map;

import co.edu.eia.ecocampus.modelo.CategoriaMaterial;

/** Estrategia: los puntos dependen del TIPO de material y de su peso. */
public class CalculoPorMaterial implements EstrategiaCalculoEcoPuntos {
	private static final int PUNTOS_POR_DEFECTO = 10;
	// Map: codigo de categoria -> puntos por kilo
	private Map<String, Integer> puntosPorKilo;

	public CalculoPorMaterial() {
		this.puntosPorKilo = new HashMap<>();
		puntosPorKilo.put("PL", 10);
		puntosPorKilo.put("OR", 6);
		puntosPorKilo.put("ES", 15);
	}

	@Override
	public int calcular(CategoriaMaterial categoria, double peso) {
		if (peso <= 0) {
			return 0; // los eco-puntos no pueden ser negativos
		}
		int factor = PUNTOS_POR_DEFECTO;
		if (categoria != null) {
			factor = puntosPorKilo.getOrDefault(categoria.getCodigo(), PUNTOS_POR_DEFECTO);
		}
		return (int) Math.round(peso * factor);
	}
}
