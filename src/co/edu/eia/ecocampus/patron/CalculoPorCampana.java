package co.edu.eia.ecocampus.patron;

import co.edu.eia.ecocampus.modelo.CategoriaMaterial;

/** Estrategia: una campaña define un valor fijo por kilo, sin importar el material. */
public class CalculoPorCampana implements EstrategiaCalculoEcoPuntos {
	private int puntosPorKilo;

	public CalculoPorCampana(int puntosPorKilo) {
		this.puntosPorKilo = puntosPorKilo;
	}

	@Override
	public int calcular(CategoriaMaterial categoria, double peso) {
		if (peso <= 0 || puntosPorKilo < 0) {
			return 0;
		}
		return (int) Math.round(peso * puntosPorKilo);
	}
}
