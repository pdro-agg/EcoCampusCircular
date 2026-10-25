package co.edu.eia.ecocampus.repositorio;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import co.edu.eia.ecocampus.core.Identificable;
import co.edu.eia.ecocampus.excepcion.IdentificadorDuplicadoException;

/**
 * Repositorio generico en memoria. Reemplaza a los cinco repositorios con arreglo.
 * - Map<String, T>: busqueda directa por identificador y control de duplicados.
 * - LinkedHashMap: ademas conserva el orden de registro.
 * - T extends Identificable: permite llamar getId() sin casting.
 */
public class RepositorioMemoria<T extends Identificable> {
	private Map<String, T> elementos;

	public RepositorioMemoria() {
		this.elementos = new LinkedHashMap<>();
	}

	public boolean existe(String id) {
		return elementos.containsKey(id);
	}

	// throws avisa que registrar puede fallar; throw lanza el error cuando el id ya existe
	public void registrar(T elemento) throws IdentificadorDuplicadoException {
		if (elemento == null) {
			throw new IllegalArgumentException("El elemento no puede ser nulo");
		}
		if (existe(elemento.getId())) {
			throw new IdentificadorDuplicadoException("Ya existe un elemento con el identificador " + elemento.getId());
		}
		elementos.put(elemento.getId(), elemento);
	}

	public T buscar(String id) {
		return elementos.get(id);
	}

	public List<T> listar() {
		return new ArrayList<>(elementos.values()); // copia: no se expone la coleccion interna
	}

	public int getTotal() {
		return elementos.size();
	}
	public void eliminar(String id) {
		elementos.remove(id);
	}
}
