import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

public class WyplataFrame extends JFrame {
    private JPanel panel1;
    private JButton OKButton;
    private JButton backButton;
    private JRadioButton a50RadioButton;
    private JRadioButton a100RadioButton;
    private JRadioButton a200RadioButton;
    private JRadioButton a300RadioButton;
    private JRadioButton a500RadioButton;
    private JRadioButton wlasnaRadio;
    private JTextField wlasnaText;
    private JComboBox cardsBox;
    private JPanel mainPanel;
    private JPanel formPanel;
    private JLabel KartaLabel;
    private JLabel KwotaLabel;
    private JPanel kartaPanel;
    private JPanel wartoscPanel;
    private int value;

    // URL for connection with database
    private static final String DB_URL = "jdbc:mysql://localhost:3306/bankapp";

    public WyplataFrame(String client_nr) {
        super("Wypłata środków");
        this.setContentPane(this.panel1); // wyswietlanie okienka na ekranie
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        StylesFunction();
        this.setLayout(new GridLayout());
        this.setIconImage(LoginFrame.img.getImage());

        this.pack();
        this.setMinimumSize(new Dimension(600,400));
        this.setLocationRelativeTo(null);

        ButtonGroup bg = new ButtonGroup();
        bg.add(a50RadioButton);
        bg.add(a100RadioButton);
        bg.add(a200RadioButton);
        bg.add(a300RadioButton);
        bg.add(a500RadioButton);
        bg.add(wlasnaRadio);

        ActionListener listener = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (wlasnaRadio.isSelected()) {
                    wlasnaText.setEnabled(true);
                } else {
                    wlasnaText.setText("");
                    wlasnaText.setEnabled(false);
                }
            }
        };

        a50RadioButton.addActionListener(listener);
        a100RadioButton.addActionListener(listener);
        a200RadioButton.addActionListener(listener);
        a300RadioButton.addActionListener(listener);
        a500RadioButton.addActionListener(listener);
        wlasnaRadio.addActionListener(listener);
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
            JOptionPane.showMessageDialog(WyplataFrame.this, "Error 12!");
        }/** END QUERY **/
        String nr_cards[] = new String[rows];
        for(int i=0;i<rows;i++) {
            nr_cards[i]= (String) cardData[i][0];
        }
        cardsBox.setModel(new DefaultComboBoxModel(nr_cards));

        Object[][] finalCardData = cardData;
        OKButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (a50RadioButton.isSelected()) {
                    value = 50;
                } else if (a100RadioButton.isSelected()) {
                    value = 100;
                } else if (a200RadioButton.isSelected()) {
                    value = 200;
                } else if (a300RadioButton.isSelected()) {
                    value = 300;
                } else if (a500RadioButton.isSelected()) {
                    value = 500;
                } else if (wlasnaRadio.isSelected()) {
                    if(!isNumeric(wlasnaRadio.getText())) {
                        JOptionPane.showMessageDialog(WyplataFrame.this, "Błędna kwota!");
                    } else {
                    if (wlasnaRadio.getText().equals("")) {
                        JOptionPane.showMessageDialog(WyplataFrame.this, "Błędna kwota!");
                    } else {
                        value = Integer.parseInt(wlasnaText.getText());


                if (value >= 10 && value % 10 == 0) {

                    String card_nr = (String) cardsBox.getItemAt(cardsBox.getSelectedIndex());
                    int r = 0;
                    while(true) {
                        if(card_nr.equals(finalCardData[r][0])) {
                            break;
                        } else {
                         r++;
                        }
                    }
                    float balance =  Float.parseFloat((String) finalCardData[r][2]);
                    String card_type = (String) finalCardData[r][1];

                    if(card_type.equals("Kredytowa")) {
                        if(balance-value<-5000) {
                            JOptionPane.showMessageDialog(WyplataFrame.this, "Nie wystarczająca ilość środków na koncie!");
                        } else {
                            try {
                                Connection connection = (Connection) DriverManager.getConnection(DB_URL,
                                        "root", "rootroot");

                                /** Query which is updating balance card balance minus value **/
                                PreparedStatement updateBalance = (PreparedStatement) connection
                                        .prepareStatement("UPDATE card SET card_balance = card_balance - " + value + " WHERE card_nr =?;");

                                updateBalance.setString(1, card_nr);
                                updateBalance.executeUpdate();

                            } catch (SQLException sqlException) {
                                // Error 12: Database is off or Your connection is invalid!
                                JOptionPane.showMessageDialog(WyplataFrame.this, "Error 12!");
                            }/** END QUERY **/

                            JOptionPane.showMessageDialog(WyplataFrame.this, "Wybrano: "+value+"zł z karty: "+card_nr);
                            dispose();
                            MainFrame main = new MainFrame(client_nr);
                            main.setVisible(true);

                        }
                    } else {
                     if(value>balance) {
                        JOptionPane.showMessageDialog(WyplataFrame.this, "Nie wystarczająca ilość środków na koncie!");
                    } else {
                         try {
                             Connection connection = (Connection) DriverManager.getConnection(DB_URL,
                                     "root", "rootroot");

                             /** Query which is updating balance card balance minus value **/
                             PreparedStatement updateBalance = (PreparedStatement) connection
                                     .prepareStatement("UPDATE card SET card_balance = card_balance - " + value + " WHERE card_nr =?;");

                             updateBalance.setString(1, card_nr);
                             updateBalance.executeUpdate();

                         } catch (SQLException sqlException) {
                             // Error 12: Database is off or Your connection is invalid!
                             JOptionPane.showMessageDialog(WyplataFrame.this, "Error 12!");
                         }/** END QUERY **/

                        JOptionPane.showMessageDialog(WyplataFrame.this, "Wybrano: "+value+"zł z karty: "+card_nr);
                        dispose();
                        MainFrame main = new MainFrame(client_nr);
                        main.setVisible(true);
                    }}
                } else JOptionPane.showMessageDialog(WyplataFrame.this, "Błędna kwota!");
                    }}
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
        for (JPanel panel : Arrays.asList(panel1, mainPanel, formPanel,kartaPanel,wartoscPanel)) {
            panel.setBackground(colors.get("dark_gray"));
        }
        for (JRadioButton radio : Arrays.asList(a50RadioButton,a100RadioButton,a200RadioButton,a300RadioButton,a500RadioButton,wlasnaRadio)) {
            radio.setBackground(colors.get("dark_gray"));
            radio.setForeground(colors.get("foreground_white"));
        }

        /** foreground setter **/
        /** setting the font color of labels **/
        for (JLabel label : Arrays.asList(KartaLabel,KwotaLabel)) {
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