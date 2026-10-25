package co.edu.eia.ecocampus.modelo;

import co.edu.eia.ecocampus.core.Disponible;

public class ResponsableSostenibilidad extends Persona implements Disponible {
	private int idResponsable;
	private String zonaAsignada;
	private boolean disponible;

	public ResponsableSostenibilidad(String idPersona, String nombre, String correo, int idResponsable,
			String zonaAsignada) {
		super(idPersona, nombre, correo);
		this.idResponsable = idResponsable;
		this.zonaAsignada = zonaAsignada;
		this.disponible = true;
	}

	public int getIdResponsable() {
		return idResponsable;
	}

	public String getZonaAsignada() {
		return zonaAsignada;
	}

	public boolean getDisponible() {
		return disponible;
	}

	@Override
	public boolean estaDisponible() {
		return disponible;
	}

	public void marcarDisponible() {
		this.disponible = true;
	}

	public void marcarNoDisponible() {
		this.disponible = false;
	}

	@Override
	public String toString() {
		return super.toString() + "Id responsable: " + idResponsable + "\n" + "Zona asignada: " + zonaAsignada + "\n"
				+ "Disponible: " + disponible;
	}
}
