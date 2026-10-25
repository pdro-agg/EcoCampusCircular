package co.edu.eia.ecocampus.test;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import co.edu.eia.ecocampus.excepcion.EcoCampusException;
import co.edu.eia.ecocampus.modelo.PuntoEcologico;
import co.edu.eia.ecocampus.repositorio.RegistroCentral;

public class EliminacionConsistenciaTest {

    private RegistroCentral registro;

    @BeforeEach
    void setUp() {
        registro = RegistroCentral.getInstancia();
        registro.reiniciar();
    }

    @Test
    void testEliminarPuntoSinReportesExitoso() throws EcoCampusException {
        PuntoEcologico p = new PuntoEcologico("P99", "Bloque Z", 50);
        registro.registrarPuntoEcologico(p);

        assertNotNull(registro.getPuntos().buscar("P99"));
        
        registro.eliminarPuntoEcologico("P99");
        assertNull(registro.getPuntos().buscar("P99"));
    }

    @Test
    void testEliminarPuntoInexistenteLanzaExcepcion() {
        assertThrows(EcoCampusException.class, () -> {
            registro.eliminarPuntoEcologico("P_NO_EXISTE");
        });
    }
}