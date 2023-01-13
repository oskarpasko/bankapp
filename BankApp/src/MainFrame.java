import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

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
    private JPanel tabsPanel;
    private JPanel buttonsPanel;
    private JPanel kartyPanel;
    private JPanel historiaPanel;
    private JScrollPane kartyScroll;
    private JLabel kartyLabel;
    private JLabel historiaLabel;
    private JScrollPane historiaScroll;

    // URL for connection with database
    private static final String DB_URL = "jdbc:mysql://localhost:3306/bankapp";

    public MainFrame(String client_number) {
        super("BankApp");
        StylesFunction();
        this.setContentPane(this.panel1); // wyswietlanie okienka na ekranie
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setIconImage(LoginFrame.img.getImage());

//        this.setSize(700,600);
        this.setLayout(new GridBagLayout());

        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(0,5,10,5);
        c.weightx = 0.5;
        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridx = 0;
        c.gridy = 0;
        panel1.add(nameField,c);
        c.gridx = 1;
        panel1.add(saldoField,c);
        c.gridx = 2;
        panel1.add(wylogujButton,c);
        c.gridx = 3;
        panel1.add(wyjdzButton,c);
        c.gridy = 1;
        c.weightx = 1;
        c.weighty = 1;
        c.gridwidth = 4;
        c.gridx = 0;
        panel1.add(tabsPanel,c);
        c.gridy = 2;
        c.gridx = 0;
        panel1.add(buttonsPanel,c);

        /** center window **/
        this.pack();


//
//        panel1.setMinimumSize(new Dimension(1700,500));
//        panel1.setPreferredSize(new Dimension(1700,500));
        this.setLocationRelativeTo(null);
        DatabaseQueries(client_number);

        wylogujButton.addActionListener(new ActionListener() {
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
        usunButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
                RemoveCard removeCard = new RemoveCard(client_number);
                removeCard.setVisible(true);
            }
        });
        wyjdzButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
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
            DefaultTableModel model = new DefaultTableModel(data, headers);
            kartyTable.setModel(model);
            TableRowSorter<TableModel> sorter = new TableRowSorter<>(model);
            kartyTable.setRowSorter(sorter);
            sorter.setComparator(3, new Comparator<Float>() {
                @Override
                public int compare(Float o1, Float o2) {
                    // Negative values should be considered lower than positive values
                    if (o1 < 0 && o2 > 0) {
                        return -1;
                    } else if (o1 > 0 && o2 < 0) {
                        return 1;
                    } else {
                        // For positive values or when both values are negative, use the default comparison
                        return o1.compareTo(o2);
                    }
                }
            });
            kartyTable.getTableHeader().setReorderingAllowed(false);
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
        Map<String, Color> colors = new HashMap<>();
        colors.put("main_green", new Color(108, 220, 96));
        colors.put("foreground_white", new Color(222, 222, 222));
        colors.put("dark_gray", new Color(42, 42, 42));
        colors.put("light_gray", new Color(63, 63, 63));
        colors.put("button_green", new Color(129, 161, 125));

// Set the background and foreground colors for all buttons
        for (JButton button : Arrays.asList(wplataButton, wyplataButton, przelewButton, dodajButton, usunButton)) {
            button.setBackground(colors.get("main_green"));
            button.setForeground(colors.get("dark_gray"));
        }
        wyjdzButton.setBackground(colors.get("button_green"));
        wylogujButton.setBackground(colors.get("button_green"));

        // Set foreground of labels
        for (JLabel label : Arrays.asList(kartyLabel,historiaLabel,nameField,saldoField)) {
            label.setForeground(colors.get("foreground_white"));
        }

// Set the background colors for all panels and scroll panes
        for (JPanel panel : Arrays.asList(panel1, tabsPanel, buttonsPanel)) {
            panel.setBackground(colors.get("dark_gray"));
        }
        for (JScrollPane scrollPane : Arrays.asList(kartyScroll, historiaScroll)) {
            scrollPane.setBackground(colors.get("dark_gray"));
            scrollPane.getViewport().setBackground(colors.get("dark_gray"));
        }

// Set the background and foreground colors for the tabbed pane and its tabs
        tabbedPane1.setBackground(colors.get("light_gray"));
        tabbedPane1.setForeground(colors.get("main_green"));
        for (JPanel panel : Arrays.asList(kartyPanel, historiaPanel)) {
            panel.setBackground(colors.get("light_gray"));
            panel.setForeground(colors.get("foreground_white"));
        }
// Set the background and foreground colors for the tables and their headers
        for (JTable table : Arrays.asList(kartyTable, historiaTable)) {
            table.setBackground(colors.get("light_gray"));
            table.setForeground(colors.get("foreground_white"));
            table.getTableHeader().setBackground(colors.get("light_gray"));
            table.getTableHeader().setForeground(colors.get("foreground_white"));
        }

// Set the selection background and foreground colors for the tables
        kartyTable.setSelectionBackground(colors.get("main_green"));
        kartyTable.setSelectionForeground(colors.get("light_gray"));
        historiaTable.setSelectionBackground(colors.get("main_green"));
        historiaTable.setSelectionForeground(colors.get("light_gray"));


    }


}
