package co.edu.eia.ecocampus.test;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import co.edu.eia.ecocampus.modelo.CategoriaMaterial;
import co.edu.eia.ecocampus.patron.CalculoPorCampana;
import co.edu.eia.ecocampus.patron.CalculoPorMaterial;
import co.edu.eia.ecocampus.repositorio.RegistroCentral;
import co.edu.eia.ecocampus.util.Materiales;

public class CalculoEcoPuntosTest {

    private CategoriaMaterial materialPrueba;

    @BeforeEach
    void setUp() {
        RegistroCentral.getInstancia().reiniciar();
        materialPrueba = Materiales.PLASTICO;
    }

    @Test
    void testCalculoPorMaterialNormal() {
        CalculoPorMaterial estrategia = new CalculoPorMaterial();
        int puntos = estrategia.calcular(materialPrueba, 10.0);
        assertEquals(100, puntos); // plastico: 10 puntos por kilo
    }

    @Test
    void testCalculoPorMaterialCero() {
        CalculoPorMaterial estrategia = new CalculoPorMaterial();
        int puntos = estrategia.calcular(materialPrueba, 0.0);
        assertEquals(0, puntos);
    }

    @Test
    void testCalculoPorCampanaNormal() {
        CalculoPorCampana estrategia = new CalculoPorCampana(10); // 10 puntos por kilo
        int puntos = estrategia.calcular(materialPrueba, 5.0);
        assertEquals(50, puntos);
    }

    @Test
    void testCalculoPorCampanaCero() {
        CalculoPorCampana estrategia = new CalculoPorCampana(10);
        int puntos = estrategia.calcular(materialPrueba, 0.0);
        assertEquals(0, puntos);
    }
}