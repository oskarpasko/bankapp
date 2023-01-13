import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

public class WplataFrame extends JFrame {
    private JPanel panel1;
    private JTextField textValue;
    private JSpinner spinnerAmount;
    private JButton OKButton;
    private JComboBox comboCards;
    private JButton backButton;
    private JPanel mainPanel;
    private JPanel formPanel;
    private JLabel KwotaLabel;
    private JLabel IloscLabel;
    private JLabel KartaLabel;
    float value;
    int spinner;

    private static final String DB_URL = "jdbc:mysql://localhost:3306/bankapp";

    public WplataFrame(String client_nr) {
        super("Wpłata środków");
        this.setContentPane(this.panel1); // wyswietlanie okienka na ekranie
        StylesFunction();
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setIconImage(LoginFrame.img.getImage());

        this.setLayout(new GridLayout());
        this.pack();
        this.setMinimumSize(new Dimension(600,400));
        this.setLocationRelativeTo(null);
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
                if(!isNumeric(textValue.getText())) {
                    JOptionPane.showMessageDialog(WplataFrame.this,"Błędna kwota!");
                } else {
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
            }}
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

    private void StylesFunction() {
        Map<String, Color> colors = new HashMap<>();
        colors.put("main_green", new Color(108, 220, 96));
        colors.put("foreground_white", new Color(222, 222, 222));
        colors.put("dark_gray", new Color(42, 42, 42));
        colors.put("button_green", new Color(129, 161, 125));

        /** setting the background of panels **/
        for (JPanel panel : Arrays.asList(panel1, mainPanel, formPanel)) {
            panel.setBackground(colors.get("dark_gray"));
        }

        /** setting the font color of labels **/
        for (JLabel label : Arrays.asList(KartaLabel,KwotaLabel,IloscLabel)) {
            label.setForeground(colors.get("foreground_white"));
        }

        /** buttons **/
        OKButton.setBackground(colors.get("main_green"));
        OKButton.setForeground(colors.get("dark_gray"));
        backButton.setBackground(colors.get("button_green"));
        backButton.setForeground(colors.get("foreground_white"));

        /** margins **/
        panel1.setBorder(BorderFactory.createEmptyBorder(20,20,20,20));
    }

    private Pattern pattern = Pattern.compile("[0-9]+");
    private boolean isNumeric(String value) {
        if (value == null) {
            return false;
        }
        return pattern.matcher(value).matches();
    }
}
