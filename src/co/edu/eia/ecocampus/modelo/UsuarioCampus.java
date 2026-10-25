package co.edu.eia.ecocampus.modelo;

import java.util.ArrayList;
import java.util.List;

import co.edu.eia.ecocampus.excepcion.OperacionNoPermitidaException;

public class UsuarioCampus extends Persona {
	private String tipoVinculacion;
	private int ecoPuntos;
	private List<MovimientoEcoPuntos> movimientos;

	public UsuarioCampus(String idPersona, String nombre, String correo, String tipoVinculacion) {
		super(idPersona, nombre, correo);
		this.tipoVinculacion = tipoVinculacion;
		this.ecoPuntos = 0;
		this.movimientos = new ArrayList<>();
	}

	public UsuarioCampus(String idPersona, String nombre, String correo, String tipoVinculacion, int ecoPuntos) {
		super(idPersona, nombre, correo);
		this.tipoVinculacion = tipoVinculacion;
		this.ecoPuntos = Math.max(0, ecoPuntos);
		this.movimientos = new ArrayList<>();
	}

	public String getTipoVinculacion() {
		return tipoVinculacion;
	}

	public int getEcoPuntos() {
		return ecoPuntos;
	}

	public void setEcoPuntos(int ecoPuntos) {
		this.ecoPuntos = Math.max(0, ecoPuntos);
	}

	/**
	 * Incrementa los eco-puntos del usuario para la gestión desde las vistas/paneles.
	 */
	public void adicionarEcoPuntos(int puntos) {
		if (puntos > 0) {
			this.ecoPuntos += puntos;
		}
	}

	// Regla: los eco-puntos no pueden ser negativos y todo movimiento conserva concepto y fecha
	public void acumularEcoPuntos(MovimientoEcoPuntos movimiento) throws OperacionNoPermitidaException {
		if (movimiento == null || movimiento.getConcepto() == null || movimiento.getFecha() == null) {
			throw new OperacionNoPermitidaException("Todo movimiento de eco-puntos requiere concepto y fecha");
		}
		if (movimiento.getPuntos() < 0) {
			throw new OperacionNoPermitidaException("Los eco-puntos no pueden ser negativos");
		}
		movimientos.add(movimiento);
		this.ecoPuntos = this.ecoPuntos + movimiento.getPuntos();
	}

	public List<MovimientoEcoPuntos> getMovimientos() {
		return new ArrayList<>(movimientos); // copia
	}

	@Override
	public String toString() {
		return super.toString() + "Tipo vinculacion: " + tipoVinculacion + "\n" + "Eco Puntos: " + ecoPuntos;
	}
}