import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.text.AbstractDocument;
import java.awt.*;
import java.awt.event.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
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
    private JTextField dateReceivedField;
    private JTextField daysToSellField;
    private JRadioButton perishableRadioButton;
    private JRadioButton nonPerishableRadioButton;

    private boolean confirmed = false;

    private final JTextField[] fields;

    private final Border RED_BORDER = BorderFactory.createLineBorder(Color.RED, 1);
    private final Border DEFAULT_BORDER = UIManager.getBorder("TextField.border");

    public AddItemDialog() {
        setContentPane(contentPane);
        setModal(true);
        setResizable(false);
        setLocationRelativeTo(null);
        getRootPane().setDefaultButton(buttonOK);

        fields = new JTextField[]{
                itemNameField,
                consignorField,
                quantityField,
                priceField,
                dateReceivedField,
                daysToSellField
        };

        ButtonGroup radioButtons = new ButtonGroup();
        radioButtons.add(perishableRadioButton);
        radioButtons.add(nonPerishableRadioButton);
        perishableRadioButton.setSelected(true);

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
        for (JTextField f : fields) {
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
        for(int i = 0; i < fields.length; i++) {
            JTextField f = fields[i];
            String s = f.getText();

            if(i == 0 || i == 1) { //purpose: 0 = Item name | 1 = Consignor
                //purpose: capitalizes first letter of a string
                s = s.substring(0,1).toUpperCase() + s.substring(1);
                output.add(s);
            }
            else if(i == 2) { //purpose: 2 = Quantity (integer)
                output.add(Integer.parseInt(s));
            }
            else if(i == 3) { //purpose: 3 = Price (double)
                output.add(Double.parseDouble(s));
            }
            else if(i == 5) { //purpose: 5 = Days to Sell (integer)
                String returnDate = getDatePlusDays(Integer.parseInt(s));
                output.add(returnDate);
            }
            else {
                output.add(s);
            }
        }
        if(perishableRadioButton.isSelected()) output.add("Perishable");
        else output.add("Non-Perishable");
        return output;
    }

    //purpose: returns an array of all the text field objects that the user left blank
    private JTextField[] getBlankFields() {
        List<JTextField> blanks = new ArrayList<>();
        for (JTextField f : fields) {
            if (f.getText().isBlank()) blanks.add(f);
        }
        return blanks.toArray(new JTextField[0]);
    }


    // ============================================================
    // -------------------- Helper functions ----------------------
    // ============================================================

    private void resetFieldBorders() {
        for (JTextField f : fields) {
            f.setBorder(DEFAULT_BORDER);
        }
    }

    //purpose: returns a string format (MM/dd/yyyy) on the date n-days ahead of today
    private static String getDatePlusDays(int days) {
        LocalDate futureDate = LocalDate.now().plusDays(days);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/dd/yyyy");
        return futureDate.format(formatter);
    }



    // ============================================================
    // -------------------- Button functions ----------------------
    // ============================================================

    private void onOK() {
        JTextField[] blankFields = getBlankFields();

        //purpose: if all fields are filled, continue with ok button (closes the dialog)
        if (blankFields.length == 0) {
            confirmed = true;
            dispose();
        }
        //purpose: if not all fields are filled, put red border on unfilled fields and show message dialog
        else {
            resetFieldBorders();
            for (JTextField f : blankFields) { f.setBorder(RED_BORDER); }

            JOptionPane.showMessageDialog(this,
                    "Please fill in all required fields.",
                    "Missing Information",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void onCancel() {
        dispose();
    }

//    public static void main(String[] args) {
//        AddItemDialog dialog = new AddItemDialog();
//        dialog.pack();
//        dialog.setVisible(true);
//        System.exit(0);
//    }
}
