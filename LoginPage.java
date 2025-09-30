import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class LoginPage{
    public static void main(String[] args) {
        // Create the main frame
        JFrame frame = new JFrame("Login Page");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(400, 300);
        frame.setLayout(new BorderLayout());

        // Create a panel for the login form and set layout
        JPanel panel = new JPanel();
        panel.setLayout(null);
        panel.setBackground(new Color(60, 63, 65)); // Dark background color

        // Labels for Username and Password
        JLabel label1 = new JLabel("Username:");
        JLabel label2 = new JLabel("Password:");

        // Style the labels
        label1.setForeground(Color.WHITE);
        label2.setForeground(Color.WHITE);
        label1.setFont(new Font("Arial", Font.BOLD, 14));
        label2.setFont(new Font("Arial", Font.BOLD, 14));

        // Create text fields for username and password
        JTextField textField = new JTextField();
        JPasswordField passwordField = new JPasswordField();

        // Style the text fields
        textField.setBackground(Color.BLACK);
        textField.setForeground(Color.GREEN);
        textField.setCaretColor(Color.WHITE);

        passwordField.setBackground(Color.BLACK);
        passwordField.setForeground(Color.GREEN);
        passwordField.setCaretColor(Color.WHITE);

        JButton loginButton = new JButton("Login");
        loginButton.setBackground(new Color(19, 117, 214));
        loginButton.setForeground(Color.WHITE);
        loginButton.setFocusPainted(false);
        loginButton.setFont(new Font("Arial", Font.BOLD, 14));

        textField.setBorder(BorderFactory.createLineBorder(Color.GRAY, 1));
        passwordField.setBorder(BorderFactory.createLineBorder(Color.GRAY, 1));

        label1.setBounds(50, 50, 100, 30);
        textField.setBounds(150, 50, 150, 30);
        label2.setBounds(50, 100, 100, 30);
        passwordField.setBounds(150, 100, 150, 30);
        loginButton.setBounds(150, 150, 150, 35);

        panel.add(label1);
        panel.add(textField);
        panel.add(label2);
        panel.add(passwordField);
        panel.add(loginButton);

        frame.add(panel, BorderLayout.CENTER);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);

        // Action listener for login button
        loginButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String username = textField.getText();
                String password = new String(passwordField.getPassword());

                if (validateLogin(username, password)) {
                    JOptionPane.showMessageDialog(null, "Login Successful!");
                    frame.dispose(); // Close the login frame
                    InventoryDashboard.showInventoryDashboard(username);
                } else {
                    JOptionPane.showMessageDialog(null, "Invalid Username or Password");
                }
            }
        });
    }

    // Method to validate login credentials using JDBC
    public static boolean validateLogin(String username, String password) {
        boolean isValid = false;
        try {
            Connection con = JDBC.getConnection();
            String query = "SELECT * FROM users WHERE username = ? AND password = ?";
            PreparedStatement pst = con.prepareStatement(query);
            pst.setString(1, username);
            pst.setString(2, password);
            ResultSet rs = pst.executeQuery();
            isValid = rs.next();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return isValid;
    }
}