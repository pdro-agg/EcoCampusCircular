package co.edu.eia.ecocampus.test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import co.edu.eia.ecocampus.excepcion.EcoCampusException;
import co.edu.eia.ecocampus.excepcion.IdentificadorDuplicadoException;
import co.edu.eia.ecocampus.excepcion.OperacionNoPermitidaException;
import co.edu.eia.ecocampus.modelo.Campana;
import co.edu.eia.ecocampus.modelo.UsuarioCampus;
import co.edu.eia.ecocampus.repositorio.RegistroCentral;

/** Reglas de las campañas: inscripciones sin duplicados y cupo limitado. */
public class CampanaTest {
	private UsuarioCampus ana;
	private UsuarioCampus luis;

	@BeforeEach
	void preparar() {
		RegistroCentral.getInstancia().reiniciar();
		ana = new UsuarioCampus("U1", "Ana", "ana@eia.edu.co", "Estudiante");
		luis = new UsuarioCampus("U2", "Luis", "luis@eia.edu.co", "Docente");
	}

	@Test
	void inscribirDosVecesLaMismaPersonaLanzaExcepcion() throws EcoCampusException {
		Campana campana = new Campana(1, "Reciclaje", 5);
		campana.inscribirParticipante(ana);
		assertThrows(IdentificadorDuplicadoException.class, () -> campana.inscribirParticipante(ana));
		assertEquals(1, campana.getTotalParticipantes());
	}

	@Test
	void inscribirMasAllaDelCupoLanzaExcepcion() throws EcoCampusException {
		Campana campana = new Campana(1, "Reciclaje", 1);
		campana.inscribirParticipante(ana);
		assertThrows(OperacionNoPermitidaException.class, () -> campana.inscribirParticipante(luis));
	}
}
