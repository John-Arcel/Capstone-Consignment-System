import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;

public class DashboardPanel extends JPanel {

    private JPanel contentPane;

    private JLabel totalSales;
    private JLabel earnings;
    private JLabel itemSold;
    private JLabel pendingPayout;

    private JTextField itemIDField;

    private JTable itemsDue;
    private JTable transactions;
    private JTable itemDetail;

    private JButton sellItemButton;
    private JButton findItemButton;

    public DashboardPanel() {

        setLayout(new BorderLayout());
        setBackground(Color.WHITE);

        // ---------------- LEFT SIDE (Dashboard Metrics) ----------------
        JPanel leftPanel = new JPanel();
        leftPanel.setLayout(new BoxLayout(leftPanel, BoxLayout.Y_AXIS));
        leftPanel.setBackground(Color.WHITE);
        leftPanel.setBorder(new EmptyBorder(30, 30, 30, 30));

        JLabel title = new JLabel("Dashboard");
        title.setFont(new Font("Segoe UI", Font.BOLD, 28));

        totalSales = new JLabel("Today's Total Sales");
        earnings = new JLabel("Commission");
        itemSold = new JLabel("Sold Count");
        pendingPayout = new JLabel("Pending Payout");

        leftPanel.add(title);
        leftPanel.add(Box.createRigidArea(new Dimension(0, 25)));

        leftPanel.add(makeBlueCard(totalSales));
        leftPanel.add(Box.createRigidArea(new Dimension(0, 15)));

        leftPanel.add(makeBlueCard(earnings));
        leftPanel.add(Box.createRigidArea(new Dimension(0, 15)));

        leftPanel.add(makeBlueCard(itemSold));
        leftPanel.add(Box.createRigidArea(new Dimension(0, 15)));

        leftPanel.add(makeBlueCard(pendingPayout));

        // ---------------- CENTER AREA (Expiry + Recent Transactions) ----------------
        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));
        centerPanel.setBackground(Color.WHITE);
        centerPanel.setBorder(new EmptyBorder(30, 20, 30, 20));

        JLabel expiryLabel = new JLabel("Expiry");
        expiryLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));

        itemsDue = new JTable();
        JScrollPane expiryScroll = new JScrollPane(itemsDue);
        expiryScroll.setPreferredSize(new Dimension(500, 180));
        expiryScroll.setBorder(new LineBorder(Color.BLACK, 1));

        JLabel recentLabel = new JLabel("Recent Transactions");
        recentLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));

        transactions = new JTable();
        JScrollPane recentScroll = new JScrollPane(transactions);
        recentScroll.setPreferredSize(new Dimension(500, 180));
        recentScroll.setBorder(new LineBorder(Color.BLACK, 1));

        centerPanel.add(expiryLabel);
        centerPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        centerPanel.add(expiryScroll);

        centerPanel.add(Box.createRigidArea(new Dimension(0, 30)));

        centerPanel.add(recentLabel);
        centerPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        centerPanel.add(recentScroll);

        // ---------------- RIGHT SIDE (Quick Sale) ----------------
        JPanel rightPanel = new JPanel();
        rightPanel.setBackground(new Color(34, 39, 47));
        rightPanel.setPreferredSize(new Dimension(350, 0));
        rightPanel.setLayout(new BoxLayout(rightPanel, BoxLayout.Y_AXIS));
        rightPanel.setBorder(new EmptyBorder(40, 30, 40, 30));

        JLabel quickSaleLabel = new JLabel("Quick Sale");
        quickSaleLabel.setFont(new Font("Segoe UI", Font.BOLD, 28));
        quickSaleLabel.setForeground(Color.WHITE);
        quickSaleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel idLabel = new JLabel("Enter Item ID:");
        idLabel.setForeground(Color.WHITE);

        // Rounded Input Field (simulate)
        itemIDField = new JTextField(15);
        itemIDField.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(new Color(122, 88, 255), 3, true),
                new EmptyBorder(5, 10, 5, 10)
        ));
        itemIDField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));

        findItemButton = new JButton("Find Item");
        findItemButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        findItemButton.setBackground(new Color(0, 122, 255));
        findItemButton.setForeground(Color.WHITE);
        findItemButton.setFocusPainted(false);

        JLabel detailsLabel = new JLabel("Item Details:");
        detailsLabel.setForeground(Color.WHITE);

        itemDetail = new JTable();
        JScrollPane detailScroll = new JScrollPane(itemDetail);
        detailScroll.setPreferredSize(new Dimension(250, 150));
        detailScroll.setBorder(new LineBorder(Color.WHITE, 1));

        sellItemButton = new JButton("Sell Item");
        sellItemButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        sellItemButton.setBackground(new Color(0, 122, 255));
        sellItemButton.setForeground(Color.WHITE);
        sellItemButton.setFocusPainted(false);

        rightPanel.add(quickSaleLabel);
        rightPanel.add(Box.createRigidArea(new Dimension(0, 30)));
        rightPanel.add(idLabel);
        rightPanel.add(itemIDField);
        rightPanel.add(Box.createRigidArea(new Dimension(0, 15)));
        rightPanel.add(findItemButton);
        rightPanel.add(Box.createRigidArea(new Dimension(0, 25)));
        rightPanel.add(detailsLabel);
        rightPanel.add(detailScroll);
        rightPanel.add(Box.createRigidArea(new Dimension(0, 30)));
        rightPanel.add(sellItemButton);

        // ---------------- ADD ALL TO MAIN PANEL ----------------
        add(leftPanel, BorderLayout.WEST);
        add(centerPanel, BorderLayout.CENTER);
        add(rightPanel, BorderLayout.EAST);
    }

    // ---------------- BLUE CARD BUILDER ----------------
    private JPanel makeBlueCard(JLabel label) {
        JPanel card = new JPanel();
        card.setBackground(new Color(25, 125, 255));
        card.setLayout(new BorderLayout());
        card.setBorder(new EmptyBorder(25, 20, 25, 20));
        card.setMaximumSize(new Dimension(220, 100));

        label.setForeground(Color.WHITE);
        label.setFont(new Font("Segoe UI", Font.PLAIN, 16));

        card.add(label, BorderLayout.CENTER);
        return card;
    }

    // ---------------- TEST MAIN ----------------
    public static void main(String[] args) {
        JFrame frame = new JFrame("Dashboard");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(1400, 800);
        frame.setContentPane(new DashboardPanel());
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
}
