package co.edu.eia.ecocampus.modelo;

/**
 * Un movimiento de eco-puntos. Regla de negocio: todo movimiento conserva
 * concepto y fecha (por eso son obligatorios en el constructor).
 */
public class MovimientoEcoPuntos {
	private int puntos;
	private String concepto;
	private String fecha;

	public MovimientoEcoPuntos(int puntos, String concepto, String fecha) {
		this.puntos = puntos;
		this.concepto = concepto;
		this.fecha = fecha;
	}

	public int getPuntos() {
		return puntos;
	}

	public String getConcepto() {
		return concepto;
	}

	public String getFecha() {
		return fecha;
	}

	@Override
	public String toString() {
		return "+" + puntos + " eco-puntos - " + concepto + " (" + fecha + ")";
	}
}
