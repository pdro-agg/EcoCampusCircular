package co.edu.eia.ecocampus.test;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import co.edu.eia.ecocampus.excepcion.IdentificadorDuplicadoException;
import co.edu.eia.ecocampus.modelo.PuntoEcologico;
import co.edu.eia.ecocampus.repositorio.RegistroCentral;
import co.edu.eia.ecocampus.repositorio.RepositorioMemoria;

public class RepositorioMemoriaTest {

    private RepositorioMemoria<PuntoEcologico> repo;

    @BeforeEach
    void setUp() {
        RegistroCentral.getInstancia().reiniciar();
        repo = new RepositorioMemoria<>();
    }

    @Test
    void testRegistrarElementoExitoso() throws IdentificadorDuplicadoException {
        PuntoEcologico punto = new PuntoEcologico("P01", "Bloque A", 100);
        repo.registrar(punto);
        assertEquals(1, repo.getTotal());
        assertNotNull(repo.buscar("P01"));
    }

    @Test
    void testRegistrarElementoDuplicadoLanzaExcepcion() throws IdentificadorDuplicadoException {
        PuntoEcologico p1 = new PuntoEcologico("P01", "Bloque A", 100);
        PuntoEcologico p2 = new PuntoEcologico("P01", "Bloque B", 200);
        repo.registrar(p1);

        assertThrows(IdentificadorDuplicadoException.class, () -> {
            repo.registrar(p2);
        });
    }

    @Test
    void testRegistrarElementoNuloLanzaExcepcion() {
        assertThrows(IllegalArgumentException.class, () -> {
            repo.registrar(null);
        });
    }
}