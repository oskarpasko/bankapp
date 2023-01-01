import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;
import java.util.Arrays;

public class RemoveCard extends JFrame {
    private JPanel panel1;
    private JComboBox cardsBox;
    private JButton backButton;
    private JButton OKButton;
    private JLabel typeLabel;
    private JLabel balanceLabel;
    private static final String DB_URL = "jdbc:mysql://localhost:3306/bankapp";
    int rows = 0;
    Object[][] cardData = new Object[0][];
    float balance = 0;
    public RemoveCard(String client_nr) {
        super("Remove Card");
        this.setContentPane(this.panel1); // wyswietlanie okienka na ekranie
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setSize(700, 600);
        /** center window **/
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
                System.out.println(cardnumber);
                String[] choices = {"Tak", "Nie"};
                if(balance==0) {
                    int input = JOptionPane.showOptionDialog(RemoveCard.this,"Czy jesteś pewny, że chcesz usunąć kartę?","Usuń kartę",0, JOptionPane.INFORMATION_MESSAGE, null, choices, null);
                    if(input==0) {
                        //TODO: baza
                        System.out.println(Arrays.toString(nr_cards));
                        JOptionPane.showMessageDialog(RemoveCard.this,"Usunięto kartę!");
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
