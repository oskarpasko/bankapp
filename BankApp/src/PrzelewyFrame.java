import jdk.jfr.Label;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

public class PrzelewyFrame extends JFrame {
    private JPanel panel1;
    private JButton backButton;
    private JButton OKButton;
    private JComboBox cardBox;
    private JTextField odbiorcaText;
    private JTextField kwotaText;
    private JPanel mainPanel;
    private JPanel formPanel;
    private JLabel KartaLabel;
    private JLabel OdbiorcaLabel;
    private JLabel KwotaLabel;

    private static final String DB_URL = "jdbc:mysql://localhost:3306/bankapp";

    //    public static void main(String[] args) {
//        PrzelewyFrame panel = new PrzelewyFrame("1");
//        panel.setVisible(true);
//    }
    public PrzelewyFrame(String client_nr) {
        super("Nowy przelew");
        this.setContentPane(this.panel1); // wyswietlanie okienka na ekranie
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        StylesFunction();
        this.setLayout(new GridLayout());
        this.setIconImage(LoginFrame.img.getImage());

        this.pack();
        this.setMinimumSize(new Dimension(600,400));
        this.setLocationRelativeTo(null);


        backButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
                MainFrame main = new MainFrame(client_nr);
                main.setVisible(true);
            }
        });
/**
 * Download cards data QUERY
 *
 */
        Object[][] cardData = new Object[0][];
        int rows = 0;
        try {
            Connection connection = (Connection) DriverManager.getConnection(DB_URL,
                    "root", "rootroot");

            /** Query which return count of client's cards **/
            PreparedStatement countRows = (PreparedStatement) connection
                    .prepareStatement("SELECT count(card_nr) as countRows FROM bankapp.card WHERE client_nr =?");

            countRows.setString(1, client_nr);
            ResultSet countRowsResult = countRows.executeQuery();
            rows = 0;

            // download how many rows/cards have our client
            if (countRowsResult.next()) rows = countRowsResult.getInt(1);
            // END QUERY

            /** Query which is getting number, type and balnce client's cards **/
            PreparedStatement cardInfo = (PreparedStatement) connection
                    .prepareStatement("SELECT card_nr, card_type, card_balance FROM bankapp.card WHERE client_nr =?;");

            cardInfo.setString(1, client_nr);
            ResultSet sumAllCardInfo = cardInfo.executeQuery();

            //declaration 2d object of data which we are gonna use later
            cardData = new Object[rows][3];

            for (int r = 0; r < rows; r++) {
                while (true) {
                    int i = 0;
                    if (sumAllCardInfo.next()) {
                        String card_nr = sumAllCardInfo.getString("card_nr");
                        String card_type = sumAllCardInfo.getString("card_type");
                        String card_balance = sumAllCardInfo.getString("card_balance");

                        cardData[r][i] = card_nr;
                        cardData[r][i + 1] = card_type;
                        cardData[r][i + 2] = card_balance;
                        break;
                    }
                }
            }
        } catch (SQLException sqlException) {
            // Error 12: Database is off or Your connection is invalid!
            JOptionPane.showMessageDialog(PrzelewyFrame.this, "Error 12!");
        }/** END QUERY **/

        String nr_cards[] = new String[rows];
        for(int i=0;i<rows;i++) {
            nr_cards[i]= (String) cardData[i][0];
        }
        cardBox.setModel(new DefaultComboBoxModel(nr_cards));

        Object[][] finalCardData = cardData;
        OKButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String nr_odbiorca = odbiorcaText.getText();

                if(kwotaText.getText().equals("")) {
                    JOptionPane.showMessageDialog(PrzelewyFrame.this,"Podaj kwotę przelewu!");
                } else if(nr_odbiorca.equals("")) {
                    JOptionPane.showMessageDialog(PrzelewyFrame.this,"Podaj odbiorcę przelewu!");
                } else {
                    if(!isNumeric(kwotaText.getText())) {
                        JOptionPane.showMessageDialog(PrzelewyFrame.this,"Błędna kwota!");

                    } else {
                        float value = Float.parseFloat(kwotaText.getText());
                        if (value > 0) {
                            String card_nr = (String) cardBox.getItemAt(cardBox.getSelectedIndex());
                            int r = 0;
                            while (true) {
                                if (card_nr.equals(finalCardData[r][0])) {
                                    break;
                                } else {
                                    r++;
                                }
                            }
                            float balance = Float.parseFloat((String) finalCardData[r][2]);
                            String card_type = (String) finalCardData[r][1];
                            if (card_type.equals("Kredytowa")) {
                                if ((balance - value) >= -5000) {
/**
 * Query to overflow from credit card
 */
                                    try {
                                        Connection connection = (Connection) DriverManager.getConnection(DB_URL,
                                                "root", "rootroot");

                                        /** Query which is updating balance card balance minus value **/
                                        PreparedStatement updateBalance = (PreparedStatement) connection
                                                .prepareStatement("UPDATE card SET card_balance = card_balance - " + value + " WHERE card_nr =?;");

                                        updateBalance.setString(1, card_nr);
                                        updateBalance.executeUpdate();

                                        /** Query which is checking if card exists with card number which users inputed **/
                                        PreparedStatement checkRecipient = (PreparedStatement) connection
                                                .prepareStatement("SELECT EXISTS(SELECT card_nr FROM bankapp.card WHERE card_nr =?);");

                                        checkRecipient.setString(1, nr_odbiorca);
                                        ResultSet checkRecipientResult = checkRecipient.executeQuery();

                                        if (checkRecipientResult.next()) {
                                            int check = checkRecipientResult.getInt(1);
                                            if (check == 1) {
                                                /** Query which is updating recipient's balance card balance plus value **/
                                                PreparedStatement updateRecipientBalance = (PreparedStatement) connection
                                                        .prepareStatement("UPDATE card SET card_balance = card_balance + " + value + " WHERE card_nr =?;");

                                                updateRecipientBalance.setString(1, nr_odbiorca);
                                                updateRecipientBalance.executeUpdate();

                                                /** Query which is inserting new overflow **/
                                                PreparedStatement insertOverflow = (PreparedStatement) connection
                                                        .prepareStatement("INSERT INTO overflow values(null, ?, ?, ?, ?);");

                                                insertOverflow.setString(1, card_nr);
                                                insertOverflow.setString(2, nr_odbiorca);
                                                insertOverflow.setString(3, String.valueOf(LocalDate.now()));
                                                insertOverflow.setString(4, String.valueOf(value));

                                                insertOverflow.executeUpdate();
                                            }
                                        }

                                    } catch (SQLException sqlException) {
                                        // Error 12: Database is off or Your connection is invalid!
                                        JOptionPane.showMessageDialog(PrzelewyFrame.this, "Error 12!");
                                    }/** END QUERY **/

                                    JOptionPane.showMessageDialog(PrzelewyFrame.this, "Poszedł przelew o kwocie: " + value + " z karty: " + card_nr + " dla odbiorcy: " + nr_odbiorca);
                                    dispose();
                                    MainFrame main = new MainFrame(client_nr);
                                    main.setVisible(true);

                                } else JOptionPane.showMessageDialog(PrzelewyFrame.this, "Za mało środków na karcie!");
                            } else {
                                if (balance - value >= 0) {
/**
 * Query to overflow from debet card
 */
                                    try {
                                        Connection connection = (Connection) DriverManager.getConnection(DB_URL,
                                                "root", "rootroot");

                                        /** Query which is updating balance card balance minus value **/
                                        PreparedStatement updateBalance = (PreparedStatement) connection
                                                .prepareStatement("UPDATE card SET card_balance = card_balance - " + value + " WHERE card_nr =?;");

                                        updateBalance.setString(1, card_nr);
                                        updateBalance.executeUpdate();

                                        /** Query which is checking if card exists with card number which users inputed **/
                                        PreparedStatement checkRecipient = (PreparedStatement) connection
                                                .prepareStatement("SELECT EXISTS(SELECT card_nr FROM bankapp.card WHERE card_nr =?);");

                                        checkRecipient.setString(1, nr_odbiorca);
                                        ResultSet checkRecipientResult = checkRecipient.executeQuery();

                                        if (checkRecipientResult.next()) {
                                            int check = checkRecipientResult.getInt(1);
                                            if (check == 1) {
                                                /** Query which is updating recipient's balance card balance plus value **/
                                                PreparedStatement updateRecipientBalance = (PreparedStatement) connection
                                                        .prepareStatement("UPDATE card SET card_balance = card_balance + " + value + " WHERE card_nr =?;");

                                                updateRecipientBalance.setString(1, nr_odbiorca);
                                                updateRecipientBalance.executeUpdate();
                                            }
                                        }

                                    } catch (SQLException sqlException) {
                                        // Error 12: Database is off or Your connection is invalid!
                                        JOptionPane.showMessageDialog(PrzelewyFrame.this, "Error 12!");
                                    }/** END QUERY **/

                                    JOptionPane.showMessageDialog(PrzelewyFrame.this, "Poszedł przelew o kwocie: " + value + " z karty: " + card_nr + " dla odbiorcy: " + nr_odbiorca);
                                    dispose();
                                    MainFrame main = new MainFrame(client_nr);
                                    main.setVisible(true);

                                } else JOptionPane.showMessageDialog(PrzelewyFrame.this, "Za mało środków na karcie!");
                            }
                        } else {
                            JOptionPane.showMessageDialog(PrzelewyFrame.this, "Przelew musi być większy od 0!");
                        }
                    }
                }
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
        for (JLabel label : Arrays.asList(KartaLabel,KwotaLabel,OdbiorcaLabel)) {
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
