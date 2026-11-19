package com.jobportal.talenthub;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

//import static org.junit.jupiter.api.Assertions.assertEquals;

public class FirstJUnitTest {

    Calculator calculator = new Calculator();


    @Test
    void firstTest() {

        int actual = calculator.add(30, 20);
//        assertEquals(50, actual);

        Assertions.assertEquals(50, actual);
    }

    @Test
    void addNegativeNumbersTest() {

        int actual = calculator.add(-30, -20);
//        assertEquals(-50, actual);

        Assertions.assertEquals(-50, actual);
    }

    @Test
    void addNegativeAndPositiveNumbersTest() {

        int actual = calculator.add(-50, 90);

//        assertEquals(40, actual);

        Assertions.assertEquals(40, actual);

    }

    @Test
    void secondTest() {
        int actual = 10 - 4;
//        assertEquals(6, actual);

        Assertions.assertEquals(6, actual);
    }
}
