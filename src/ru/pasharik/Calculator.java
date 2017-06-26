package ru.pasharik;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;
import java.util.regex.Pattern;

import static java.util.Arrays.asList;

/**
 * Created by pasharik on 21/06/17.
 */
public class Calculator {
    private static final Pattern numberPattern = Pattern.compile("\\d+");
    public static void main(String[] args) {
        System.out.println(calculate("1 + 2 * 3 - 4 * 5"));
    }

    private static int calculate(String str) {
        String[] arr = str.split(" ");
        List<String> out = new ArrayList<>();
        Stack<String> stOp = new Stack<>();

        for (String s : arr) {
            if (isNumber(s)) { out.add(s); }
            else {
                if (!stOp.isEmpty() && !isGreaterPriority(s, stOp.peek())) {
                    out.add(stOp.pop());
                }
                stOp.push(s);
            }
        }
        while (!stOp.isEmpty()) {
            out.add(stOp.pop());
        }

        Stack<Integer> res = new Stack<>();
        for (String s : out) {
            if (isNumber(s)) { res.push(Integer.parseInt(s)); }
            else { doCalc(res, s); }
        }
        return res.pop();
    }

    private static void doCalc(Stack<Integer> stack, String op) {
        Integer val2 = stack.pop();
        Integer val1 = stack.pop();
        switch (op) {
            case "+": stack.push(val1 + val2); break;
            case "-": stack.push(val1 - val2); break;
            case "*": stack.push(val1 * val2);
        }
    }

    private static boolean isNumber(String s) {
        return numberPattern.matcher(s).matches();
    }

    private static boolean isGreaterPriority(String op1, String op2) {
        return (asList("*", "/").contains(op1) && asList("+", "-").contains(op2));
    }
}
