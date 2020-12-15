package fr.imt.coffee.machine;

import fr.imt.coffee.machine.exception.LackOfWaterInTankException;
import fr.imt.coffee.machine.exception.MachineNotPluggedException;
import fr.imt.coffee.storage.cupboard.coffee.type.CoffeeType;
import fr.imt.coffee.storage.cupboard.container.CoffeeContainer;
import fr.imt.coffee.storage.cupboard.container.CoffeeMug;
import fr.imt.coffee.storage.cupboard.container.Mug;
import fr.imt.coffee.storage.cupboard.exception.CupNotEmptyException;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.junit.jupiter.api.Assertions;
import org.mockito.Mockito;

import java.util.Random;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;


public class CoffeeMachineCucumberStepsTest {

    public CoffeeMachine coffeeMachine;
    public Mug mug;
    public CoffeeContainer containerWithCoffee;

    @Given("a coffee machine with {double} l of min capacity, {double} l of max capacity, {double} l per h of water flow for the pump")
    public void givenACoffeeMachine(double minimalWaterCapacity, double maximalWaterCapacity, double pumpWaterFlow){
        coffeeMachine = new CoffeeMachine(minimalWaterCapacity, maximalWaterCapacity, pumpWaterFlow);
    }

    @And("a mug with a capacity of {double}")
    public void aMugWithACapacityOf(double mugCapacity) {
        mug = new Mug(mugCapacity);
    }

    @When("I plug the machine to electricity")
    public void iPlugTheMachineToElectricity() {
        coffeeMachine.plugToElectricalPlug();
    }

    @And("I add {double} liter of water")
    public void iAddLiterOfWater(double waterVolume) {
        coffeeMachine.addWaterInTank(waterVolume);
    }

    @And("I made a coffee {string}")
    public void iMadeACoffee(String coffeeType) throws InterruptedException, CupNotEmptyException, LackOfWaterInTankException, MachineNotPluggedException {
        //On créé un mock de l'objet random
        Random randomMock = Mockito.mock(Random.class);
        //On vient ensuite stubber la méthode nextGaussian pour pouvoir controler la valeur retournée
        //ici on veut qu'elle retourne 0.6
        Mockito.when(randomMock.nextGaussian()).thenReturn(0.6);
        //On injecte ensuite le mock créé dans la machine à café
        coffeeMachine.setRandomGenerator(randomMock);

        containerWithCoffee = coffeeMachine.makeACoffee(mug, CoffeeType.valueOf(coffeeType));
    }
    
    @Then("the coffee machine return a coffee mug not empty")
    public void theCoffeeMachineReturnACoffeeMugNotEmpty() {
        Assertions.assertFalse(containerWithCoffee.isEmpty());
    }


    @And("a coffee volume equals to {double}")
    public void aCoffeeVolumeEqualsTo(double coffeeVolume) {
        assertThat(coffeeVolume, is(containerWithCoffee.getCapacity()));
    }

    @And("a coffee mug containing a coffee type {string}")
    public void aCoffeeMugContainingACoffeeType(String coffeeType) {
        assertThat(containerWithCoffee, instanceOf(CoffeeMug.class));
        assertThat(containerWithCoffee.getCoffeeType(), is(CoffeeType.valueOf(coffeeType)));
    }

}
