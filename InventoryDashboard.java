import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class InventoryDashboard {
    private static JLabel totalSoldLabel;
    private static double totalSoldAmount = 0.0;
    private static String loggedInUser;
    private static JTable allItemsTable;
    private static DefaultTableModel tableModel;

    public static void showInventoryDashboard(String username) {
        loggedInUser = username;

        // Setup the frame with improved size and background color
        JFrame frame = new JFrame("Inventory Management Dashboard");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(590, 500);
        frame.getContentPane().setBackground(new Color(157, 194, 213)); // Light background

        // Top panel to display username
        JPanel topPanel = new JPanel();
        topPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
        topPanel.setBackground(new Color(87, 166, 234)); // Deep sky blue background

        JLabel usernameLabel = new JLabel("Logged in as: " + username);
        usernameLabel.setFont(new Font("Arial", Font.BOLD, 16));
        usernameLabel.setForeground(Color.WHITE);

        topPanel.add(usernameLabel);

        // Center panel for action buttons
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 20, 20));
        buttonPanel.setBackground(new Color(191, 237, 230)); // Light gray background

        JButton addItemButton = new JButton("Add Item");
        JButton sellItemButton = new JButton("Sell Item");
        totalSoldLabel = new JLabel("Total Sold Amount: ₹" + totalSoldAmount);  // Updated label

        // Button styles
        styleButton(addItemButton);
        styleButton(sellItemButton);
        totalSoldLabel.setFont(new Font("Arial", Font.BOLD, 16));
        totalSoldLabel.setForeground(new Color(50, 50, 50));  // Darker text color

        // Add action listeners
        addItemButton.addActionListener(e -> AddItem.showAddItemDialog());
        sellItemButton.addActionListener(e -> SellItem.showSellItemDialog());

        // Add components to buttonPanel
        buttonPanel.add(addItemButton);
        buttonPanel.add(sellItemButton);
        buttonPanel.add(totalSoldLabel);

        JLabel viewInventoryLabel = new JLabel("View Inventory Products", JLabel.CENTER);
        viewInventoryLabel.setFont(new Font("Arial", Font.BOLD, 17));
        viewInventoryLabel.setForeground(new Color(207, 35, 35)); // Darker label text
        viewInventoryLabel.setBorder(BorderFactory.createEmptyBorder(5, 0, 5, 0));
        viewInventoryLabel.setBackground(new Color(218, 204, 204));
        viewInventoryLabel.setOpaque(true);

        // Combo box for filtering products
        String[] filterOptions = { "All", "Product ID", "Product Name","Product Type", "Limited in Stock (<10)", "Out of Stock", "Price" };
        JComboBox<String> filterComboBox = new JComboBox<>(filterOptions);
        filterComboBox.setPreferredSize(new Dimension(130, 30));

        JTextField searchBar = new JTextField(12);  // Adjusted search bar width
        searchBar.setBackground(Color.BLACK);
        searchBar.setForeground(Color.GREEN);
        searchBar.setCaretColor(Color.WHITE);

        JButton searchButton = new JButton("Search");
        searchButton.setBackground(new Color(51, 153, 255));
        searchButton.setForeground(Color.WHITE);  // White text
        searchButton.setFocusPainted(false);  // Remove focus border
        searchButton.setFont(new Font("Arial", Font.BOLD, 12));
        searchButton.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                searchButton.setBackground(new Color(47, 75, 184)); // Darker blue on hover
            }
            public void mouseExited(MouseEvent e) {
                searchButton.setBackground(new Color(51, 153, 255)); // Back to normal color
            }
        });

        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        filterPanel.setBackground(new Color(60, 63, 65));
        JLabel filterLabel = new JLabel("Search by:");
        filterLabel.setFont(new Font("Arial", Font.BOLD, 14));
        filterLabel.setForeground(new Color(255, 255, 255));

        JLabel searchByNameLabel = new JLabel("Search by name:");
        searchByNameLabel.setFont(new Font("Arial", Font.BOLD, 14));
        searchByNameLabel.setForeground(new Color(255, 255, 255));

        filterPanel.add(filterLabel);
        filterPanel.add(filterComboBox);
        filterPanel.add(searchByNameLabel);
        filterPanel.add(searchBar);
        filterPanel.add(searchButton);

        searchButton.addActionListener(e -> {
            String searchQuery = searchBar.getText();
            if (searchQuery.isEmpty()) {
                JOptionPane.showMessageDialog(null, "Please enter a product name to search.", "Error", JOptionPane.ERROR_MESSAGE);
            } else {
                updateTable("SELECT * FROM products WHERE product_name LIKE '%" + searchQuery + "%'");
            }
        });

        filterComboBox.addActionListener(e -> {
            String selectedFilter = filterComboBox.getSelectedItem().toString();
            String query = "SELECT * FROM products";
            switch (selectedFilter) {
                case "Product ID":
                    query = "SELECT * FROM products ORDER BY product_id";
                    break;
                case "Product Name":
                    query = "SELECT * FROM products ORDER BY product_name";
                    break;
                case "Product Type":
                    query = "SELECT * FROM products ORDER BY product_type";
                    break;
                case "Limited in Stock (<10)":
                    query = "SELECT * FROM products WHERE product_quantity < 10";
                    break;
                case "Out of Stock":
                    query = "SELECT * FROM products WHERE product_quantity = 0";
                    break;
                case "Price":
                    query = "SELECT * FROM products ORDER BY price";
                    break;
            }
            updateTable(query);
        });

        tableModel = new DefaultTableModel();
        tableModel.addColumn("Product ID");
        tableModel.addColumn("Product Name");
        tableModel.addColumn("Quantity in stock");
        tableModel.addColumn("Product Type");
        tableModel.addColumn("Price");

        allItemsTable = new JTable(tableModel);
        allItemsTable.getTableHeader().setReorderingAllowed(false);
        allItemsTable.getTableHeader().setBackground(new Color(70, 130, 180)); // Steel blue header
        allItemsTable.getTableHeader().setForeground(Color.WHITE); // White text
        allItemsTable.setBackground(new Color(255, 255, 255)); // White background
        allItemsTable.setForeground(new Color(50, 50, 50)); // Dark text

        JScrollPane allItemsScrollPane = new JScrollPane(allItemsTable);
        allItemsScrollPane.setPreferredSize(new Dimension(600, 230));

        JPanel tablePanel = new JPanel(new BorderLayout());
        tablePanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        tablePanel.add(filterPanel, BorderLayout.NORTH);
        tablePanel.add(allItemsScrollPane, BorderLayout.CENTER);

        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, viewInventoryLabel, tablePanel);
        splitPane.setDividerLocation(30);
        splitPane.setResizeWeight(0.2);
        splitPane.setEnabled(false);

        JMenuBar menuBar = new JMenuBar();
        JMenu helpMenu = new JMenu("Help");
        JMenuItem aboutItem = new JMenuItem("About");
        JMenuItem contact = new JMenuItem("Contact us");
        aboutItem.addActionListener(e -> showAboutDialog());
        contact.addActionListener(e -> contactus());
        helpMenu.add(contact);
        helpMenu.add(aboutItem);

        JMenu reportsMenu = new JMenu("Reports");
        JMenuItem reportItem = new JMenuItem("View Sold Items Report");
        reportItem.addActionListener(e -> Reports.showSoldItemsReport());
        reportsMenu.add(reportItem);

        JMenu settingsMenu = new JMenu("Settings");
        JMenuItem changePasswordItem = new JMenuItem("Change Password");
        changePasswordItem.addActionListener(e -> changePassword());
        JMenuItem addUser = new JMenuItem("Add User");
        addUser.addActionListener(e->addUser());
        settingsMenu.add(changePasswordItem);
        settingsMenu.add(addUser);

        menuBar.add(helpMenu);
        menuBar.add(reportsMenu);
        menuBar.add(settingsMenu);

        frame.setJMenuBar(menuBar);
        frame.add(topPanel, BorderLayout.NORTH);
        frame.add(buttonPanel, BorderLayout.CENTER);
        frame.add(splitPane, BorderLayout.SOUTH);

        frame.setResizable(false);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);

        updateTable("SELECT * FROM products");
        updateTotalSoldAmountLabel();
    }

    private static void styleButton(JButton button) {
        button.setBackground(new Color(51, 153, 255));  // Soft blue
        button.setForeground(Color.WHITE);  // White text
        button.setFocusPainted(false);  // Remove focus border
        button.setFont(new Font("Arial", Font.BOLD, 14));
        button.setPreferredSize(new Dimension(110, 30));
        button.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));

        button.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                button.setBackground(new Color(47, 75, 184)); // Darker blue on hover
            }
            public void mouseExited(MouseEvent e) {
                button.setBackground(new Color(51, 153, 255)); // Back to normal color
            }
        });
    }

    private static void updateTable(String query) {
        tableModel.setRowCount(0);
        try {
            Connection con = JDBC.getConnection();
            PreparedStatement pst = con.prepareStatement(query);
            ResultSet rs = pst.executeQuery();
            while (rs.next()) {
                Object[] row = new Object[5];
                row[0] = rs.getInt("product_id");
                row[1] = rs.getString("product_name");
                row[2] = rs.getInt("product_quantity");
                row[3] = rs.getString("product_type");
                row[4] = rs.getDouble("price");
                tableModel.addRow(row);
            }
            con.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static void showAboutDialog() {
        JOptionPane.showMessageDialog(null, "Inventory Management System\nVersion 1.0\nCreated by Aman Varma", "About", JOptionPane.INFORMATION_MESSAGE);
    }

    private static void contactus() {
        JOptionPane.showMessageDialog(null, "Contact us: Inventory123@gmail.com \nPhone: +91 86522 83215 ", "Contact ", JOptionPane.INFORMATION_MESSAGE);
    }

    private static void changePassword() {
        JPasswordField oldPasswordField = new JPasswordField();
        JPasswordField newPasswordField = new JPasswordField();
        JPasswordField confirmPasswordField = new JPasswordField();

        Object[] message = {
                "Old Password:", oldPasswordField,
                "New Password:", newPasswordField,
                "Confirm New Password:", confirmPasswordField
        };

        int option = JOptionPane.showConfirmDialog(null, message, "Change Password", JOptionPane.OK_CANCEL_OPTION);
        if (option == JOptionPane.OK_OPTION) {
            String oldPassword = new String(oldPasswordField.getPassword());
            String newPassword = new String(newPasswordField.getPassword());
            String confirmPassword = new String(confirmPasswordField.getPassword());

            if (!newPassword.equals(confirmPassword)) {
                JOptionPane.showMessageDialog(null, "New passwords do not match!");
                return;
            }

            try {
                Connection con = JDBC.getConnection();
                String query = "SELECT * FROM users WHERE username = ? AND password = ?";
                PreparedStatement pst = con.prepareStatement(query);
                pst.setString(1, loggedInUser);
                pst.setString(2, oldPassword);
                ResultSet rs = pst.executeQuery();

                if (rs.next()) {
                    String updateQuery = "UPDATE users SET password = ? WHERE username = ?";
                    PreparedStatement updatePst = con.prepareStatement(updateQuery);
                    updatePst.setString(1, newPassword);
                    updatePst.setString(2, loggedInUser);
                    updatePst.executeUpdate();
                    JOptionPane.showMessageDialog(null, "Password updated successfully!");
                } else {
                    JOptionPane.showMessageDialog(null, "Incorrect old password!");
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }
    public static void updateTotalSoldAmountLabel() {
        double totalSoldAmount = 0.0;

        try {
            // Connect to the database
            Connection con = JDBC.getConnection();
            String query = "SELECT SUM(total_price) AS total_sold FROM solditems";

            PreparedStatement pst = con.prepareStatement(query);
            ResultSet rs = pst.executeQuery();

            // If the result has data, get the sum of total_price
            if (rs.next()) {
                totalSoldAmount = rs.getDouble("total_sold");
            }

            // Update the label with the new total sold amount
            totalSoldLabel.setText("Total Sold Amount: ₹" + String.format("%.2f", totalSoldAmount));

            // Close database resources
            rs.close();
            pst.close();
            con.close();
        } catch (SQLException e) {
            e.printStackTrace();  // Print any SQL exceptions
        }
    }

    public static void addUser() {
        // Create input fields for username and password
        JTextField usernameField = new JTextField(15);
        JPasswordField passwordField = new JPasswordField(15);

        // Create a panel to hold the input fields
        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(2, 2));
        panel.add(new JLabel("Username:"));
        panel.add(usernameField);
        panel.add(new JLabel("Password:"));
        panel.add(passwordField);

        // Show a dialog to get user input
        int option = JOptionPane.showConfirmDialog(null, panel, "Add New User", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (option == JOptionPane.OK_OPTION) {
            String username = usernameField.getText().trim();
            String password = new String(passwordField.getPassword()).trim();

            // Check if fields are not empty
            if (username.isEmpty() || password.isEmpty()) {
                JOptionPane.showMessageDialog(null, "Username and password cannot be empty.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Add the user to the database
            if (addUserToDatabase(username, password)) {
                JOptionPane.showMessageDialog(null, "User added successfully.");
            } else {
                JOptionPane.showMessageDialog(null, "Failed to add user. The username might already exist.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private static boolean addUserToDatabase(String username, String password) {
        boolean success = false;
        try {
            Connection con = JDBC.getConnection();
            String checkQuery = "SELECT * FROM users WHERE username = ?";
            PreparedStatement checkPst = con.prepareStatement(checkQuery);
            checkPst.setString(1, username);
            ResultSet rs = checkPst.executeQuery();

            // Check if username already exists
            if (rs.next()) {
                return false; // Username already exists
            }

            String insertQuery = "INSERT INTO users (username, password) VALUES (?, ?)";
            PreparedStatement insertPst = con.prepareStatement(insertQuery);
            insertPst.setString(1, username);
            insertPst.setString(2, password);
            int rowsInserted = insertPst.executeUpdate();

            success = rowsInserted > 0;

            con.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return success;
    }

}
