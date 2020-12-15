package fr.imt.coffee.machine.component;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class WaterTankTest {
    //TODO: Check for water below min or above max
    WaterTank waterTank;

    @BeforeEach
    void setUp() {
        waterTank = new WaterTank(15, 10, 20);
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void decreaseWaterVolume() {
        assertEquals(15, waterTank.getActualVolume());

        waterTank.decreaseWaterVolume(3);

        assertEquals(12, waterTank.getActualVolume());
    }

    @Test
    void increaseWaterVolume() {
        assertEquals(15, waterTank.getActualVolume());

        waterTank.increaseWaterVolume(3);

        assertEquals(18, waterTank.getActualVolume());
    }
}