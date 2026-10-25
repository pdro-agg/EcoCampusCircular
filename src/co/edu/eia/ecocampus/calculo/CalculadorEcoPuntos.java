package co.edu.eia.ecocampus.calculo;

import co.edu.eia.ecocampus.modelo.CategoriaMaterial;
import co.edu.eia.ecocampus.patron.EstrategiaCalculoEcoPuntos;

/**
 * Contexto del patron STRATEGY: no sabe COMO se calcula, solo delega en la estrategia
 * que tenga. La politica se cambia sin modificar esta clase ni las entidades.
 */
public class CalculadorEcoPuntos {
	private EstrategiaCalculoEcoPuntos estrategia;

	public CalculadorEcoPuntos(EstrategiaCalculoEcoPuntos estrategia) {
		this.estrategia = estrategia;
	}

	public void setEstrategia(EstrategiaCalculoEcoPuntos estrategia) {
		this.estrategia = estrategia;
	}

	public int calcular(CategoriaMaterial categoria, double peso) {
		return estrategia.calcular(categoria, peso);
	}
}
