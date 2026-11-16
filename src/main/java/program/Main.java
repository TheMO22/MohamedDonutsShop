package program;

import core.DB;
import entity.Donuts;
import entity.DonutsDAO;
import entity.ItemOrder;
import entity.ItemOrderDAO;
import entity.Transaction;
import entity.TransactionDAO;
import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

public class Main
{
    private static final double TAX_RATE = 0.06;

    private final JFrame frame = new JFrame("Oak Donuts");

    private final DonutsDAO donutsDAO = new DonutsDAO();
    private final TransactionDAO transactionDAO = new TransactionDAO();
    private final ItemOrderDAO itemOrderDAO = new ItemOrderDAO();

    private final DefaultListModel<Donuts> menuModel = new DefaultListModel<>();
    private final JList<Donuts> menuList = new JList<>(menuModel);

    // ITEM_ID is stored in the model but hidden from the visible table.
    private final DefaultTableModel orderModel = new DefaultTableModel(
            new Object[]{"Item", "Options", "Qty", "Price", "Total", "ITEM_ID"}, 0
    );

    private JTable orderTable;

    private final JComboBox<String> categoryCombo =
            new JComboBox<>(new String[]{"All", "Donuts", "Bagels", "Coffee", "Tea"});
    private final JComboBox<String> icingCombo =
            new JComboBox<>(new String[]{"None", "Chocolate", "Strawberry", "Vanilla"});
    private final JComboBox<String> fillingCombo =
            new JComboBox<>(new String[]{"None", "Boston Cream", "Jelly", "Custard"});
    private final JComboBox<String> seasonCombo =
            new JComboBox<>(new String[]{"All Seasons", "Fall", "Winter", "Spring", "Summer"});

    private final JCheckBox sprinklesCheckBox = new JCheckBox("Sprinkles");
    private final JTextField searchField = new JTextField();
    private final JTextField customerNameField = new JTextField();
    private final JSpinner quantitySpinner =
            new JSpinner(new SpinnerNumberModel(1, 1, 99, 1));

    private final JLabel unitPriceLabel = new JLabel("Unit: $0.00");
    private final JLabel subtotalValue = new JLabel("$0.00");
    private final JLabel taxValue = new JLabel("$0.00");
    private final JLabel totalValue = new JLabel("$0.00");

