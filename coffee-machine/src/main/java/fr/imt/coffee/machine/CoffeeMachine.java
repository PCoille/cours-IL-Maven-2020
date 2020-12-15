package fr.imt.coffee.machine;

import fr.imt.coffee.machine.component.ElectricalResistance;
import fr.imt.coffee.machine.component.WaterPump;
import fr.imt.coffee.machine.component.WaterTank;
import fr.imt.coffee.machine.exception.LackOfWaterInTankException;
import fr.imt.coffee.machine.exception.MachineNotPluggedException;
import fr.imt.coffee.storage.cupboard.coffee.type.CoffeeType;
import fr.imt.coffee.storage.cupboard.container.*;
import fr.imt.coffee.storage.cupboard.exception.CupNotEmptyException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Random;

public class CoffeeMachine {

    public static final Logger logger = LogManager.getLogger(CoffeeMachine.class);

    private final WaterTank waterTank;
    private final WaterPump waterPump;
    private final ElectricalResistance electricalResistance;
    private boolean isPlugged;
    private boolean isOutOfOrder;
    private int nbCoffeeMade;
    private Random randomGenerator;

    public CoffeeMachine(double minWaterTank, double maxWaterTank, double pumpingCapacity){
        this.waterTank = new WaterTank(0, minWaterTank, maxWaterTank);
        this.waterPump = new WaterPump(pumpingCapacity/3600); //On convertie les L/h en L/seconde
        this.electricalResistance = new ElectricalResistance(1000);
        this.isPlugged = false;
        this.isOutOfOrder = false;
        this.nbCoffeeMade = 0;
        this.randomGenerator = new Random();
    }

    /**
     * Branche la machine à café au réseau électrique
     */
    public void plugToElectricalPlug(){
        isPlugged = true;
    }

    /**
     * RAZ de la machine quand elle est en défaut
     */
    public void reset(){
        isOutOfOrder = false;
    }

    /**
     * Ajoute de l'eau dans le réservoir
     * @param waterVolume Volume d'eau en litres à ajouter
     */
    public void addWaterInTank(double waterVolume){
        this.waterTank.increaseWaterVolume(waterVolume);
    }

    /**
     * Permet de faire couler un café à partir d'un contenant et d'un type de café
     * Doit retourner une instance d'un objet CoffeeMug ou CoffeeCup quand un Mug ou un Cup est passé en paramètre
     * Le contenant du café retourné ne doit pas être vide et doit avoir la même capacité que le contenant passsé en paramètre
     * Le contenant doit également avoir son coffeeType qui est égale au type de café passé en paramètre
     * Le nombre de café de la machine doit être incrémenté de 1
     * @param container Contenant pour faire couler le café
     * @param coffeeType Type de café dans l'énumération CoffeeType.java
     * @return Contenant non vide avec son type de café
     * @throws LackOfWaterInTankException Exception à lever lorsque que l'on manque d'eau dans le réservoir, message "You must plug your coffee machine to an electrical plug."
     * @throws MachineNotPluggedException Exception levée lorsque que la machine n'est pas branchée, message : "You must add more water in the water tank."
     * @throws CupNotEmptyException Exception levée lorsque le contenant donné en paramètre n'est pas vide, message : "The container given is not empty."
     * @throws InterruptedException Exception levée lorsqu'un problème survient dans les Threads lors du sleep
     */
    public CoffeeContainer makeACoffee(Container container, CoffeeType coffeeType) throws LackOfWaterInTankException, InterruptedException, MachineNotPluggedException, CupNotEmptyException {
        if(!isPlugged){
            throw new MachineNotPluggedException("You must plug your coffee machine to an electrical plug.");
        }

        if (waterTank.getActualVolume() < container.getCapacity()){
            throw new LackOfWaterInTankException("You must add more water in the water tank.");
        }

        if (!container.isEmpty()){
            throw new CupNotEmptyException("The container given is not empty.");
        }

        coffeeMachineFailure();

        if(isOutOfOrder){
            logger.warn("The machine is out of order. Please reset the coffee machine");
            return null;
        }

        electricalResistance.waterHeating(container.getCapacity());
        waterPump.pumpWater(container.getCapacity(), waterTank);

        CoffeeContainer coffeeContainer = null;
        if(container instanceof Cup) {
            coffeeContainer = new CoffeeCup((Cup) container, coffeeType);
            nbCoffeeMade++;
        }
        else if(container instanceof Mug) {
            coffeeContainer = new CoffeeMug((Mug) container, coffeeType);
            nbCoffeeMade++;
        }
        else if(container instanceof CoffeeContainer)
            throw new CupNotEmptyException("The container given is not empty.");



        return coffeeContainer;
    }

    /**
     * Tirage aléatoire d'un nombre en suivant une loi normale.
     * Permet de simuler une panne sur la cafetière. Probabilité d'une panne d'environ 32% (1*Omega)
     * Si la valeur absolue du double tiré est supérieur ou égale à 1 alors une on considère une panne
     */
    public void coffeeMachineFailure(){
        double nxt = randomGenerator.nextGaussian();

        isOutOfOrder = (Math.abs(nxt) >= 1);
    }

    public String toString(){
        return "Your coffee machine has : \n" +
        "- water tank : " + waterTank.toString() + "\n" +
        "- water pump : " + waterPump.toString() + "\n" +
        "- electrical resistance : " + electricalResistance + "\n" +
        "- is plugged : " + isPlugged + "\n"+
        "and made " + nbCoffeeMade + " coffees";
    }

    public WaterTank getWaterTank() {
        return waterTank;
    }

    public WaterPump getWaterPump() {
        return waterPump;
    }

    public ElectricalResistance getElectricalResistance() {
        return electricalResistance;
    }

    public boolean isPlugged() {
        return isPlugged;
    }

    public boolean isOutOfOrder() {
        return isOutOfOrder;
    }

    public void setOutOfOrder(boolean outOfOrder) {
        isOutOfOrder = outOfOrder;
    }

    public int getNbCoffeeMade() {
        return nbCoffeeMade;
    }

    public void setNbCoffeeMade(int nbCoffeeMade) {
        this.nbCoffeeMade = nbCoffeeMade;
    }

    public Random getRandomGenerator() {
        return randomGenerator;
    }

    public void setRandomGenerator(Random randomGenerator) {
        this.randomGenerator = randomGenerator;
    }
}
