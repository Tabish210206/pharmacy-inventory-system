import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.text.SimpleDateFormat;

public class SellItem {

    public static void showSellItemDialog() {
        // Create the frame for the Sell Product form
        JFrame sellItemFrame = new JFrame("Sell Product");
        sellItemFrame.setSize(450, 400);
        sellItemFrame.setLayout(null);  // Using null layout for advanced positioning
        sellItemFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        sellItemFrame.getContentPane().setBackground(new Color(60, 63, 65));  // Dark background

        // Create form fields with labels and text fields
        JLabel nameLabel = new JLabel("Product Name:");
        JTextField nameField = new JTextField();

        JLabel soldQuantityLabel = new JLabel("Sold Quantity:");
        JTextField soldQuantityField = new JTextField();

        JLabel dateLabel = new JLabel("Transaction Date:");
        JTextField dateField = new JTextField(new SimpleDateFormat("yyyy-MM-dd").format(new java.util.Date()));

        JLabel timeLabel = new JLabel("Transaction Time:");
        JTextField timeField = new JTextField(new SimpleDateFormat("HH:mm:ss").format(new java.util.Date()));

        JButton sellButton = new JButton("Sell");

        // Customize labels to match the style of the login page
        nameLabel.setForeground(Color.WHITE);
        soldQuantityLabel.setForeground(Color.WHITE);
        dateLabel.setForeground(Color.WHITE);
        timeLabel.setForeground(Color.WHITE);
        nameLabel.setFont(new Font("Arial", Font.BOLD, 14));
        soldQuantityLabel.setFont(new Font("Arial", Font.BOLD, 14));
        dateLabel.setFont(new Font("Arial", Font.BOLD, 14));
        timeLabel.setFont(new Font("Arial", Font.BOLD, 14));

        // Customize text fields to match the style of the login page
        nameField.setBackground(Color.BLACK);
        nameField.setForeground(Color.GREEN);
        nameField.setCaretColor(Color.WHITE);
        soldQuantityField.setBackground(Color.BLACK);
        soldQuantityField.setForeground(Color.GREEN);
        soldQuantityField.setCaretColor(Color.WHITE);
        dateField.setBackground(Color.BLACK);
        dateField.setForeground(Color.GREEN);
        dateField.setCaretColor(Color.WHITE);
        timeField.setBackground(Color.BLACK);
        timeField.setForeground(Color.GREEN);
        timeField.setCaretColor(Color.WHITE);

        // Customize the button to match the login page style
        sellButton.setBackground(new Color(19, 117, 214));
        sellButton.setForeground(Color.WHITE);
        sellButton.setFont(new Font("Arial", Font.BOLD, 14));
        sellButton.setFocusPainted(false);

        // Set borders around text fields
        nameField.setBorder(BorderFactory.createLineBorder(Color.GRAY, 1));
        soldQuantityField.setBorder(BorderFactory.createLineBorder(Color.GRAY, 1));
        dateField.setBorder(BorderFactory.createLineBorder(Color.GRAY, 1));
        timeField.setBorder(BorderFactory.createLineBorder(Color.GRAY, 1));

        // Position the components manually using setBounds
        nameLabel.setBounds(50, 50, 120, 30);
        nameField.setBounds(180, 50, 180, 30);
        soldQuantityLabel.setBounds(50, 100, 120, 30);
        soldQuantityField.setBounds(180, 100, 180, 30);
        dateLabel.setBounds(50, 150, 140, 30);
        dateField.setBounds(180, 150, 180, 30);
        timeLabel.setBounds(50, 200, 140, 30);
        timeField.setBounds(180, 200, 180, 30);
        sellButton.setBounds(180, 250, 180, 35);

        // Add components to the frame
        sellItemFrame.add(nameLabel);
        sellItemFrame.add(nameField);
        sellItemFrame.add(soldQuantityLabel);
        sellItemFrame.add(soldQuantityField);
        sellItemFrame.add(dateLabel);
        sellItemFrame.add(dateField);
        sellItemFrame.add(timeLabel);
        sellItemFrame.add(timeField);
        sellItemFrame.add(sellButton);

        sellItemFrame.setLocationRelativeTo(null);  // Center the frame
        sellItemFrame.setVisible(true);

        // Action Listener for selling the item
        sellButton.addActionListener(e -> {
            String productName = nameField.getText();
            int soldQuantity;
            String transactionDate = dateField.getText();
            String transactionTime = timeField.getText();

            // Validate inputs
            try {
                soldQuantity = Integer.parseInt(soldQuantityField.getText());

                if (productName.isEmpty() || transactionDate.isEmpty() || transactionTime.isEmpty()) {
                    JOptionPane.showMessageDialog(null, "Please fill all fields.", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                // Call method to handle product selling logic
                sellProduct(productName, soldQuantity, transactionDate, transactionTime);

                // Close the frame after successful sale
                sellItemFrame.dispose();
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(null, "Please enter valid numbers for quantity.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
    }

    private static void sellProduct(String productName, int soldQuantity, String transactionDate, String transactionTime) {
        try {
            // Connect to the database
            Connection conn = JDBC.getConnection(); // Modify this as per your setup
            String selectQuery = "SELECT product_id, product_quantity, price FROM products WHERE product_name = ?";
            PreparedStatement psSelect = conn.prepareStatement(selectQuery);
            psSelect.setString(1, productName);
            ResultSet rs = psSelect.executeQuery();

            if (rs.next()) {
                int availableQuantity = rs.getInt("product_quantity");
                double pricePerStock = rs.getDouble("price");
                int productId = rs.getInt("product_id");

                if (availableQuantity >= soldQuantity) {
                    // Calculate total price
                    double totalPrice = pricePerStock * soldQuantity;

                    // Show the total price to the user
                    JOptionPane.showMessageDialog(null, "Total Price: " + totalPrice);

                    // Update the product quantity in the products table
                    String updateProductQuery = "UPDATE products SET product_quantity = ? WHERE product_name = ?";
                    PreparedStatement psUpdate = conn.prepareStatement(updateProductQuery);
                    psUpdate.setInt(1, availableQuantity - soldQuantity);
                    psUpdate.setString(2, productName);
                    psUpdate.executeUpdate();

                    // Insert transaction into solditems table
                    String insertQuery = "INSERT INTO solditems (product_id, product_name, sold_quantity, price_perstock, total_price, transaction_time, transaction_date) VALUES (?, ?, ?, ?, ?, ?, ?)";
                    PreparedStatement psInsert = conn.prepareStatement(insertQuery);
                    psInsert.setInt(1, productId);
                    psInsert.setString(2, productName);
                    psInsert.setInt(3, soldQuantity);
                    psInsert.setDouble(4, pricePerStock);
                    psInsert.setDouble(5, totalPrice);
                    psInsert.setString(6, transactionTime);
                    psInsert.setString(7, transactionDate);
                    psInsert.executeUpdate();

                    // Update Total Sold Amount label
                    InventoryDashboard.updateTotalSoldAmountLabel();

                    JOptionPane.showMessageDialog(null, "Product sold successfully!");
                } else {
                    JOptionPane.showMessageDialog(null, "Not enough stock available. Available quantity: " + availableQuantity, "Error", JOptionPane.ERROR_MESSAGE);
                }

            } else {
                JOptionPane.showMessageDialog(null, "Product not in stock.", "Error", JOptionPane.ERROR_MESSAGE);
            }

            // Close resources
            rs.close();
            psSelect.close();
            conn.close();

        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error while selling product: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
