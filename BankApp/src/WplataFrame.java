import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;

public class WplataFrame extends JFrame {
    private JPanel panel1;
    private JTextField textValue;
    private JSpinner spinnerAmount;
    private JButton OKButton;
    private JComboBox comboCards;
    private JButton backButton;
    float value;
    int spinner;

    private static final String DB_URL = "jdbc:mysql://localhost:3306/bankapp";

    public WplataFrame(String client_nr) {
        super("WplataFrame");
        this.setContentPane(this.panel1); // wyswietlanie okienka na ekranie
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setSize(700, 600);

/**
 * Download cards data QUERY
 *
 */
        String[] cards = new String[0];
        try {
            Connection connection = (Connection) DriverManager.getConnection(DB_URL,
                    "root", "rootroot");

            /** Query which return count of client's cards **/
            PreparedStatement countRows = (PreparedStatement) connection
                    .prepareStatement("SELECT count(card_nr) as countRows FROM bankapp.card WHERE client_nr =?");

            countRows.setString(1, client_nr);
            ResultSet countRowsResult = countRows.executeQuery();
            int rows = 0;

            // download how many rows/cards have our client
            if (countRowsResult.next()) rows = countRowsResult.getInt(1);
            // END QUERY

            cards = new String[rows];

            /** Query which is getting number client's cards **/
            PreparedStatement cardInfo = (PreparedStatement) connection
                    .prepareStatement("SELECT card_nr FROM bankapp.card WHERE client_nr =?;");

            cardInfo.setString(1, client_nr);
            ResultSet sumAllCardInfo = cardInfo.executeQuery();

            for (int i = 0; i < rows; i++) {
                if (sumAllCardInfo.next()) {
                    String card_nr = sumAllCardInfo.getString("card_nr");

                    cards[i] = card_nr;
                }
            }
        } catch (SQLException sqlException) {
            // Error 12: Database is off or Your connection is invalid!
            JOptionPane.showMessageDialog(WplataFrame.this, "Error 12!");
        }/** END QUERY **/

        comboCards.setModel(new DefaultComboBoxModel(cards));

        OKButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                value = Float.parseFloat(textValue.getText());
                spinner = (Integer) spinnerAmount.getValue();
                if (value >= 10 && value <= 1000 && spinner >= 1 && spinner <= 10) {
                    if (((value != 10 && value != 20 && value != 50 && value != 100 && value != 200 && value != 500) && spinner == 1) || value == 10 && spinner > 1) {
                        JOptionPane.showMessageDialog(WplataFrame.this, "Błędna kwota!");
                    } else {
                        String card_nr = (String) comboCards.getItemAt(comboCards.getSelectedIndex());
/**
 * UPDATE QUERY
 *
 */
                        try {
                            Connection connection = (Connection) DriverManager.getConnection(DB_URL,
                                    "root", "rootroot");

                            /** Query which is updating balance on card adding value **/
                            PreparedStatement updateBalance = (PreparedStatement) connection
                                    .prepareStatement("UPDATE card SET card_balance = card_balance + " + value + " WHERE card_nr =?;");

                            updateBalance.setString(1, card_nr);
                            updateBalance.executeUpdate();

                        } catch (SQLException sqlException) {
                            // Error 12: Database is off or Your connection is invalid!
                            JOptionPane.showMessageDialog(WplataFrame.this, "Error 12!");
                        }/** END QUERY **/

                        JOptionPane.showMessageDialog(WplataFrame.this,"Wpłacono na kartę nr: "+card_nr+" kwotę o wartości: "+value+"zł.");
                        dispose();
                        MainFrame main = new MainFrame(client_nr);
                        main.setVisible(true);
                    }
                } else {
                    JOptionPane.showMessageDialog(WplataFrame.this, "Błędna kwota!");
                }
            }
        });
        backButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
                MainFrame main = new MainFrame(client_nr);
                main.setVisible(true);
            }
        });
    }
}
