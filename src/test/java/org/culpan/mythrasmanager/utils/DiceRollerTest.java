package org.culpan.mythrasmanager.utils;

import org.junit.Test;

import static org.junit.Assert.*;

public class DiceRollerTest {
    @Test
    public void testTokenize1() {
        DiceRoller diceRoller = new DiceRoller();
        String [] tokens = diceRoller.tokenize("2d6");
        assertEquals(3, tokens.length);
        assertEquals("2", tokens[0]);
        assertEquals("d", tokens[1]);
        assertEquals("6", tokens[2]);
    }

    @Test
    public void testTokenize2() {
        DiceRoller diceRoller = new DiceRoller();
        String [] tokens = diceRoller.tokenize("2d6 +2");
        assertEquals(5, tokens.length);
        assertEquals("2", tokens[0]);
        assertEquals("d", tokens[1]);
        assertEquals("6", tokens[2]);
        assertEquals("+", tokens[3]);
        assertEquals("2", tokens[4]);
    }

    @Test
    public void testTokenize3() {
        DiceRoller diceRoller = new DiceRoller();
        String [] tokens = diceRoller.tokenize("71 - 2 d6 +2");
        assertEquals(7, tokens.length);
        assertEquals("71", tokens[0]);
        assertEquals("-", tokens[1]);
        assertEquals("2", tokens[2]);
        assertEquals("d", tokens[3]);
        assertEquals("6", tokens[4]);
        assertEquals("+", tokens[5]);
        assertEquals("2", tokens[6]);
    }

    @Test
    public void testRoll1() throws Exception {
        DiceRoller diceRoller = new DiceRoller(new Integer[] {1, 2, 3});
        assertEquals(3, diceRoller.roll("3"));
    }

    @Test
    public void testRoll2() throws Exception {
        DiceRoller diceRoller = new DiceRoller(new Integer[] {1, 2, 3});
        assertEquals(9, diceRoller.roll("3d6"));
    }

    @Test
    public void testRoll3() throws Exception {
        DiceRoller diceRoller = new DiceRoller(new Integer[] {1, 2, 3});
        assertEquals(19, diceRoller.roll("3d6+10"));
    }

    @Test
    public void testRoll4() throws Exception {
        DiceRoller diceRoller = new DiceRoller(new Integer[] {1, 2, 3});
        assertEquals(19, diceRoller.roll("10 +3d6"));
    }

    @Test
    public void testRoll5() throws Exception {
        DiceRoller diceRoller = new DiceRoller(new Integer[] {1, 2, 3});
        assertEquals(29, diceRoller.roll("10 +3d6+10"));
    }

    @Test
    public void testRoll6() throws Exception {
        DiceRoller diceRoller = new DiceRoller(new Integer[] {1, 2, 3, 4, 5});
        assertEquals(8, diceRoller.roll("3d6+10 - 2d8"));
    }
}