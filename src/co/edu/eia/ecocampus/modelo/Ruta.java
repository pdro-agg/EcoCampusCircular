package co.edu.eia.ecocampus.modelo;

public class Ruta {
	private String id;
	private String nombre;
	private String origen;
	private String destino;

	public Ruta(String id, String nombre, String origen, String destino) {
		this.id = id;
		this.nombre = nombre;
		this.origen = origen;
		this.destino = destino;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getNombre() {
		return nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	public String getOrigen() {
		return origen;
	}

	public void setOrigen(String origen) {
		this.origen = origen;
	}

	public String getDestino() {
		return destino;
	}

	public void setDestino(String destino) {
		this.destino = destino;
	}

	@Override
	public String toString() {
		return "Ruta [" + id + "]: " + nombre + " (" + origen + " -> " + destino + ")";
	}
}