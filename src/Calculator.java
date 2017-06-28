import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;
import java.util.regex.Pattern;

import static java.util.Arrays.asList;

/**
 * Created by pasharik on 26/06/17.
 */
public class Calculator extends JPanel implements ActionListener {

    private static final Pattern numberPattern = Pattern.compile("[-]*[\\d\\.]+");
    String[] a = new String[]{"1", "2", "3", "+", "4", "5", "6", "-", "7", "8", "9", "*", "0", "C", "=", "/"};
    JPanel[] panels = new JPanel[5];
    protected JTextField txt;
    Dimension buttonDimension = new Dimension(50, 30);
    Font font = new Font("Times new Roman", Font.BOLD, 14);
    protected boolean isNew = true;
    protected boolean isCalculated = false;

    public Calculator() {
        txt = new JTextField("0");
        txt.setFont(font);
        txt.setPreferredSize(new Dimension(100, 30));
        txt.setEditable(false);
        txt.setHorizontalAlignment(SwingConstants.RIGHT);
        add(txt);
        for (int i = 0; i < 4; i++) {
            panels[i] = new JPanel();
            add(panels[i]);
        }
        for (int i = 0; i < a.length; i++) b(a[i], panels[i / 4]);
    }

    private JButton b(String text, JPanel line) {
        JButton b = new JButton(text);
        line.add(b);
        b.setPreferredSize(buttonDimension);
        b.addActionListener(this);
        return b;
    }

    private static void createAndShowGUI() {
        JFrame frame = new JFrame("Calculator");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        Calculator w = new Calculator();
        w.setLayout(new BoxLayout(w, BoxLayout.Y_AXIS));
        frame.setContentPane(w);
        frame.setResizable(false);
        try { UIManager.setLookAndFeel( "javax.swing.plaf.nimbus.NimbusLookAndFeel"); } catch (Exception e) { }

        frame.pack();
        frame.setVisible(true);
    }


    public static void main(String[] args) {
        javax.swing.SwingUtilities.invokeLater(Calculator::createAndShowGUI);
    }

    private void trimLastOperator() {
        String s = txt.getText().trim();
        if (s.isEmpty()) return;
        String lastSymbol = s.substring(s.length() - 1);
        if (!isNumber(lastSymbol)) {
            txt.setText(s.substring(0, s.length() - 2).trim());
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        JButton b = (JButton) e.getSource();
        String s = b.getText();
        if (isNumber(s)) {
            if (isCalculated) {
                txt.setText("");
            }
            if (isNew) {
                txt.setText("");
                isNew = false;
            }
            txt.setText(txt.getText() + s);
            isCalculated = false;
        } else if ("=".equals(s)) {
            trimLastOperator();
            float res = calculate(txt.getText());
            if (Float.isInfinite(res)) isNew = true;
            txt.setText("" + res);
            isCalculated = true;
        } else if ("C".equals(s)) {
            isNew = true;
            txt.setText("0");
            isCalculated = false;
        } else {
            if (isNew) return;
            trimLastOperator();
            txt.setText(txt.getText() + " " + s + " ");
            isCalculated = false;
        }
    }

    //Magic starts here
    public static float calculate(String str) {
        str = str.replaceAll("- ", "+ -"); //replace "1 - 2" with "1 + -2"
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

        Stack<Float> res = new Stack<>();
        for (String s : out) {
            if (isNumber(s)) { res.push(Float.parseFloat(s)); }
            else { doCalc(res, s); }
        }
        return res.pop();
    }

    private static void doCalc(Stack<Float> stack, String op) {
        Float val2 = stack.pop();
        Float val1 = stack.pop();
        switch (op) {
            case "+": stack.push(val1 + val2); break;
            case "-": stack.push(val1 - val2); break;
            case "*": stack.push(val1 * val2); break;
            case "/": stack.push(val1 / val2); break;
        }
    }

    public static boolean isNumber(String s) {
        return numberPattern.matcher(s).matches();
    }

    private static boolean isGreaterPriority(String op1, String op2) {
        return (asList("*", "/").contains(op1) && asList("+", "-").contains(op2));
    }
}
