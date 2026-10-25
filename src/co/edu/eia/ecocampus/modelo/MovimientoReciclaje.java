package co.edu.eia.ecocampus.modelo;

/** Movimiento que viene de reciclar un material: guarda ademas la categoria y el peso. */
public class MovimientoReciclaje extends MovimientoEcoPuntos {
	private CategoriaMaterial categoria;
	private double peso;

	public MovimientoReciclaje(int puntos, String concepto, String fecha, CategoriaMaterial categoria, double peso) {
		super(puntos, concepto, fecha);
		this.categoria = categoria;
		this.peso = peso;
	}

	public CategoriaMaterial getCategoria() {
		return categoria;
	}

	public double getPeso() {
		return peso;
	}

	@Override
	public String toString() {
		return super.toString() + " [" + categoria.getNombre() + ", " + peso + " kg]";
	}
}
