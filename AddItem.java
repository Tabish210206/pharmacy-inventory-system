import javax.swing.*;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

public class AddItem {

    public static void showAddItemDialog() {
        // Create the frame for adding a product
        JFrame addItemFrame = new JFrame("Add Product");
        addItemFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        addItemFrame.setSize(500, 400);
        addItemFrame.setLayout(null);  // Use absolute layout for custom positioning

        // Set background panel
        JPanel panel = new JPanel();
        panel.setLayout(null);
        panel.setBackground(new Color(60, 63, 65));  // Dark background color
        addItemFrame.setContentPane(panel);

        // Create form fields
        JLabel nameLabel = new JLabel("Product Name:");
        JLabel quantityLabel = new JLabel("Quantity:");
        JLabel typeLabel = new JLabel("Product Type:");
        JLabel priceLabel = new JLabel("Price:");

        // Style the labels
        Font labelFont = new Font("Arial", Font.BOLD, 14);
        nameLabel.setFont(labelFont);
        quantityLabel.setFont(labelFont);
        typeLabel.setFont(labelFont);
        priceLabel.setFont(labelFont);
        nameLabel.setForeground(Color.WHITE);
        quantityLabel.setForeground(Color.WHITE);
        typeLabel.setForeground(Color.WHITE);
        priceLabel.setForeground(Color.WHITE);

        // Create text fields
        JTextField nameField = new JTextField();
        JTextField quantityField = new JTextField();
        JTextField typeField = new JTextField();
        JTextField priceField = new JTextField();

        // Style the text fields
        Font fieldFont = new Font("Arial", Font.PLAIN, 14);
        nameField.setFont(fieldFont);
        quantityField.setFont(fieldFont);
        typeField.setFont(fieldFont);
        priceField.setFont(fieldFont);

        nameField.setBackground(Color.BLACK);
        nameField.setForeground(Color.GREEN);
        nameField.setCaretColor(Color.WHITE);

        quantityField.setBackground(Color.BLACK);
        quantityField.setForeground(Color.GREEN);
        quantityField.setCaretColor(Color.WHITE);

        typeField.setBackground(Color.BLACK);
        typeField.setForeground(Color.GREEN);
        typeField.setCaretColor(Color.WHITE);

        priceField.setBackground(Color.BLACK);
        priceField.setForeground(Color.GREEN);
        priceField.setCaretColor(Color.WHITE);

        // Add border around text fields
        nameField.setBorder(BorderFactory.createLineBorder(Color.GRAY, 1));
        quantityField.setBorder(BorderFactory.createLineBorder(Color.GRAY, 1));
        typeField.setBorder(BorderFactory.createLineBorder(Color.GRAY, 1));
        priceField.setBorder(BorderFactory.createLineBorder(Color.GRAY, 1));

        // Create the add button and set its style
        JButton addButton = new JButton("Add Product");
        addButton.setBackground(new Color(19, 117, 214));  // Blue button color
        addButton.setForeground(Color.WHITE);
        addButton.setFocusPainted(false);
        addButton.setFont(new Font("Arial", Font.BOLD, 14));
        addButton.setBorder(BorderFactory.createLineBorder(Color.GRAY, 1));

        // Position components using setBounds
        nameLabel.setBounds(50, 50, 150, 30);
        nameField.setBounds(220, 50, 200, 30);
        quantityLabel.setBounds(50, 100, 150, 30);
        quantityField.setBounds(220, 100, 200, 30);
        typeLabel.setBounds(50, 150, 150, 30);
        typeField.setBounds(220, 150, 200, 30);
        priceLabel.setBounds(50, 200, 150, 30);
        priceField.setBounds(220, 200, 200, 30);
        addButton.setBounds(220, 250, 200, 40);

        // Add components to the panel
        panel.add(nameLabel);
        panel.add(nameField);
        panel.add(quantityLabel);
        panel.add(quantityField);
        panel.add(typeLabel);
        panel.add(typeField);
        panel.add(priceLabel);
        panel.add(priceField);
        panel.add(addButton);

        // Center the frame on the screen
        addItemFrame.setLocationRelativeTo(null);
        addItemFrame.setVisible(true);

        // Action Listener for the add button
        addButton.addActionListener(e -> {
            String productName = nameField.getText();
            int productQuantity;
            double price;
            String productType = typeField.getText();

            // Input validation
            try {
                productQuantity = Integer.parseInt(quantityField.getText());
                price = Double.parseDouble(priceField.getText());

                if (productName.isEmpty() || productType.isEmpty()) {
                    JOptionPane.showMessageDialog(null, "Please fill all fields.", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                // Call method to handle database insert/update
                addOrUpdateProduct(productName, productQuantity, productType, price);

                // Close the frame after successful addition
                addItemFrame.dispose();
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(null, "Please enter valid numbers for quantity and price.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
    }

    // Method to add or update product in the database
    private static void addOrUpdateProduct(String productName, int productQuantity, String productType, double price) {
        try {

            Connection conn = JDBC.getConnection();
            String selectQuery = "SELECT * FROM products WHERE product_name = ?";
            PreparedStatement psSelect = conn.prepareStatement(selectQuery);
            psSelect.setString(1, productName);
            ResultSet rs = psSelect.executeQuery();

            if (rs.next()) {
                int existingQuantity = rs.getInt("product_quantity");
                int newQuantity = existingQuantity + productQuantity;

                String updateQuery = "UPDATE products SET product_quantity = ? WHERE product_name = ?";
                PreparedStatement psUpdate = conn.prepareStatement(updateQuery);
                psUpdate.setInt(1, newQuantity);
                psUpdate.setString(2, productName);
                psUpdate.executeUpdate();

                JOptionPane.showMessageDialog(null, "Product quantity updated successfully.");
            } else {
                // If the product does not exist, insert a new product
                String insertQuery = "INSERT INTO products (product_name, product_quantity, product_type, price) VALUES (?, ?, ?, ?)";
                PreparedStatement psInsert = conn.prepareStatement(insertQuery, Statement.RETURN_GENERATED_KEYS);
                psInsert.setString(1, productName);
                psInsert.setInt(2, productQuantity);
                psInsert.setString(3, productType);
                psInsert.setDouble(4, price);
                psInsert.executeUpdate();

                // Retrieve the generated product ID
                ResultSet generatedKeys = psInsert.getGeneratedKeys();
                if (generatedKeys.next()) {
                    int newProductId = generatedKeys.getInt(1);
                    JOptionPane.showMessageDialog(null, "New product added successfully with Product ID: " + newProductId);
                }
            }

            // Close resources
            psSelect.close();
            rs.close();
            conn.close();

        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error while adding/updating product: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
