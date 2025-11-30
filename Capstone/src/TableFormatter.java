import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.text.DecimalFormat;

public class TableFormatter {

    //purpose: adds $ sign and displays 2 decimal places to doubles (price column) in table
    static class DollarDecimalRenderer extends DefaultTableCellRenderer {
        private DecimalFormat formatter;
        private int top, left, bottom, right;

        public DollarDecimalRenderer(int top, int left, int bottom, int right) {
            this.formatter = new DecimalFormat("$#0.00"); // $ and 2 decimals
            this.top = top;
            this.left = left;
            this.bottom = bottom;
            this.right = right;
            setHorizontalAlignment(RIGHT); // align numbers to the right
            setOpaque(true);
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                                                       boolean isSelected, boolean hasFocus,
                                                       int row, int column) {
            Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            if (c instanceof JLabel) {
                ((JLabel) c).setBorder(BorderFactory.createEmptyBorder(top, left, bottom, right));
                if (value instanceof Number) {
                    setText(formatter.format(value));
                }
            }
            return c;
        }
    }

    //purpose: center aligns integers (quantity column) in table
    static class IntegerRenderer extends DefaultTableCellRenderer {
        private int top, left, bottom, right;

        public IntegerRenderer(int top, int left, int bottom, int right) {
            this.top = top;
            this.left = left;
            this.bottom = bottom;
            this.right = right;
            setHorizontalAlignment(CENTER);
            setOpaque(true);
        }

        @Override
        public Component getTableCellRendererComponent(JTable table,
                                                       Object value,
                                                       boolean isSelected,
                                                       boolean hasFocus,
                                                       int row,
                                                       int column) {
            Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            if (c instanceof JLabel) {
                ((JLabel) c).setBorder(BorderFactory.createEmptyBorder(top, left, bottom, right));
                if (value instanceof Number) {
                    setText(String.valueOf(((Number) value).intValue()));
                }
            }
            return c;
        }
    }

    //purpose: adds cell padding to table cells
    static class PaddedCellRenderer extends DefaultTableCellRenderer {
        private int top, left, bottom, right;

        public PaddedCellRenderer(int top, int left, int bottom, int right) {
            this.top = top;
            this.left = left;
            this.bottom = bottom;
            this.right = right;
            setOpaque(true); // important so background is visible
        }

        @Override
        public Component getTableCellRendererComponent(JTable table,
                                                       Object value,
                                                       boolean isSelected,
                                                       boolean hasFocus,
                                                       int row,
                                                       int column) {
            Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            if (c instanceof JLabel) {
                ((JLabel) c).setBorder(BorderFactory.createEmptyBorder(top, left, bottom, right));
            }
            return c;
        }
    }

}
