package co.edu.eia.ecocampus.modelo;

import co.edu.eia.ecocampus.core.Disponible;

public class Operador extends Persona implements Disponible {
	private String idOperador;
	private boolean disponible;
	private boolean habilitadoResiduosEspeciales;

	public Operador(String idPersona, String nombre, String correo, String idOperador) {
		super(idPersona, nombre, correo);
		this.idOperador = idOperador;
		this.disponible = true;
		this.habilitadoResiduosEspeciales = false;
	}

	public String getIdOperador() {
		return idOperador;
	}

	public boolean getDisponible() {
		return disponible;
	}

	@Override
	public boolean estaDisponible() {
		return disponible;
	}

	public boolean getHabilitadoResiduosEspeciales() {
		return habilitadoResiduosEspeciales;
	}

	public void ocupar() {
		this.disponible = false;
	}

	public void desocupar() {
		this.disponible = true;
	}

	public void habilitarResiduosEspeciales() {
		this.habilitadoResiduosEspeciales = true;
	}

	@Override
	public String toString() {
		return super.toString() + "Id operador: " + idOperador + "\n" + "Disponible: " + disponible + "\n"
				+ "Habilitado residuos especiales: " + habilitadoResiduosEspeciales;
	}
}
