import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
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
    private JButton wylogujButton;
    private JButton dodajButton;
    private JButton usunButton;
    private JPanel welcomePanel;
    private JPanel tabsPanel;
    private JPanel buttonsPanel;
    private JPanel logoutButtons;

    // URL for connection with database
    private static final String DB_URL = "jdbc:mysql://localhost:3306/bankapp";

    public MainFrame(String client_number) {
        super("MainFrame");
        StylesFunction();
        this.setContentPane(this.panel1); // wyswietlanie okienka na ekranie
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setSize(700,600);
        /** center window **/
        this.setLocationRelativeTo(null);
        DatabaseQueries(client_number);

        wyjdzButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
                LoginFrame login = new LoginFrame();
                login.setVisible(true);
            }
        });
        wplataButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
                WplataFrame wplata = new WplataFrame(client_number);
                wplata.setVisible(true);
            }
        });
        wyplataButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
                WyplataFrame wyplata = new WyplataFrame(client_number);
                wyplata.setVisible(true);
            }
        });
        przelewButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
                PrzelewyFrame przelewy = new PrzelewyFrame(client_number);
                przelewy.setVisible(true);
            }
        });
        dodajButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
                AddCard addCard = new AddCard(client_number);
                addCard.setVisible(true);
            }
        });
    }

    private void DatabaseQueries(String client_nr)
    {
        try {
            //connection with database
            Connection connection = (Connection) DriverManager.getConnection(DB_URL,
                    "root", "rootroot");
/**
 * Welcome Query
 */
            /** Query which is getting first name out client **/
            PreparedStatement st = (PreparedStatement) connection
                    .prepareStatement("SELECT client_fname FROM bankapp.client WHERE client_nr=?");

            st.setString(1, client_nr); // replacment first '?' in query by clinet number
            ResultSet rs = st.executeQuery();        // execute query

            // chech if some rows are existing in database
            // if yes then download first name from db and output in nameField
            //if no then ouput error
            if (rs.next()) {
                String clientName = rs.getString("client_fname");
                nameField.setText("Witaj, " + clientName);
            } else {
                // Error 123: Data in database are not comipled!
                JOptionPane.showMessageDialog(MainFrame.this, "Error 123!");
            }// END QUERY

            /** Query which gets client's all balance **/
            PreparedStatement sumBalance = (PreparedStatement) connection
                    .prepareStatement("SELECT ROUND(SUM(card_balance), 2) AS balance FROM card WHERE client_nr=?");

            sumBalance.setString(1, client_nr);
            ResultSet sumBalanceResult = sumBalance.executeQuery();

            if (sumBalanceResult.next()) {
                saldoField.setText("Saldo: " + sumBalanceResult.getString("balance"));

            } else {
                // Error 123: Data in database dose not exist!
                JOptionPane.showMessageDialog(MainFrame.this, "Error 123!");
            }// END QUERY

/**
 *  Cards Queries
 */

            /** Query which return count of client's cards **/
            PreparedStatement countRows = (PreparedStatement) connection
                    .prepareStatement("SELECT count(card_nr) as countRows FROM bankapp.card WHERE client_nr =?");

            countRows.setString(1, client_nr);
            ResultSet countRowsResult = countRows.executeQuery();
            int rows = 0;

            // download how many rows/cards have our client
            if(countRowsResult.next()) rows = countRowsResult.getInt(1);
            // END QUERY

            /** Query which gets balance for our client's each card **/
            PreparedStatement allBalances = (PreparedStatement) connection
                    .prepareStatement("SELECT card_nr, card_term_data, card_type, card_balance FROM bankapp.card WHERE client_nr =?;");

            allBalances.setString(1, client_nr);
            ResultSet sumAllBalances = allBalances.executeQuery();

            //declaration 2d object of data which we are gonna use later
            Object[][] data = new Object[rows][4];

            // mechanism to write all data from database to out variable "data", dirst rows then columns
            for(int r=0;r<rows;r++) {
                while(true){
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

            // add headers to table and then display it (table)
            String[] headers = {"Numer Karty", "Data Ważności", "Typ Karty", "Saldo Karty"};
            kartyTable.setModel(new DefaultTableModel(data, headers));
            // END QUERY

/**
 *  Overflows Queries
 */

            /** Query which return count of client's overflows **/
            PreparedStatement countOverflowRows = (PreparedStatement) connection
                    .prepareStatement(
                            "SELECT COUNT(overflow_id)" +
                                    "FROM bankapp.overflow " +
                                    "LEFT JOIN Card ON overflow.overflow_send_number = card.card_nr OR overflow.overflow_recipent_number = card.card_nr " +
                                    "WHERE client_nr =?;");

            countOverflowRows.setString(1, client_nr);
            ResultSet countAllOverflowRows = countOverflowRows.executeQuery();
            int overflowRows = 0;

            // download how many overflows have our client
            if(countAllOverflowRows.next()) overflowRows = countAllOverflowRows.getInt(1);

            /** Query which gets balance for our client's each card **/
            PreparedStatement allOverflows = (PreparedStatement) connection
                    .prepareStatement(
                            "SELECT overflow_send_number, overflow_recipent_number, overflow_data, overflow_amount FROM bankapp.overflow  " +
                            "LEFT JOIN card ON overflow.overflow_send_number = card.card_nr OR overflow.overflow_recipent_number = card.card_nr  " +
                            "WHERE client_nr =?");

            allOverflows.setString(1, client_nr);
            ResultSet sumAllOverflows = allOverflows.executeQuery();

            //declaration 2d object of data which we are gonna use later
            Object[][] oferflowData = new Object[overflowRows][4];

            // mechanism to write all data from database to out variable "data", dirst rows then columns
            for(int r=0;r<overflowRows;r++) {
                while(true){
                    int i = 0;
                    if (sumAllOverflows.next()) {
                        String overflow_send_number = sumAllOverflows.getString("overflow_send_number");
                        String overflow_recipent_number = sumAllOverflows.getString("overflow_recipent_number");
                        String overflow_data = sumAllOverflows.getString("overflow_data");
                        String overflow_amount = sumAllOverflows.getString("overflow_amount");

                        oferflowData[r][i] = overflow_send_number;
                        oferflowData[r][i+1] = overflow_recipent_number;
                        oferflowData[r][i+2] = overflow_data;
                        oferflowData[r][i+3] = overflow_amount;
                        break;
                    }
                }
            }

            // add headers to table and then display it (table)
            String[] oferflowHeaders = {"Nr Wysyłającego", "Nr Odbiorcy", "Data Przelewu", "Wartość przelewu"};
            historiaTable.setModel(new DefaultTableModel(oferflowData, oferflowHeaders));
            // END QUERY

        } catch (SQLException sqlException) {
            // Error 12: Database is off or Your connection is invalid!
            JOptionPane.showMessageDialog(MainFrame.this, "Error 12!");
        }
    }

    private void StylesFunction() {
        /** buttons style **/
        wplataButton.setBackground(new Color(108, 220, 96));
        wyplataButton.setBackground(new Color(108, 220, 96));
        przelewButton.setBackground(new Color(108, 220, 96));

        /** text field style **/
        panel1.setBackground(new Color(42, 42, 42));
        /** margin **/
//        Border border = witajLabel.getBorder();
//        Border margin = new EmptyBorder(0,100,100,100);
//        witajLabel.setBorder(new CompoundBorder(border, margin));

        panel1.setBorder(BorderFactory.createEmptyBorder(20,20,20,20));
////        welcomePanel.setBorder(BorderFactory.createEmptyBorder(20,20,20,20));
//        tabsPanel.setBorder(BorderFactory.createEmptyBorder(20,20,20,20));
//        buttonsPanel.setBorder(BorderFactory.createEmptyBorder(20,20,20,20));

    }
}
