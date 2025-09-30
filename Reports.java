import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Reports {
    public static void showSoldItemsReport() {
        JFrame reportFrame = new JFrame("Sold Items Report");
        reportFrame.setSize(800, 500);
        reportFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        reportFrame.setLayout(new BorderLayout());
        JPanel tableLabelPanel = new JPanel();
        tableLabelPanel.setBackground(new Color(232, 232, 232));
        JLabel tableLabel = new JLabel("Sold Items Report", JLabel.CENTER);
        tableLabel.setFont(new Font("Arial", Font.BOLD, 18));
        tableLabel.setForeground(new Color(0, 0, 0));
        tableLabel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        tableLabelPanel.add(tableLabel);
        DefaultTableModel tableModel = new DefaultTableModel();
        tableModel.addColumn("Product ID");
        tableModel.addColumn("Product Name");
        tableModel.addColumn("Sold Quantity");
        tableModel.addColumn("Price per Stock");
        tableModel.addColumn("Total Price");
        tableModel.addColumn("Transaction Date");
        tableModel.addColumn("Transaction Time");
        JTable soldItemsTable = new JTable(tableModel);
        soldItemsTable.setFillsViewportHeight(true);
        soldItemsTable.setRowHeight(30);
        soldItemsTable.setFont(new Font("Arial", Font.PLAIN, 14));
        soldItemsTable.setGridColor(new Color(200, 200, 200));
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        for (int i = 0; i < tableModel.getColumnCount(); i++) {
            soldItemsTable.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
        }
        soldItemsTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 14));
        soldItemsTable.getTableHeader().setForeground(new Color(255, 255, 255));
        soldItemsTable.getTableHeader().setBackground(new Color(0, 122, 204));
        soldItemsTable.getTableHeader().setBorder(BorderFactory.createLineBorder(new Color(0, 0, 0)));
        JScrollPane scrollPane = new JScrollPane(soldItemsTable);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        JButton exportButton = new JButton("Export to CSV");
        exportButton.setFont(new Font("Arial", Font.PLAIN, 14));
        exportButton.addActionListener(e -> exportTableToCSV(soldItemsTable));
        JPanel buttonPanel = new JPanel();
        buttonPanel.add(exportButton);
        reportFrame.add(tableLabelPanel, BorderLayout.NORTH);
        reportFrame.add(scrollPane, BorderLayout.CENTER);
        reportFrame.add(buttonPanel, BorderLayout.SOUTH);
        reportFrame.setLocationRelativeTo(null);
        reportFrame.setVisible(true);
        try {
            Connection con = JDBC.getConnection();
            String query = "SELECT * FROM solditems";
            PreparedStatement pst = con.prepareStatement(query);
            ResultSet rs = pst.executeQuery();
            while (rs.next()) {
                Object[] row = new Object[7];
                row[0] = rs.getInt("product_id");
                row[1] = rs.getString("product_name");
                row[2] = rs.getInt("sold_quantity");
                row[3] = rs.getDouble("price_perstock");
                row[4] = rs.getDouble("total_price");
                row[5] = rs.getString("transaction_date");
                row[6] = rs.getString("transaction_time");
                tableModel.addRow(row);
            }
            con.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    public static void exportTableToCSV(JTable table) {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Save CSV file");
        int userSelection = fileChooser.showSaveDialog(null);
        if (userSelection == JFileChooser.APPROVE_OPTION) {
            String filePath = fileChooser.getSelectedFile().getAbsolutePath();
            if (!filePath.endsWith(".csv")) {
                filePath += ".csv";
            }
            try (FileWriter csvWriter = new FileWriter(filePath)) {
                DefaultTableModel model = (DefaultTableModel) table.getModel();
                for (int i = 0; i < model.getColumnCount(); i++) {
                    csvWriter.append(model.getColumnName(i));
                    csvWriter.append(i == model.getColumnCount() - 1 ? "\n" : ",");
                }
                for (int i = 0; i < model.getRowCount(); i++) {
                    for (int j = 0; j < model.getColumnCount(); j++) {
                        csvWriter.append(model.getValueAt(i, j).toString());
                        csvWriter.append(j == model.getColumnCount() - 1 ? "\n" : ",");
                    }
                }
                JOptionPane.showMessageDialog(null, "CSV file saved successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
            } catch (IOException e) {
                JOptionPane.showMessageDialog(null, "Error saving CSV file: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                e.printStackTrace();
            }
        }
    }
}


























//Code before adding Excel export
// import javax.swing.*;
//import javax.swing.table.DefaultTableCellRenderer;
//import javax.swing.table.DefaultTableModel;
//import java.awt.*;
//import java.sql.Connection;
//import java.sql.PreparedStatement;
//import java.sql.ResultSet;
//import java.sql.SQLException;
//
//public class Reports {
//
//    public static void showSoldItemsReport() {
//        // Create the frame for the report
//        JFrame reportFrame = new JFrame("Sold Items Report");
//        reportFrame.setSize(800, 500);
//        reportFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
//        reportFrame.setLayout(new BorderLayout());
//
//        // Create a panel for the table label
//        JPanel tableLabelPanel = new JPanel();
//        tableLabelPanel.setBackground(new Color(232, 232, 232)); // Light gray background for the label
//        JLabel tableLabel = new JLabel("Sold Items Report", JLabel.CENTER);
//        tableLabel.setFont(new Font("Arial", Font.BOLD, 18));
//        tableLabel.setForeground(new Color(0, 0, 0)); // Black text
//        tableLabel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
//        tableLabelPanel.add(tableLabel);
//
//        // Create a table model and set column headers
//        DefaultTableModel tableModel = new DefaultTableModel();
//        tableModel.addColumn("Product ID");
//        tableModel.addColumn("Product Name");
//        tableModel.addColumn("Sold Quantity");
//        tableModel.addColumn("Price per Stock");
//        tableModel.addColumn("Total Price");
//        tableModel.addColumn("Transaction Date");
//        tableModel.addColumn("Transaction Time");
//
//        // Create a JTable with the model
//        JTable soldItemsTable = new JTable(tableModel);
//        soldItemsTable.setFillsViewportHeight(true);
//        soldItemsTable.setRowHeight(30); // Set row height
//        soldItemsTable.setFont(new Font("Arial", Font.PLAIN, 14));
//        soldItemsTable.setGridColor(new Color(200, 200, 200)); // Light gray grid color
//
//        // Center align the text in table cells
//        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
//        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
//        for (int i = 0; i < tableModel.getColumnCount(); i++) {
//            soldItemsTable.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
//        }
//
//        // Header customizations
//        soldItemsTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 14));
//        soldItemsTable.getTableHeader().setForeground(new Color(255, 255, 255)); // White text
//        soldItemsTable.getTableHeader().setBackground(new Color(0, 122, 204)); // Dark blue header background
//        soldItemsTable.getTableHeader().setBorder(BorderFactory.createLineBorder(new Color(0, 0, 0))); // Black border
//
//        // Add a scroll pane for the table
//        JScrollPane scrollPane = new JScrollPane(soldItemsTable);
//        scrollPane.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
//
//        // Add the table label panel and scroll pane to the frame
//        reportFrame.add(tableLabelPanel, BorderLayout.NORTH);
//        reportFrame.add(scrollPane, BorderLayout.CENTER);
//
//        // Center the frame and make it visible
//        reportFrame.setLocationRelativeTo(null);
//        reportFrame.setVisible(true);
//
//        // Fetch data from the database and populate the table
//        try {
//            Connection con = JDBC.getConnection(); // Use your JDBC connection method
//            String query = "SELECT * FROM solditems";
//            PreparedStatement pst = con.prepareStatement(query);
//            ResultSet rs = pst.executeQuery();
//
//            while (rs.next()) {
//                Object[] row = new Object[7];
//                row[0] = rs.getInt("product_id");
//                row[1] = rs.getString("product_name");
//                row[2] = rs.getInt("sold_quantity");
//                row[3] = rs.getDouble("price_perstock");
//                row[4] = rs.getDouble("total_price");
//                row[5] = rs.getString("transaction_date");
//                row[6] = rs.getString("transaction_time");
//
//                tableModel.addRow(row);
//            }
//
//            con.close();
//        } catch (SQLException e) {
//            e.printStackTrace();
//        }
//    }
//}
