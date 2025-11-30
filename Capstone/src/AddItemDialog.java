import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.text.AbstractDocument;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;

public class AddItemDialog extends JDialog {
    private JPanel contentPane;
    private JButton buttonOK;
    private JButton buttonCancel;
    private JTextField itemNameField;
    private JTextField consignorField;
    private JTextField quantityField;
    private JTextField priceField;
    private JTextField dateRecievedField;
    private JTextField returnDateField;
    private JRadioButton perishableRadioButton;
    private JRadioButton nonPerishableRadioButton;

    private boolean confirmed = false;

    private JTextField[] fields;

    private final Border RED_BORDER = BorderFactory.createLineBorder(Color.RED, 1);
    private final Border DEFAULT_BORDER = UIManager.getBorder("TextField.border");

    public AddItemDialog() {
        setContentPane(contentPane);
        setModal(true);
        setResizable(false);
        getRootPane().setDefaultButton(buttonOK);

        fields = new JTextField[]{
                itemNameField,
                consignorField,
                quantityField,
                priceField,
                dateRecievedField,
                returnDateField
        };

        ButtonGroup radioButtons = new ButtonGroup();
        radioButtons.add(perishableRadioButton);
        radioButtons.add(nonPerishableRadioButton);
        perishableRadioButton.setSelected(true);

        //purpose: adds integer-only and double-only filters to price and quantity fields respectively
        ((AbstractDocument) quantityField.getDocument()).setDocumentFilter(new TextFieldFilter.IntegerFilter());
        ((AbstractDocument) priceField.getDocument()).setDocumentFilter(new TextFieldFilter.DoubleFilter());

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

    public boolean isConfirmed() {
        return confirmed;
    }

    public ArrayList<Object> getAllFieldInput() {
        ArrayList<Object> output = new ArrayList<Object>();
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
            else {
                output.add(s);
            }
        }
        return output;
    }

    // ============================================================
    // -------------------- Helper functions ----------------------
    // ============================================================


    private JTextField[] getBlankFields() {
        List<JTextField> blanks = new ArrayList<JTextField>();
        for (JTextField f : fields) {
            if (f.getText().isBlank()) blanks.add(f);
        }
        return blanks.toArray(new JTextField[0]);
    }

    private void resetFieldBorders() {
        for (JTextField f : fields) {
            f.setBorder(DEFAULT_BORDER);
        }
    }

    private void onOK() {
        JTextField[] blankFields = getBlankFields();

        //purpose: if all fields are filled, continue with ok button (closes the dialog)
        if (blankFields.length == 0) { confirmed = true; dispose(); return; }

        //purpose: if not all fields are filled, put red border on unfilled fields and show message dialog
        resetFieldBorders();
        for (JTextField f : blankFields) { f.setBorder(RED_BORDER); }

        JOptionPane.showMessageDialog(this,
                "Please fill in all required fields.",
                "Missing Information",
                JOptionPane.ERROR_MESSAGE);
    }

    private void onCancel() {
        dispose();
    }

    public static void main(String[] args) {
        AddItemDialog dialog = new AddItemDialog();
        dialog.pack();
        dialog.setVisible(true);
        System.exit(0);
    }
}