    private List<Donuts> allMenuItems = new ArrayList<>();

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new Main().showWindow());
    }

    private void showWindow()
    {
        try {
            DB.getInstance();
            allMenuItems = donutsDAO.getAll();
        } catch (RuntimeException e) {
            JOptionPane.showMessageDialog(
                    null,
                    "Could not load the menu from Derby:\n" + e.getMessage(),
                    "Database Error",
                    JOptionPane.ERROR_MESSAGE
            );
            return;
        }

        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setMinimumSize(new Dimension(1000, 620));
        frame.setContentPane(buildInterface());
        frame.setSize(1200, 720);
        frame.setLocationRelativeTo(null);

        refreshMenu();
        frame.setVisible(true);
    }

    private JPanel buildInterface()
    {
        JPanel root = new JPanel(new BorderLayout(12, 10));
        root.setBorder(BorderFactory.createEmptyBorder(12, 14, 12, 14));

        JLabel title = new JLabel("Oak Donuts");
        title.setFont(new Font("SansSerif", Font.BOLD, 26));
        root.add(title, BorderLayout.NORTH);

        JPanel content = new JPanel(new BorderLayout(14, 0));
        content.add(buildOptionsPanel(), BorderLayout.WEST);
        content.add(buildMenuPanel(), BorderLayout.CENTER);
        content.add(buildOrderPanel(), BorderLayout.EAST);

        root.add(content, BorderLayout.CENTER);
        return root;
    }

    private JPanel buildOptionsPanel()
    {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setPreferredSize(new Dimension(190, 0));
        panel.setBorder(BorderFactory.createEmptyBorder(4, 0, 4, 8));

        addSectionTitle(panel, "Filters");
        addLabeledComponent(panel, "Category:", categoryCombo);

        searchField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 28));
        addLabeledComponent(panel, "Search:", searchField);

        panel.add(Box.createVerticalStrut(12));
        panel.add(new JSeparator());
        panel.add(Box.createVerticalStrut(12));

        addSectionTitle(panel, "Item Options");
        addLabeledComponent(panel, "Icing:", icingCombo);
        addLabeledComponent(panel, "Filling:", fillingCombo);

        sprinklesCheckBox.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(Box.createVerticalStrut(6));
        panel.add(sprinklesCheckBox);

        addLabeledComponent(panel, "Season:", seasonCombo);
        panel.add(Box.createVerticalGlue());

        categoryCombo.addActionListener(e -> refreshMenu());
        seasonCombo.addActionListener(e -> refreshMenu());

        searchField.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                refreshMenu();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                refreshMenu();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                refreshMenu();
            }
        });

        return panel;
    }

    private JPanel buildMenuPanel()
    {
        JPanel panel = new JPanel(new BorderLayout(8, 8));
        panel.setPreferredSize(new Dimension(330, 0));

        JLabel heading = new JLabel("Menu");
        heading.setFont(new Font("SansSerif", Font.BOLD, 18));
        panel.add(heading, BorderLayout.NORTH);

        menuList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        menuList.setFixedCellHeight(25);

        menuList.setCellRenderer((list, item, index, selected, focus) -> {
            JLabel label = new JLabel(
                    item.getDonutName() + "  —  " + money(item.getPrice())
            );
            label.setOpaque(true);
            label.setBorder(BorderFactory.createEmptyBorder(3, 5, 3, 5));

            if (selected) {
                label.setBackground(list.getSelectionBackground());
                label.setForeground(list.getSelectionForeground());
            } else {
                label.setBackground(list.getBackground());
                label.setForeground(list.getForeground());
            }

            return label;
        });

        panel.add(new JScrollPane(menuList), BorderLayout.CENTER);

        JPanel controls = new JPanel(new FlowLayout(FlowLayout.LEFT, 7, 4));
        controls.add(new JLabel("Qty:"));
        controls.add(quantitySpinner);
        controls.add(unitPriceLabel);

        JButton addButton = new JButton("Add to Order");
        addButton.addActionListener(e -> addSelectedItem());
        controls.add(addButton);

        panel.add(controls, BorderLayout.SOUTH);

        menuList.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                Donuts selected = menuList.getSelectedValue();
                unitPriceLabel.setText(selected == null
                        ? "Unit: $0.00"
                        : "Unit: " + money(selected.getPrice()));
            }
        });

        return panel;
    }

    private JPanel buildOrderPanel()
    {
        JPanel panel = new JPanel(new BorderLayout(8, 8));
        panel.setPreferredSize(new Dimension(560, 0));

        JLabel heading = new JLabel("Order");
        heading.setFont(new Font("SansSerif", Font.BOLD, 18));

        JPanel header = new JPanel(new BorderLayout(6, 6));
        header.add(heading, BorderLayout.NORTH);

        JPanel customerRow = new JPanel(new BorderLayout(8, 0));
        customerRow.add(new JLabel("Customer name:"), BorderLayout.WEST);
        customerRow.add(customerNameField, BorderLayout.CENTER);
        header.add(customerRow, BorderLayout.SOUTH);
        panel.add(header, BorderLayout.NORTH);

        orderTable = new JTable(orderModel);
        orderTable.setRowHeight(24);
        orderTable.setFillsViewportHeight(true);
        orderTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        orderTable.setDefaultEditor(Object.class, null);
        orderTable.removeColumn(orderTable.getColumnModel().getColumn(5));

        panel.add(new JScrollPane(orderTable), BorderLayout.CENTER);

        JPanel bottom = new JPanel(new BorderLayout(8, 8));

        JPanel totals = new JPanel(new GridLayout(3, 2, 8, 4));
        totals.add(new JLabel("Subtotal:"));
        totals.add(rightAligned(subtotalValue));
        totals.add(new JLabel("Tax (6%):"));
        totals.add(rightAligned(taxValue));

        JLabel totalLabel = new JLabel("Total:");
        totalLabel.setFont(totalLabel.getFont().deriveFont(Font.BOLD, 16f));
        totalValue.setFont(totalValue.getFont().deriveFont(Font.BOLD, 16f));
        totals.add(totalLabel);
        totals.add(rightAligned(totalValue));

        bottom.add(totals, BorderLayout.NORTH);

        JButton removeButton = new JButton("Remove Selected Item");
        removeButton.addActionListener(e -> removeSelectedItem());

        JButton clearButton = new JButton("Clear");
        clearButton.addActionListener(e -> clearOrder());

        JButton checkoutButton = new JButton("Checkout");
        checkoutButton.addActionListener(e -> checkout());

        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttons.add(removeButton);
        buttons.add(clearButton);
        buttons.add(checkoutButton);
        bottom.add(buttons, BorderLayout.SOUTH);

        panel.add(bottom, BorderLayout.SOUTH);
        return panel;
    }

    private void refreshMenu()
    {
        String category = (String) categoryCombo.getSelectedItem();
        String season = (String) seasonCombo.getSelectedItem();
        String query = searchField.getText().trim().toLowerCase();

        menuModel.clear();

        for (Donuts item : allMenuItems) {
            boolean categoryMatches = "All".equals(category)
                    || item.getCategory().equalsIgnoreCase(category);

            String itemSeason = item.getSeason();
            boolean seasonMatches = "All Seasons".equals(season)
                    || (itemSeason != null && itemSeason.equalsIgnoreCase(season));

            boolean searchMatches = item.getDonutName().toLowerCase().contains(query)
                    || (item.getDescription() != null
                    && item.getDescription().toLowerCase().contains(query));

            if (item.isAvailable() && categoryMatches && seasonMatches && searchMatches) {
                menuModel.addElement(item);
            }
        }
    }

    private void addSelectedItem()
    {
        Donuts item = menuList.getSelectedValue();

        if (item == null) {
            JOptionPane.showMessageDialog(frame, "Select a menu item first.");
            return;
        }

        int quantity = (Integer) quantitySpinner.getValue();
        List<String> options = new ArrayList<>();

        String icing = (String) icingCombo.getSelectedItem();
        String filling = (String) fillingCombo.getSelectedItem();

        if (icing != null && !icing.equals("None")) {
            options.add("Icing: " + icing);
        }

        if (filling != null && !filling.equals("None")) {
            options.add("Filling: " + filling);
        }

        if (sprinklesCheckBox.isSelected()) {
            options.add("Sprinkles");
        }

        String optionsText = options.isEmpty() ? "-" : String.join(", ", options);
        double lineTotal = item.getPrice() * quantity;

        orderModel.addRow(new Object[]{
                item.getDonutName(),
                optionsText,
                quantity,
                money(item.getPrice()),
                money(lineTotal),
                item.getDonutId()
        });

        updateTotals();
    }

    private void removeSelectedItem()
    {
        int selectedRow = orderTable.getSelectedRow();

        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(frame, "Select an order row to remove.");
            return;
        }

        orderModel.removeRow(orderTable.convertRowIndexToModel(selectedRow));
        updateTotals();
    }

    private void updateTotals()
    {
        double subtotal = 0.0;

        for (int row = 0; row < orderModel.getRowCount(); row++) {
            String lineTotal = orderModel.getValueAt(row, 4)
                    .toString()
                    .replace("$", "");
            subtotal += Double.parseDouble(lineTotal);
        }

        double tax = subtotal * TAX_RATE;
        subtotalValue.setText(money(subtotal));
        taxValue.setText(money(tax));
        totalValue.setText(money(subtotal + tax));
    }

    private void clearOrder()
    {
        orderModel.setRowCount(0);
        customerNameField.setText("");
        updateTotals();
    }

    private void checkout()
    {
        if (orderModel.getRowCount() == 0)
        {
            JOptionPane.showMessageDialog(frame, "Add an item to the order first.");
            return;
        }

        String customerName = customerNameField.getText().trim();
        if (customerName.isEmpty()) {
            JOptionPane.showMessageDialog(frame, "Enter the customer's name.");
            return;
        }

        try {
            int transactionId = nextTransactionId();

            Transaction transaction = new Transaction();
            transaction.setTransactionId(transactionId);
            transaction.setCustomerName(customerName);
            transaction.setOrderDate(new Timestamp(System.currentTimeMillis()));

            // Save the transaction first because ORDER_ITEM references it.
            transactionDAO.insert(transaction);

            for (int row = 0; row < orderModel.getRowCount(); row++) {
                ItemOrder itemOrder = new ItemOrder();
                itemOrder.setOrderItemId(nextOrderItemId());
                itemOrder.setTransactionId(transactionId);
                itemOrder.setDonutId((Integer) orderModel.getValueAt(row, 5));
                itemOrder.setQuantity((Integer) orderModel.getValueAt(row, 2));

                String price = orderModel.getValueAt(row, 3)
                        .toString()
                        .replace("$", "");
                itemOrder.setPriceOfItem(Double.parseDouble(price));

                String options = orderModel.getValueAt(row, 1).toString();
                itemOrder.setIcing(optionValue(options, "Icing: "));
                itemOrder.setFilling(optionValue(options, "Filling: "));
                itemOrder.setSprinkles(options.contains("Sprinkles"));

                itemOrderDAO.insert(itemOrder);
            }

            JOptionPane.showMessageDialog(
                    frame,
                    "Thank you, " + customerName + "!\nOrder #" + transactionId
                            + "\nTotal: " + totalValue.getText()
                            + "\nOrder saved to Derby.",
                    "Order Saved",
                    JOptionPane.INFORMATION_MESSAGE
            );

            clearOrder();
        } catch (RuntimeException e) {
            JOptionPane.showMessageDialog(
                    frame,
                    "Could not save the order:\n" + e.getMessage(),
                    "Database Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    private int nextTransactionId()
    {
        int maxId = 0;
        for (Transaction transaction : transactionDAO.getAll()) {
            maxId = Math.max(maxId, transaction.getTransactionId());
        }
        return maxId + 1;
    }

    private int nextOrderItemId() {
        int maxId = 0;
        for (ItemOrder item : itemOrderDAO.getAll()) {
            maxId = Math.max(maxId, item.getOrderItemId());
        }
        return maxId + 1;
    }

    private String optionValue(String options, String prefix) {
        for (String option : options.split(", ")) {
            if (option.startsWith(prefix)) {
                return option.substring(prefix.length());
            }
        }
        return "None";
    }

    private void addSectionTitle(JPanel panel, String text)
    {
        JLabel label = new JLabel(text);
        label.setFont(label.getFont().deriveFont(Font.BOLD, 16f));
        label.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(label);
        panel.add(Box.createVerticalStrut(7));
    }

    private void addLabeledComponent(JPanel panel, String labelText, JComponent component)
    {
        JLabel label = new JLabel(labelText);
        label.setAlignmentX(Component.LEFT_ALIGNMENT);
        component.setAlignmentX(Component.LEFT_ALIGNMENT);
        component.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));
        panel.add(Box.createVerticalStrut(7));
        panel.add(label);
        panel.add(component);
    }

    private JLabel rightAligned(JLabel label)
    {
        label.setHorizontalAlignment(SwingConstants.RIGHT);
        return label;
    }

    private String money(double amount) {
        return String.format("$%.2f", amount);
    }
}