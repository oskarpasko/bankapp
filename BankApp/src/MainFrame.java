import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;

public class MainFrame extends JFrame {
    private JPanel panel1;
    private JTabbedPane tabbedPane1;
    private JButton wplataButton;
    private JButton wyplataButton;
    private JButton przelewButton;
    private JButton wyjdzButton;
    private JTable kartyTable;
    private JTable historiaTable;
    private JLabel nameField;
    private JLabel saldoField;

    // URL for connection with database
    private static final String DB_URL = "jdbc:mysql://localhost:3306/BankApp";

    public MainFrame(String client_number) {
        super("MainFrame");
        this.setContentPane(this.panel1); // wyswietlanie okienka na ekranie
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setSize(700, 600);

        DatabaseQueries(client_number);

//        wyjdzButton.addActionListener(new ActionListener() {
//            @Override
//            public void actionPerformed(ActionEvent e) {
//                dispose();
//                LoginFrame login = new LoginFrame();
//                login.setVisible(true);
//            }
//        });
//        wplataButton.addActionListener(new ActionListener() {
//            @Override
//            public void actionPerformed(ActionEvent e) {
//                dispose();
//                WplataFrame wplata = new WplataFrame(client_nr);
//                wplata.setVisible(true);
//            }
//        });
//        wyplataButton.addActionListener(new ActionListener() {
//            @Override
//            public void actionPerformed(ActionEvent e) {
//                dispose();
//                WyplataFrame wyplata = new WyplataFrame();
//                wyplata.setVisible(true);
//            }
//        });
    }

    private void DatabaseQueries(String client_nr)
    {
        try {
            Connection connection = (Connection) DriverManager.getConnection(DB_URL,
                    "root", "rootroot");

            /** Query which is getting first name out client **/
            PreparedStatement st = (PreparedStatement) connection
                    .prepareStatement("SELECT client_fname FROM BankApp.Client WHERE client_nr=?");

            st.setString(1, client_nr);
            ResultSet rs = st.executeQuery();

            if (rs.next()) {
                String clientName = rs.getString("client_fname");

                nameField.setText("Witaj, " + clientName);

            } else {
                // Error 123: Data in database are not comipled!
                JOptionPane.showMessageDialog(MainFrame.this, "Error 123!");
            }//END QUERY

            /** Query which gets client's all balance **/
            PreparedStatement sumBalance = (PreparedStatement) connection
                    .prepareStatement("SELECT ROUND(SUM(card_balance), 2) AS balance FROM CARD WHERE client_nr=?");

            sumBalance.setString(1, client_nr);
            ResultSet sumBalanceResult = sumBalance.executeQuery();

            if (sumBalanceResult.next()) {
                saldoField.setText("Saldo: " + sumBalanceResult.getString("balance"));

            } else {
                // Error 123: Data in database dose not exist!
                JOptionPane.showMessageDialog(MainFrame.this, "Error 123!");
            }//END QUERY

            /** Query which gets balance for our client's each card **/
            PreparedStatement allBalances = (PreparedStatement) connection
                    .prepareStatement("SELECT card_nr, card_term_data, card_type, card_balance FROM BankApp.Card WHERE client_nr =?;");

            allBalances.setString(1, client_nr);
            ResultSet sumAllBalances = allBalances.executeQuery();

            PreparedStatement countRows = (PreparedStatement) connection
                    .prepareStatement("SELECT count(card_nr) as countRows FROM BankApp.Card WHERE client_nr =?");

            countRows.setString(1, client_nr);
            ResultSet countRowsResult = countRows.executeQuery();
            int rows = 0;

            if(countRowsResult.next())
            {
                rows = countRowsResult.getInt(1);
            }

            Object[][] data = new Object[rows][4];

            for(int r=0;r<rows;r++) {
                for(int c=0;c<3;c++){
                    int i = 0;
                    if (sumAllBalances.next()) {
                        String card_nr = sumAllBalances.getString("card_nr");
                        String card_term_data = sumAllBalances.getString("card_term_data");
                        String card_type = sumAllBalances.getString("card_type");
                        String card_balance = sumAllBalances.getString("card_balance");

                        data[r][i] = card_nr;
                        data[r][i+1] = card_term_data;
                        data[r][i+2] = card_type;
                        data[r][i+3] = card_balance;
                        break;
                    }
                }
            }

            String[] naglowek = {"Numer Karty", "Data Ważności", "Typ Karty", "Saldo Karty"};
            kartyTable.setModel(new DefaultTableModel(data, naglowek));

        } catch (SQLException sqlException) {
            // Error 12: Database is off or Your connection is invalid!
            JOptionPane.showMessageDialog(MainFrame.this, "Error 12!");
        }
    }
}
