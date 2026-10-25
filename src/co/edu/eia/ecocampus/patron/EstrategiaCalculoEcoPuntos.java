package co.edu.eia.ecocampus.patron;

import co.edu.eia.ecocampus.modelo.CategoriaMaterial;

/** STRATEGY: cada politica de calculo de eco-puntos implementa esta interfaz. */
public interface EstrategiaCalculoEcoPuntos {
	int calcular(CategoriaMaterial categoria, double peso);
}
