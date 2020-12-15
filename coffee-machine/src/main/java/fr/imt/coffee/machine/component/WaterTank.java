package fr.imt.coffee.machine.component;

public class WaterTank {
    private final double maxVolume;
    private final double minVolume;
    private double actualVolume;

    /**
     * Réservoir d'eau de la cafetière.
     * @param initialVolume Volume d'eau à mettre dans le réservoir à sa création
     * @param minVolume Volume d'eau minimal du réservoir
     * @param maxVolume Volume d'eau maximal du réservoir
     */
    public WaterTank(double initialVolume, double minVolume, double maxVolume){
        this.maxVolume = maxVolume;
        this.minVolume = minVolume;
        this.actualVolume = initialVolume;
    }

    /**
     * Réduit le volume d'eau du réservoir
     * @param waterVolumeToDecrease Volume d'eau à enlever
     */
    public void decreaseWaterVolume(double waterVolumeToDecrease){
        this.actualVolume -= waterVolumeToDecrease;
    }

    /**
     * Augmente le volume d'eau dans le réservoir
     * @param waterVolumeToIncrease Volume d'eau à ajouter dans le réservoir
     */
    public void increaseWaterVolume(double waterVolumeToIncrease){
        this.actualVolume += waterVolumeToIncrease;
    }

    public double getMaxVolume() {
        return maxVolume;
    }

    public double getMinVolume() {
        return minVolume;
    }

    public double getActualVolume() {
        return actualVolume;
    }
}

