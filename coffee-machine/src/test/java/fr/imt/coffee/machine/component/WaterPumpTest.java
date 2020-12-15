package fr.imt.coffee.machine.component;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class WaterPumpTest {

    // durée du pompage : (volume en L / débit de la pompe en L/seconde) * 1000 pour les ms * 2
    private double pumpDuration(double volume, double pumpingCapacity) {
        return (volume / pumpingCapacity) * 1000 * 2;
    }

    @BeforeEach
    void setUp() {
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void pumpWater() throws InterruptedException {
        double pumpingCapacity = 5.5;
        double volume = 10;
        WaterPump waterPump = new WaterPump(pumpingCapacity);
        WaterTank waterTank = new WaterTank(30, 60, 10);

        assertEquals(pumpDuration(volume, pumpingCapacity),
                waterPump.pumpWater(volume, waterTank));

        assertEquals(20, waterTank.getActualVolume());
    }
}