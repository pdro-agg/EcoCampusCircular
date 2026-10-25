package co.edu.eia.ecocampus.modelo;

import java.util.ArrayList;
import java.util.List;

import co.edu.eia.ecocampus.core.Identificable;
import co.edu.eia.ecocampus.excepcion.OperacionNoPermitidaException;
import co.edu.eia.ecocampus.excepcion.TransicionInvalidaException;
import co.edu.eia.ecocampus.patron.Sujeto;

public class RutaRecoleccion extends Sujeto implements Identificable {
	private int numero;
	private String estado;
	// List: las paradas son ordenadas, el orden importa
	private List<Parada> paradas;

	public RutaRecoleccion(int numero) {
		this.numero = numero;
		this.estado = "Abierta";
		this.paradas = new ArrayList<>();
	}

	public void agregarParada(Parada parada) throws OperacionNoPermitidaException {
		if (!"Abierta".equals(estado)) {
			throw new OperacionNoPermitidaException("Ruta " + numero + ": esta cerrada y no admite paradas");
		}
		if (parada == null || parada.getPuntoEcologico() == null) {
			throw new OperacionNoPermitidaException("Ruta " + numero + ": la parada debe referenciar un punto ecologico");
		}
		if (!"Activo".equals(parada.getPuntoEcologico().getEstado())) {
			throw new OperacionNoPermitidaException(
					"Ruta " + numero + ": el punto " + parada.getPuntoEcologico().getCodigo() + " esta inactivo");
		}
		paradas.add(parada);
	}

	public void cerrarRuta() throws TransicionInvalidaException {
		if (!"Abierta".equals(estado)) {
			throw new TransicionInvalidaException("Ruta " + numero + ": solo se cierra una ruta abierta");
		}
		this.estado = "Cerrada";
		notificarObservadores("Ruta: " + numero + "\n" + "Estado: " + estado);
	}

	public void abrirRuta() throws TransicionInvalidaException {
		if (!"Cerrada".equals(estado)) {
			throw new TransicionInvalidaException("Ruta " + numero + ": solo se abre una ruta cerrada");
		}
		this.estado = "Abierta";
		notificarObservadores("Ruta: " + numero + "\n" + "Estado: " + estado);
	}

	public List<Parada> getParadas() {
		return new ArrayList<>(paradas); // copia
	}

	@Override
	public String getId() {
		return String.valueOf(numero);
	}

	public String getEstado() {
		return estado;
	}

	public int getNumero() {
		return numero;
	}

	public int getTotalParadas() {
		return paradas.size();
	}

	@Override
	public String toString() {
		String resultado = "Ruta: " + numero + "\n" + "Estado: " + estado + "\n";
		for (Parada parada : paradas) {
			resultado = resultado + parada.toString() + "\n";
		}
		return resultado;
	}

}
