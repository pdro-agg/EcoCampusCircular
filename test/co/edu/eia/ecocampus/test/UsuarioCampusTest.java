package co.edu.eia.ecocampus.test;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import co.edu.eia.ecocampus.excepcion.OperacionNoPermitidaException;
import co.edu.eia.ecocampus.modelo.MovimientoEcoPuntos;
import co.edu.eia.ecocampus.modelo.UsuarioCampus;
import co.edu.eia.ecocampus.repositorio.RegistroCentral;

public class UsuarioCampusTest {

    private UsuarioCampus usuario;

    @BeforeEach
    void setUp() {
        RegistroCentral.getInstancia().reiniciar();
        usuario = new UsuarioCampus("U01", "Pedro", "pedro@eia.edu.co", "Estudiante");
    }

    @Test
    void testAcumularEcoPuntosExitoso() throws OperacionNoPermitidaException {
        MovimientoEcoPuntos mov = new MovimientoEcoPuntos(50, "Reciclaje PET", "2026-10-22");
        usuario.acumularEcoPuntos(mov);
        assertEquals(50, usuario.getEcoPuntos());
    }

    @Test
    void testAcumularPuntosNegativosLanzaExcepcion() {
        MovimientoEcoPuntos mov = new MovimientoEcoPuntos(-10, "Ajuste invalido", "2026-10-22");
        assertThrows(OperacionNoPermitidaException.class, () -> {
            usuario.acumularEcoPuntos(mov);
        });
    }

    @Test
    void testMovimientoNuloLanzaExcepcion() {
        assertThrows(OperacionNoPermitidaException.class, () -> {
            usuario.acumularEcoPuntos(null);
        });
    }
}