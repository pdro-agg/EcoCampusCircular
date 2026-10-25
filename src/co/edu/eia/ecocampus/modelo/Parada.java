package co.edu.eia.ecocampus.modelo;

public class Parada {
	private int orden;
	private PuntoEcologico puntoEcologico;
	private String accionEsperada;

	public Parada(int orden, PuntoEcologico puntoEcologico, String accionEsperada) {
		this.orden = orden;
		this.puntoEcologico = puntoEcologico;
		this.accionEsperada = accionEsperada;
	}

	public int getOrden() {
		return orden;
	}

	public PuntoEcologico getPuntoEcologico() {
		return puntoEcologico;
	}

	public String getAccionEsperada() {
		return accionEsperada;
	}

	@Override
	public String toString() {
		return "Informe parada: " + "\n" + "Punto ecologico: " + puntoEcologico.getCodigo() + "\n" + "Accion esperada: "
				+ accionEsperada;
	}
}
