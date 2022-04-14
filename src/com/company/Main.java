package com.company;

import org.jetbrains.annotations.Nullable;

import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.*;

public class Main {

    static Map<String, Operationable> operations = new HashMap<String, Operationable>();
    static Map<ExpressionTypes, String> expressions = new HashMap<ExpressionTypes, String>();

    public static void main(String[] args) {

        Scanner inputSource = new Scanner(System.in);

        expressions.put(ExpressionTypes.ARABIC, "^ *([1-9]|10) *([\\+\\-\\*\\/]) *([1-9]|10) *$");
        expressions.put(ExpressionTypes.ROMANIC, "^ *(V|X|V?I{1,3}|I[VX]) *([\\+\\-\\*\\/]) *(V|X|V?I{1,3}|I[VX]) *$");

        operations.put("+", (x, y) -> x + y);
        operations.put("-", (x, y) -> x - y);
        operations.put("*", (x, y) -> x * y);
        operations.put("/", (x, y) -> x / y);

        System.out.println("\nВведите математическое выражение:");
        String input = inputSource.nextLine();

        try {
            System.out.printf("Результат операции = %s\n", calc(input));
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

    }

    public static String calc(String input) throws Exception {

        ExpressionData expData = getExpressionData(input);

        String result = "";

        if (expData != null) {
            Operationable operation = operations.get(expData.getOperator());
            ExpressionTypes type = expData.getType();

            if (type == ExpressionTypes.ARABIC) {
                int firstOperand = Integer.parseInt(expData.getFirstOperand());
                int secondOperand = Integer.parseInt(expData.getSecondOperand());
                result += operation.calculate(firstOperand, secondOperand);
            }
            if (type == ExpressionTypes.ROMANIC) {
                int firstOperand = NumberConverter.romanToInteger(expData.getFirstOperand());
                int secondOperand = NumberConverter.romanToInteger(expData.getSecondOperand());
                int intResult = operation.calculate(firstOperand, secondOperand);
                if (intResult < 0)
                    throw new Exception("В римской системе нет отрицательных чисел!");
                result += NumberConverter.integerToRoman(intResult);
            }

        } else {

            throw new Exception("Неверное выражение!");

        }

        return result;
    }

    static ExpressionData getExpressionData(String input) {

        Pattern arabicPattern = Pattern.compile(expressions.get(ExpressionTypes.ARABIC), Pattern.CASE_INSENSITIVE);
        Matcher arabicMatcher = arabicPattern.matcher(input);

        Pattern romanPattern = Pattern.compile(expressions.get(ExpressionTypes.ROMANIC), Pattern.CASE_INSENSITIVE);
        Matcher romanMatcher = romanPattern.matcher(input);

        ExpressionData result = null;

        if (arabicMatcher.find())
            result = new ExpressionData(
                    ExpressionTypes.ARABIC,
                    arabicMatcher.group(2),
                    arabicMatcher.group(1),
                    arabicMatcher.group(3));
        if (romanMatcher.find())
            result = new ExpressionData(
                    ExpressionTypes.ROMANIC,
                    romanMatcher.group(2),
                    romanMatcher.group(1),
                    romanMatcher.group(3));

        return result;
    }
}

interface Operationable {
    int calculate(int x, int y);
}

enum ExpressionTypes {
    ARABIC,
    ROMANIC
}

class ExpressionData {
    private ExpressionTypes type;
    private String operator;
    private String firstOperand;
    private String secondOperand;

    ExpressionData(ExpressionTypes type, String operator, String firstOperand, String secondOperand) {
        this.type = type;
        this.operator = operator;
        this.firstOperand = firstOperand;
        this.secondOperand = secondOperand;
    }

    public ExpressionTypes getType() {
        return this.type;
    }

    public String getOperator() {
        return this.operator;
    }

    public String getFirstOperand() {
        return this.firstOperand;
    }

    public String getSecondOperand() {
        return this.secondOperand;
    }
}

class NumberConverter {

    public static @Nullable
    String integerToRoman(int number) {
        if (number > 100 || number <= 0)
            return null;
        StringBuilder result = new StringBuilder();
        for (Integer key : units.descendingKeySet()) {
            while (number >= key) {
                number -= key;
                result.append(units.get(key));
            }
        }
        return result.toString();
    }

    public static int romanToInteger(String roman) {
        roman = roman.toUpperCase();
        Map<Character, Integer> numbersMap = new HashMap<Character, Integer>();
        numbersMap.put('I', 1);
        numbersMap.put('V', 5);
        numbersMap.put('X', 10);

        int result = 0;

        for (int i = 0; i < roman.length(); i++) {
            char ch = roman.charAt(i);

            if (i > 0 && numbersMap.get(ch) > numbersMap.get(roman.charAt(i - 1))) {
                result += numbersMap.get(ch) - 2 * numbersMap.get(roman.charAt(i - 1));
            } else
                result += numbersMap.get(ch);
        }

        return result;
    }

    private static final NavigableMap<Integer, String> units;

    static {
        NavigableMap<Integer, String> initMap = new TreeMap<Integer, String>();
        initMap.put(100, "C");
        initMap.put(90, "XC");
        initMap.put(50, "L");
        initMap.put(40, "XL");
        initMap.put(10, "X");
        initMap.put(9, "IX");
        initMap.put(5, "V");
        initMap.put(4, "IV");
        initMap.put(1, "I");
        units = Collections.unmodifiableNavigableMap(initMap);
    }
}
