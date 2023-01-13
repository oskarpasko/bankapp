import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class RemoveCard extends JFrame {
    private JPanel panel1;
    private JComboBox cardsBox;
    private JButton backButton;
    private JButton OKButton;
    private JLabel typeLabel;
    private JLabel balanceLabel;
    private JPanel mainPanel;
    private JPanel formPanel;
    private JLabel kartaLabel;
    private JLabel typLabel;
    private JLabel saldoLabel;
    private static final String DB_URL = "jdbc:mysql://localhost:3306/bankapp";
    int rows = 0;
    Object[][] cardData = new Object[0][];
    float balance = 0;
    public RemoveCard(String client_nr) {
        super("Remove Card");
        this.setContentPane(this.panel1); // wyswietlanie okienka na ekranie
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        StylesFunction();
        this.setLayout(new GridLayout());
        this.pack();
        this.setMinimumSize(new Dimension(600,400));
        this.setLocationRelativeTo(null);

        /**
         * Download cards data QUERY
         *
         */

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
            JOptionPane.showMessageDialog(RemoveCard.this, "Error 12!");
        }/** END QUERY **/
        String nr_cards[] = new String[rows];
        for(int i=0;i<rows;i++) {
            nr_cards[i]= (String) cardData[i][0];
        }
        cardsBox.setModel(new DefaultComboBoxModel(nr_cards));

        cardGetter();

        backButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
                MainFrame main = new MainFrame(client_nr);
                main.setVisible(true);
            }
        });
        OKButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String cardnumber = cardGetter();
                String[] choices = {"Tak", "Nie"};
                if(balance==0) {
                    int input = JOptionPane.showOptionDialog(RemoveCard.this,"Czy jesteś pewny, że chcesz usunąć kartę?","Usuń kartę",0, JOptionPane.INFORMATION_MESSAGE, null, choices, null);
                    if(input==0) {
/**
 * INSERT QUERY
 *
 */
                        try {
                            Connection connection = (Connection) DriverManager.getConnection(DB_URL,
                                    "root", "rootroot");

                            /** Query which is inserting new card to database **/
                            PreparedStatement deleteCard = (PreparedStatement) connection
                                    .prepareStatement("DELETE FROM bankapp.card WHERE (card_nr =?);");

                            deleteCard.setString(1, cardnumber);
                            deleteCard.executeUpdate();

                        } catch (SQLException sqlException) {
                            // Error 12: Database is off or Your connection is invalid!
                            JOptionPane.showMessageDialog(RemoveCard.this, "Error 12!");
                        }/** END QUERY **/

                        JOptionPane.showMessageDialog(RemoveCard.this,"Usunięto kartę!");
                        dispose();
                        MainFrame main = new MainFrame(client_nr);
                        main.setVisible(true);

                    }else JOptionPane.showMessageDialog(RemoveCard.this,"Zrezygnowano z usunięcia karty!");
                } else JOptionPane.showMessageDialog(RemoveCard.this,"Karta nie może posiadać środków!");
            }
        });
        cardsBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                cardGetter();
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
        for (JLabel label : Arrays.asList(kartaLabel,typeLabel,saldoLabel,typLabel,balanceLabel)) {
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

    public String cardGetter() {
        String nrCards = (String) cardsBox.getItemAt(cardsBox.getSelectedIndex());
        int r = 0;
        while(true) {
            if(nrCards.equals(cardData[r][0])) {
                break;
            } else {
                r++;
            }
        }
        balance =  Float.parseFloat((String) cardData[r][2]);
        String card_type = (String) cardData[r][1];
        typeLabel.setText(card_type);
        balanceLabel.setText(balance+"zł");
        return nrCards;
    }
}
