package co.edu.eia.ecocampus.test;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import co.edu.eia.ecocampus.excepcion.EcoCampusException;
import co.edu.eia.ecocampus.modelo.UsuarioCampus;
import co.edu.eia.ecocampus.repositorio.RegistroCentral;

public class RegistroCentralTest {

    private RegistroCentral registro;

    @BeforeEach
    void setUp() {
        registro = RegistroCentral.getInstancia();
        registro.reiniciar();
    }

    @Test
    void testRegistrarUsuarioExitoso() throws EcoCampusException {
        String idUnico = "U_TEST_" + System.currentTimeMillis();
        UsuarioCampus u = new UsuarioCampus(idUnico, "Carlos", "carlos@eia.edu.co", "Estudiante");
        registro.registrarUsuario(u);
        assertNotNull(registro.getPersonas().buscar(idUnico));
    }

    @Test
    void testRegistrarUsuarioDuplicadoLanzaExcepcion() throws EcoCampusException {
        String idUnico = "U_TEST_DUP_" + System.currentTimeMillis();
        UsuarioCampus u1 = new UsuarioCampus(idUnico, "Carlos", "carlos@eia.edu.co", "Estudiante");
        UsuarioCampus u2 = new UsuarioCampus(idUnico, "Carlos", "carlos@eia.edu.co", "Estudiante");
        
        registro.registrarUsuario(u1);
        assertThrows(EcoCampusException.class, () -> {
            registro.registrarUsuario(u2);
        });
    }
}