package co.edu.eia.ecocampus.modelo;

import java.util.ArrayList;
import java.util.List;

import co.edu.eia.ecocampus.excepcion.OperacionNoPermitidaException;

public class Recoleccion {
	private int numero;
	private String fecha;
	private String observaciones;
	private Operador operadorResponsable;
	// List: los materiales se registran en orden y pueden repetirse categorias
	private List<MaterialRecolectado> materiales;

	public Recoleccion(int numero, String fecha, Operador operadorResponsable) {
		this.numero = numero;
		this.fecha = fecha;
		this.operadorResponsable = operadorResponsable;
		this.materiales = new ArrayList<>();
	}

	public Recoleccion(int numero, String fecha, Operador operadorResponsable, String observaciones) {
		this(numero, fecha, operadorResponsable);
		this.observaciones = observaciones;
	}

	public void agregarMaterial(CategoriaMaterial categoria, double peso) throws OperacionNoPermitidaException {
		if (categoria != null && "ES".equals(categoria.getCodigo())) {
			if (operadorResponsable == null || !operadorResponsable.getHabilitadoResiduosEspeciales()) {
				throw new OperacionNoPermitidaException(
						"Recoleccion " + numero + ": el residuo especial requiere un operador habilitado");
			}
		}
		materiales.add(new MaterialRecolectado(categoria, peso));
	}

	public double getPesoTotal() {
		double pesoTotal = 0;
		for (MaterialRecolectado material : materiales) {
			pesoTotal += material.getPeso();
		}
		return pesoTotal;
	}

	public List<MaterialRecolectado> getMateriales() {
		return new ArrayList<>(materiales); // copia
	}

	public int getNumero() {
		return numero;
	}

	public String getFecha() {
		return fecha;
	}

	public String getObservaciones() {
		return observaciones;
	}

	public Operador getOperadorResponsable() {
		return operadorResponsable;
	}

	@Override
	public String toString() {
		String resultado = "Recoleccion: " + numero + "\n" + "Fecha: " + fecha + "\n" + "Observaciones: "
				+ observaciones + "\n" + "Operador Responsable: " + operadorResponsable.getIdOperador() + "\n"
				+ "Materiales Recolectados:\n";
		for (MaterialRecolectado material : materiales) {
			resultado = resultado + "- Categoria: " + material.getCategoria().getNombre() + ", Peso: "
					+ material.getPeso() + "\n";
		}
		return resultado;
	}
}
