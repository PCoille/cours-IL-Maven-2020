package fr.imt.coffee.machine;

import fr.imt.coffee.storage.cupboard.coffee.type.CoffeeType;
import fr.imt.coffee.storage.cupboard.container.*;
import fr.imt.coffee.storage.cupboard.exception.CupNotEmptyException;
import fr.imt.coffee.machine.exception.LackOfWaterInTankException;
import fr.imt.coffee.machine.exception.MachineNotPluggedException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.Random;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public class CoffeeMachineTest {
    public CoffeeMachine coffeeMachineUnderTest;

    /**
     * @BeforeEach est une annotation permettant d'exécuter la méthode annotée avant chaque test unitaire
     * Ici avant chaque test on initialise la machine à café
     */
    @BeforeEach
    public void beforeTest(){
        coffeeMachineUnderTest = new CoffeeMachine(0,10,  700);
    }

    /**
     * On vient tester si la machine ne se met pas en défaut
     */
    @Test
    public void testMachineFailureTrue(){
        //On créé un mock de l'objet random
        Random randomMock = Mockito.mock(Random.class);
        //On vient ensuite stubber la méthode nextGaussian pour pouvoir contrôler la valeur retournée
        //ici on veut qu'elle retourne 1.0
        //when : permet de définir quand sur quelle méthode établir le stub
        //thenReturn : va permettre de contrôler la valeur retournée par le stub
        Mockito.when(randomMock.nextGaussian()).thenReturn(1.0);
        //On injecte ensuite le mock créé dans la machine à café
        coffeeMachineUnderTest.setRandomGenerator(randomMock);

        //On vérifie que le booleen outOfOrder est bien à faux avant d'appeler la méthode
        Assertions.assertFalse(coffeeMachineUnderTest.isOutOfOrder());
        //Ou avec Hamcrest
        assertThat(false, is(coffeeMachineUnderTest.isOutOfOrder()));

        //on appelle la méthode qui met la machine en défaut
        //On a mocké l'objet random donc la valeur retournée par nextGaussian() sera 1
        //La machine doit donc se mettre en défaut
        coffeeMachineUnderTest.coffeeMachineFailure();

        Assertions.assertTrue(coffeeMachineUnderTest.isOutOfOrder());
        assertThat(true, is(coffeeMachineUnderTest.isOutOfOrder()));
    }

    /**
     * On vient tester si la machine se met en défaut
     */
    @Test
    public void testMachineFailureFalse(){
        //On créé un mock de l'objet random
        Random randomMock = Mockito.mock(Random.class);
        //On vient ensuite stubber la méthode nextGaussian pour pouvoir controler la valeur retournée
        //ici on veut qu'elle retourne 0.6
        //when : permet de définir quand sur quelle méthode établir le stub
        //thenReturn : va permettre de contrôler la valeur retournée par le stub
        Mockito.when(randomMock.nextGaussian()).thenReturn(0.6);
        //On injecte ensuite le mock créé dans la machine à café
        coffeeMachineUnderTest.setRandomGenerator(randomMock);

        //On vérifie que le booleen outOfOrder est bien à faux avant d'appeler la méthode
        Assertions.assertFalse(coffeeMachineUnderTest.isOutOfOrder());
        //Ou avec Hamcrest
        assertThat(false, is(coffeeMachineUnderTest.isOutOfOrder()));

        //on appelle la méthode qui met la machine en défaut
        //On a mocker l'objet random donc la valeur retournée par nextGaussian() sera 0.6
        //La machine doit donc NE PAS se mettre en défaut
        coffeeMachineUnderTest.coffeeMachineFailure();

        Assertions.assertFalse(coffeeMachineUnderTest.isOutOfOrder());
        //Ou avec Hamcrest
        assertThat(false, is(coffeeMachineUnderTest.isOutOfOrder()));
    }

    /**
     * On test que la machine se branche correctement au réseau électrique
     */
    @Test
    public void testPlugMachine(){
        Assertions.assertFalse(coffeeMachineUnderTest.isPlugged());

        coffeeMachineUnderTest.plugToElectricalPlug();

        Assertions.assertTrue(coffeeMachineUnderTest.isPlugged());
    }

    /**
     * On test qu'une exception est bien levée lorsque que le cup passé en paramètre retourne qu'il n'est pas vide
     * Tout comme le test sur la mise en défaut afin d'avoir un comportement isolé et indépendant de la machine
     * on vient ici mocker un objet Cup afin d'en maitriser complétement son comportement
     * On ne compte pas sur "le bon fonctionnement de la méthode"
     */
    @Test
    public void testMakeACoffeeCupNotEmptyException(){
        Cup mockCup = Mockito.mock(Cup.class);
        Mockito.when(mockCup.isEmpty()).thenReturn(false);

        coffeeMachineUnderTest.plugToElectricalPlug();

        //assertThrows( [Exception class expected], [lambda expression with the method that throws an exception], [exception message expected])
        //AssertThrows va permettre de venir tester la levée d'une exception, ici lorsque que le contenant passé en
        //paramètre n'est pas vide
        //On teste à la fois le type d'exception levée mais aussi le message de l'exception
        Assertions.assertThrows(CupNotEmptyException.class, ()->{
                coffeeMachineUnderTest.makeACoffee(mockCup, CoffeeType.MOKA);
            });
    }

    @Test
    void reset() {
        coffeeMachineUnderTest.setOutOfOrder(true);
        Assertions.assertTrue(coffeeMachineUnderTest.isOutOfOrder());
        coffeeMachineUnderTest.reset();
        Assertions.assertFalse(coffeeMachineUnderTest.isOutOfOrder());
    }

    @Test
    void addWaterInTank() {
        Random randomMock = Mockito.mock(Random.class);
        Mockito.when(randomMock.nextGaussian()).thenReturn(0.6);
        coffeeMachineUnderTest.setRandomGenerator(randomMock);

        double initWaterTankVolume = coffeeMachineUnderTest.getWaterTank().getActualVolume();
        double volumeToAdd = 5;

        coffeeMachineUnderTest.addWaterInTank(volumeToAdd);

        Assertions.assertEquals(initWaterTankVolume + volumeToAdd,
                coffeeMachineUnderTest.getWaterTank().getActualVolume());

    }

    @Test
    void getErrorWhenMakingCoffeeWithoutBeingPluggedIn() throws InterruptedException, CupNotEmptyException, LackOfWaterInTankException, MachineNotPluggedException {
        Random randomMock = Mockito.mock(Random.class);
        Mockito.when(randomMock.nextGaussian()).thenReturn(0.6);
        coffeeMachineUnderTest.setRandomGenerator(randomMock);

        Assertions.assertFalse(coffeeMachineUnderTest.isPlugged());

        Mug mug = new Mug(5);



        Assertions.assertThrows(MachineNotPluggedException.class,
                ()-> coffeeMachineUnderTest.makeACoffee(mug, CoffeeType.ARABICA),
                "You must plug your coffee machine to an electrical plug.");
    }

    @Test
    void getErrorWhenMakingCoffeeWithoutEnoughWater() throws InterruptedException, CupNotEmptyException, LackOfWaterInTankException, MachineNotPluggedException {
        Random randomMock = Mockito.mock(Random.class);
        Mockito.when(randomMock.nextGaussian()).thenReturn(0.6);
        coffeeMachineUnderTest.setRandomGenerator(randomMock);

        coffeeMachineUnderTest.addWaterInTank(10);
        coffeeMachineUnderTest.plugToElectricalPlug();
        Assertions.assertEquals(10, coffeeMachineUnderTest.getWaterTank().getActualVolume());

        Mug mug = new Mug(20);



        Assertions.assertThrows(LackOfWaterInTankException.class,
                ()-> coffeeMachineUnderTest.makeACoffee(mug, CoffeeType.ARABICA),
                "You must add more water in the water tank.");
    }



    @Test
    void getErrorWhenMakingCoffeeInFullCup() throws InterruptedException, CupNotEmptyException, LackOfWaterInTankException, MachineNotPluggedException {
        Random randomMock = Mockito.mock(Random.class);
        Mockito.when(randomMock.nextGaussian()).thenReturn(0.6);
        coffeeMachineUnderTest.setRandomGenerator(randomMock);

        coffeeMachineUnderTest.addWaterInTank(10);
        coffeeMachineUnderTest.plugToElectricalPlug();
        Assertions.assertEquals(10, coffeeMachineUnderTest.getWaterTank().getActualVolume());

        CoffeeMug mug = new CoffeeMug(0.05, CoffeeType.ARABICA);



        Assertions.assertThrows(CupNotEmptyException.class,
                ()-> coffeeMachineUnderTest.makeACoffee(mug, CoffeeType.ARABICA),
                "The container given is not empty.");
    }

    @Test
    void makeCoffeeMugCorrectly() throws InterruptedException, CupNotEmptyException, LackOfWaterInTankException, MachineNotPluggedException {
        Random randomMock = Mockito.mock(Random.class);
        Mockito.when(randomMock.nextGaussian()).thenReturn(0.6);
        coffeeMachineUnderTest.setRandomGenerator(randomMock);

        Assertions.assertEquals(0, coffeeMachineUnderTest.getNbCoffeeMade());

        coffeeMachineUnderTest.addWaterInTank(10);
        coffeeMachineUnderTest.plugToElectricalPlug();
        Assertions.assertEquals(10, coffeeMachineUnderTest.getWaterTank().getActualVolume());



        Mug mug = new Mug(0.05);
        CoffeeContainer coffeeContainer = coffeeMachineUnderTest.makeACoffee(mug, CoffeeType.ARABICA);

        Assertions.assertTrue(coffeeContainer instanceof CoffeeMug);
        Assertions.assertEquals(0.05, coffeeContainer.getCapacity());
        Assertions.assertEquals(CoffeeType.ARABICA, coffeeContainer.getCoffeeType());
        Assertions.assertEquals(1, coffeeMachineUnderTest.getNbCoffeeMade());

        Mug mug2 = new Mug(0.08);
        CoffeeContainer coffeeContainer2 = coffeeMachineUnderTest.makeACoffee(mug2, CoffeeType.MOKA);

        Assertions.assertTrue(coffeeContainer2 instanceof CoffeeMug);
        Assertions.assertEquals(0.08, coffeeContainer2.getCapacity());
        Assertions.assertEquals(CoffeeType.MOKA, coffeeContainer2.getCoffeeType());
        Assertions.assertEquals(2, coffeeMachineUnderTest.getNbCoffeeMade());
    }

    @Test
    void makeCoffeeCupCorrectly() throws InterruptedException, CupNotEmptyException, LackOfWaterInTankException, MachineNotPluggedException {
        Random randomMock = Mockito.mock(Random.class);
        Mockito.when(randomMock.nextGaussian()).thenReturn(0.6);
        coffeeMachineUnderTest.setRandomGenerator(randomMock);
        Assertions.assertEquals(0, coffeeMachineUnderTest.getNbCoffeeMade());

        coffeeMachineUnderTest.addWaterInTank(10);
        coffeeMachineUnderTest.plugToElectricalPlug();
        Assertions.assertEquals(10, coffeeMachineUnderTest.getWaterTank().getActualVolume());

        Cup cup = new Cup(0.05);
        CoffeeContainer coffeeContainer = coffeeMachineUnderTest.makeACoffee(cup, CoffeeType.ARABICA);

        Assertions.assertTrue(coffeeContainer instanceof CoffeeCup);
        Assertions.assertEquals(0.05, coffeeContainer.getCapacity());
        Assertions.assertEquals(CoffeeType.ARABICA, coffeeContainer.getCoffeeType());
        Assertions.assertEquals(1, coffeeMachineUnderTest.getNbCoffeeMade());

        Cup cup2 = new Cup(0.08);
        CoffeeContainer coffeeContainer2 = coffeeMachineUnderTest.makeACoffee(cup2, CoffeeType.MOKA);

        Assertions.assertTrue(coffeeContainer2 instanceof CoffeeCup);
        Assertions.assertEquals(0.08, coffeeContainer2.getCapacity());
        Assertions.assertEquals(CoffeeType.MOKA, coffeeContainer2.getCoffeeType());
        Assertions.assertEquals(2, coffeeMachineUnderTest.getNbCoffeeMade());
    }

    @AfterEach
    public void afterTest(){

    }
}
