package co.edu.eia.ecocampus.modelo;

public class MaterialRecolectado {
	private CategoriaMaterial categoria;
	private double peso;

	public MaterialRecolectado(CategoriaMaterial categoria, double peso) {
		this.categoria = categoria;
		this.peso = peso;
	}

	public CategoriaMaterial getCategoria() {
		return categoria;
	}

	public double getPeso() {
		return peso;
	}
}
