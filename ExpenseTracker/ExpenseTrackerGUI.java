import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class ExpenseTrackerGUI {
    private static final String FILE_NAME = "expenses.dat";
    private List<Expense> expenses = new ArrayList<>();
    private DefaultTableModel tableModel;
    private JTextField amountField, categoryField, dateField, descField;
    private JLabel totalLabel;

    public ExpenseTrackerGUI() {
        loadExpenses();

        JFrame frame = new JFrame("Expense Tracker");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(600, 400);
        frame.setLayout(new BorderLayout());

        // Top Panel - Input Fields
        JPanel inputPanel = new JPanel(new GridLayout(5, 2, 5, 5));
        inputPanel.setBorder(BorderFactory.createTitledBorder("Add Expense"));

        inputPanel.add(new JLabel("Amount:"));
        amountField = new JTextField();
        inputPanel.add(amountField);

        inputPanel.add(new JLabel("Category:"));
        categoryField = new JTextField();
        inputPanel.add(categoryField);

        inputPanel.add(new JLabel("Date (YYYY-MM-DD):"));
        dateField = new JTextField();
        inputPanel.add(dateField);

        inputPanel.add(new JLabel("Description:"));
        descField = new JTextField();
        inputPanel.add(descField);

        JButton addButton = new JButton("Add Expense");
        addButton.addActionListener(this::addExpense);
        inputPanel.add(addButton);

        frame.add(inputPanel, BorderLayout.NORTH);

        // Center Panel - Table
        String[] columns = {"Date", "Category", "Amount", "Description"};
        tableModel = new DefaultTableModel(columns, 0);
        JTable table = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(table);
        frame.add(scrollPane, BorderLayout.CENTER);

        // Bottom Panel - Total & Buttons
        JPanel bottomPanel = new JPanel(new FlowLayout());
        totalLabel = new JLabel("Total Expenses: $0.00");
        bottomPanel.add(totalLabel);

        JButton calculateTotalButton = new JButton("Calculate Total");
        calculateTotalButton.addActionListener(e -> calculateTotal());
        bottomPanel.add(calculateTotalButton);

        JButton saveButton = new JButton("Save Expenses");
        saveButton.addActionListener(e -> saveExpenses());
        bottomPanel.add(saveButton);

        frame.add(bottomPanel, BorderLayout.SOUTH);

        frame.setVisible(true);
    }

    private void addExpense(ActionEvent e) {
        try {
            double amount = Double.parseDouble(amountField.getText());
            String category = categoryField.getText();
            String date = dateField.getText();
            String description = descField.getText();

            Expense expense = new Expense(amount, category, date, description);
            expenses.add(expense);

            tableModel.addRow(new Object[]{date, category, "$" + amount, description});

            // Clear input fields
            amountField.setText("");
            categoryField.setText("");
            dateField.setText("");
            descField.setText("");

        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(null, "Please enter a valid amount!", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void calculateTotal() {
        double total = expenses.stream().mapToDouble(Expense::getAmount).sum();
        totalLabel.setText("Total Expenses: $" + total);
    }

    private void saveExpenses() {
        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(FILE_NAME))) {
            out.writeObject(expenses);
            JOptionPane.showMessageDialog(null, "Expenses saved successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Error saving expenses.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    @SuppressWarnings("unchecked")
    private void loadExpenses() {
        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(FILE_NAME))) {
            expenses = (List<Expense>) in.readObject();
        } catch (IOException | ClassNotFoundException e) {
            expenses = new ArrayList<>();
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(ExpenseTrackerGUI::new);
    }
}
