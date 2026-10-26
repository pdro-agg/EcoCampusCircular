package co.edu.eia.ecocampus.test;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import co.edu.eia.ecocampus.excepcion.EcoCampusException;
import co.edu.eia.ecocampus.modelo.PuntoEcologico;
import co.edu.eia.ecocampus.modelo.Reporte;
import co.edu.eia.ecocampus.modelo.UsuarioCampus;
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
    void testNoSeEliminaUnPuntoConReportePendiente() throws EcoCampusException {
        PuntoEcologico p = new PuntoEcologico("P10", "Biblioteca", 50);
        UsuarioCampus autor = new UsuarioCampus("U1", "Ana", "ana@eia.edu.co", "Estudiante");
        registro.registrarPuntoEcologico(p);
        registro.getReportes().registrarSiEsValido(new Reporte(1, autor, p, "Caneca llena", "2026-10-26"));

        assertThrows(EcoCampusException.class, () -> registro.eliminarPuntoEcologico("P10"));
        assertNotNull(registro.getPuntos().buscar("P10"));
    }

    @Test
    void testEliminarPuntoInexistenteLanzaExcepcion() {
        assertThrows(EcoCampusException.class, () -> {
            registro.eliminarPuntoEcologico("P_NO_EXISTE");
        });
    }
}