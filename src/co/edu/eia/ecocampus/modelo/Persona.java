package co.edu.eia.ecocampus.modelo;

import java.util.Objects;

import co.edu.eia.ecocampus.core.Identificable;

public class Persona implements Identificable {
	private String idPersona;
	private String nombre;
	private String correo;

	public Persona(String idPersona, String nombre, String correo) {
		this.idPersona = idPersona;
		this.nombre = nombre;
		this.correo = correo;
	}

	public String getIdPersona() {
		return idPersona;
	}

	@Override
	public String getId() {
		return idPersona;
	}

	// Dos personas son la misma si tienen el mismo id (lo usa el Set de participantes)
	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof Persona)) {
			return false;
		}
		Persona otra = (Persona) obj;
		return idPersona.equals(otra.idPersona);
	}

	@Override
	public int hashCode() {
		return Objects.hash(idPersona);
	}

	public String getNombre() {
		return nombre;
	}

	public String getCorreo() {
		return correo;
	}

	@Override
	public String toString() { // toString() es un método heredado de Object que define cómo se representa un
								// objeto como texto. Se sobrescribe con @Override, y Java lo llama
								// automáticamente al hacer println(objeto) o
		return "Información personal:" + "\n" + "ID personal: " + idPersona + "\n" // concatenarlo con +. En nuestras
																					// clases, cada subclase de Persona
																					// lo sobrescribe y llama
																					// super.toString() para no repetir
																					// los datos heredados.
				+ "Nombre: " + nombre + "\n" + "Correo Electronico: " + correo + "\n";
	}
}
