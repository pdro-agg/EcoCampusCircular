package co.edu.eia.ecocampus.modelo;

public class Actividad {
	private String nombre;
	private String fecha;
	private int cupo;

	public Actividad(String nombre, String fecha, int cupo) {
		this.nombre = nombre;
		this.fecha = fecha;
		this.cupo = cupo;
	}

	public String getNombre() {
		return nombre;
	}

	public String getFecha() {
		return fecha;
	}

	public int getCupo() {
		return cupo;
	}

	@Override
	public String toString() {
		return "Actividad: " + nombre + "\n" + "Fecha: " + fecha + "\n" + "Cupo: " + cupo;
	}
}
