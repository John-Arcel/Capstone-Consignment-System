import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;

public class TextFieldFilter {
    //purpose: only allows integer values in text field
    public static class IntegerFilter extends DocumentFilter {
        @Override
        public void insertString(FilterBypass fb, int offset, String string, AttributeSet attr)
                throws BadLocationException {
            if (string.matches("\\d+")) { //purpose: (regex) empty allowed for deleting
                super.insertString(fb, offset, string, attr);
            }
        }

        @Override
        public void replace(FilterBypass fb, int offset, int length, String text, AttributeSet attrs)
                throws BadLocationException {
            if (text.matches("\\d*")) { //purpose: (regex) empty allowed for deleting
                super.replace(fb, offset, length, text, attrs);
            }
        }
    }

    //purpose: only allows double values in text field
    public static class DoubleFilter extends DocumentFilter {
        @Override
        public void replace(FilterBypass fb, int offset, int length, String text, AttributeSet attrs)
                throws BadLocationException {

            String oldText = fb.getDocument().getText(0, fb.getDocument().getLength());
            String newText = oldText.substring(0, offset) + text + oldText.substring(offset + length);

            if (newText.matches("\\d*(\\.\\d*)?")) {  //purpose: (regex) digits, optional dot, more digits
                super.replace(fb, offset, length, text, attrs);
            }
        }

        @Override
        public void insertString(FilterBypass fb, int offset, String string, AttributeSet attr)
                throws BadLocationException {

            String oldText = fb.getDocument().getText(0, fb.getDocument().getLength());
            String newText = oldText.substring(0, offset) + string + oldText.substring(offset);

            if (newText.matches("\\d*(\\.\\d*)?")) { //purpose: (regex) digits, optional dot, more digits
                super.insertString(fb, offset, string, attr);
            }
        }
    }


}
