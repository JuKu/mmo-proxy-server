package com.jukusoft.mmo.proxy.core.utils;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class RandomUtilsTest {

    @Test
    public void testRandomNumberCreator(){
        assertEquals(1, RandomUtils.getRandomNumber(1, 1));

        int max = 100;
        int min = 5;
        for(int i = 0; i < max*10; ++i){
            assertTrue(RandomUtils.getRandomNumber(min, max) < max + 1);
            assertTrue(RandomUtils.getRandomNumber(min, max) > min - 1);
        }
    }

    @Test
    public void testDiceRoller(){
        assertTrue(RandomUtils.rollTheDice(1));

        int chance = 100;
        int trues = 0;
        for(int i = 0; i < chance * 100; ++i){
            if(RandomUtils.rollTheDice(chance)){
                ++trues;
            }
        }
        assertTrue(trues > 0);
        assertTrue(trues < chance * 100);

        assertTrue(trues < 150);
        assertTrue(trues > 50);
    }
}