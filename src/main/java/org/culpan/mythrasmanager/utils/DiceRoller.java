package org.culpan.mythrasmanager.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class DiceRoller {
    public static final Random random = new Random();

    String expr;

    int currentToken = 0;

    String [] tokens;

    Integer [] randomNumbers;

    int currentRandomNumber = 0;

    public DiceRoller(Integer [] randomNumbers) {
        this.randomNumbers = randomNumbers;
    }

    public DiceRoller() {
    }

    public DiceRoller(String expr) {
        this.expr = expr;
    }

    protected int getRandomNumber(int bounds) {
        if (randomNumbers != null) {
            int result = randomNumbers[currentRandomNumber];
            currentRandomNumber++;
            if (currentRandomNumber >= randomNumbers.length) {
                currentRandomNumber = 0;
            }
            return result;
        } else {
            return random.nextInt(bounds);
        }
    }

    protected String [] tokenize(String expr) {
        List<String> result = new ArrayList<>();

        String currentToken = "";
        for (int i = 0; i < expr.length(); i++) {
            char c = expr.charAt(i);
            if (Character.isDigit(c)) {
                currentToken += c;
            } else if (c == 'd' || c == 'D') {
                if (!currentToken.isEmpty()) {
                    result.add(currentToken);
                    currentToken = "";
                }
                result.add("d");
            } else if (c == '+' || c == '-') {
                if (!currentToken.isEmpty()) {
                    result.add(currentToken);
                    currentToken = "";
                }
                result.add(Character.toString(c));
            }
        }

        if (!currentToken.isEmpty()) {
            result.add(currentToken);
        }

        return result.toArray(new String[result.size()]);
    }

    public int roll(String expr) throws Exception {
        currentToken = 0;
        this.expr = expr;
        tokens = tokenize(expr);

        return expr();
    }

    public int roll() throws Exception {
        return roll(expr);
    }

    protected boolean isLastToken() {
        return currentToken + 1 >= tokens.length;
    }

    protected String getCurrentTokent() {
        return tokens[currentToken];
    }

    protected String peakNextToken() {
        if (!isLastToken()) {
            return tokens[currentToken + 1];
        } else {
            return "";
        }
    }

    protected String advanceToken() {
        if (!isLastToken()) {
            currentToken++;
            return getCurrentTokent();
        }
        return "";
    }

    protected int expr() throws Exception {
        if (tokens[currentToken].equals("d") || tokens[currentToken].equals("+") || tokens[currentToken].equals("-")) {
            throw new Exception("Invalid character " + currentToken + " at beginning of expression");
        }

        int result = term();
        if (!isLastToken()) {
            String token = getCurrentTokent();
            advanceToken();
            if (token.equals("-")) {
                result = result - expr();
            } else {
                result = result + expr();
            }
        }

        return result;
    }

    protected int term() {
        int result = 0;

        if (peakNextToken().equals("d")) {
            int numDice = Integer.parseInt(getCurrentTokent());
            advanceToken();
            int diceSides = Integer.parseInt(advanceToken());
            advanceToken();
            for (int i = 0; i < numDice; i++) {
                result += getRandomNumber(diceSides) + 1;
            }
        } else {
            result = Integer.parseInt(getCurrentTokent());
            advanceToken();
        }

        return result;
    }

}
