package ui;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.text.AbstractDocument;
import java.awt.*;
import java.awt.event.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

// ============================================================
// ------------------ AddItemDialog class ---------------------
// ============================================================

public class AddItemDialog extends JDialog {
    private JPanel contentPane;
    private JButton buttonOK;
    private JButton buttonCancel;
    private JTextField itemNameField;
    private JTextField consignorField;
    private JTextField quantityField;
    private JTextField priceField;
    private JSpinner dateReceivedField;
    private JTextField daysToSellField;
    private JRadioButton perishableRadioButton;
    private JRadioButton nonPerishableRadioButton;

    private boolean confirmed = false;

    private final JComponent[] fields;

    private final Border RED_BORDER = BorderFactory.createLineBorder(Color.RED, 1);
    private final Border DEFAULT_BORDER = UIManager.getBorder("TextField.border");

    public AddItemDialog() {
        setContentPane(contentPane);
        setModal(true);
        setResizable(false);
        setLocationRelativeTo(null);
        getRootPane().setDefaultButton(buttonOK);

        ButtonGroup radioButtons = new ButtonGroup();
        radioButtons.add(perishableRadioButton);
        radioButtons.add(nonPerishableRadioButton);
        perishableRadioButton.setSelected(true);

        fields = new JComponent[]{
                itemNameField,
                consignorField,
                quantityField,
                priceField,
                dateReceivedField,
                daysToSellField
        };

                //purpose: adds integer-only and double-only filters to price, quantity, and days to sell fields
        ((AbstractDocument) quantityField.getDocument()).setDocumentFilter(new TextFieldFilter.IntegerFilter());
        ((AbstractDocument) priceField.getDocument()).setDocumentFilter(new TextFieldFilter.DoubleFilter());
        ((AbstractDocument) daysToSellField.getDocument()).setDocumentFilter(new TextFieldFilter.IntegerFilter());

        //purpose: removes red border color when field gets a key press
        KeyAdapter resetBorder = new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent e) {
                ((JTextField) e.getSource()).setBorder(DEFAULT_BORDER);
            }
        };

        for (JComponent f : fields) {
            f.addKeyListener(resetBorder);
        }

        buttonOK.addActionListener(e -> onOK());
        buttonCancel.addActionListener(e -> onCancel());

        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                onCancel();
            }
        });
    }


    // ============================================================
    // -------------------- Getter functions ----------------------
    // ============================================================

    //purpose: used in InventoryPanel to detect if user has pressed OK.
    public boolean isConfirmed() {
        return confirmed;
    }

    //purpose: returns an arraylist of all the input in the fields
    public ArrayList<Object> getAllFieldInput() {
        ArrayList<Object> output = new ArrayList<>();

        output.add(itemNameField.getText());
        output.add(consignorField.getText());
        output.add(quantityField.getText());
        output.add(priceField.getText());

        Date utilDate = (Date) dateReceivedField.getValue();
        LocalDate localDate = utilDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        output.add(localDate.toString());

        output.add(daysToSellField.getText());
        output.add(perishableRadioButton.isSelected());

        return output;
    }

    //purpose: returns an array of all the text field objects that the user left blank
    private boolean isFilled() {
        for (JComponent f : fields) {
            if (f instanceof JTextField){
                if(((JTextField) f).getText().isEmpty()){
                    return false;
                }
            }
        }

        return true;
    }


    // ============================================================
    // -------------------- Helper functions ----------------------
    // ============================================================

    private void resetFieldBorders() {
        for (JComponent f : fields) {
            f.setBorder(DEFAULT_BORDER);
        }
    }

    //purpose: returns a string format (MM-dd-yyyy) on the date n-days ahead of today
    private static String getDatePlusDays(int days) {
        LocalDate futureDate = LocalDate.now().plusDays(days);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM-dd-yyyy");
        return futureDate.format(formatter);
    }



    // ============================================================
    // -------------------- Button functions ----------------------
    // ============================================================

    private void onOK() {
        boolean ifAllFilled = isFilled();
       //purpose: if all fields are filled, continue with ok button (closes the dialog)
        if (ifAllFilled) {
            confirmed = true;
            dispose();
        }
        //purpose: if not all fields are filled, put red border on unfilled fields and show message dialog
        else {
            resetFieldBorders();
            for (JComponent f : fields) { f.setBorder(RED_BORDER); }

            JOptionPane.showMessageDialog(this,
                    "Please fill in all required fields.",
                    "Missing Information",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void onCancel() {
        dispose();
    }

    private void createUIComponents() {
        // TODO: place custom component creation code here
        SpinnerDateModel model = new SpinnerDateModel();
        dateReceivedField = new JSpinner(model);
        JSpinner.DateEditor editor = new JSpinner.DateEditor(dateReceivedField, "yyyy-MM-dd");
        dateReceivedField.setEditor(editor);
        dateReceivedField.setValue(new Date());
    }

//    public static void main(String[] args) {
//        AddItemDialog dialog = new AddItemDialog();
//        dialog.pack();
//        dialog.setVisible(true);
//        System.exit(0);
//    }
}
