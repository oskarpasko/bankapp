
import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.regex.Pattern;

public class AddCard extends JFrame {
    private JPanel panel1;
    private JButton backButton;
    private JTextField nrCardText;
    private JComboBox typComboBox;
    private JButton OKButton;

    public AddCard(String client_number) {
        super("Add Cards");
        this.setContentPane(this.panel1); // wyswietlanie okienka na ekranie
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setSize(700, 600);
        /** center window **/
        this.setLocationRelativeTo(null);


        typComboBox.setModel(new DefaultComboBoxModel(new String[]{"Debetowa","Kredytowa"}));

        backButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
                MainFrame main = new MainFrame(client_number);
                main.setVisible(true);
            }

        });
        OKButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String card_nr = nrCardText.getText();
                String card_type = (String) typComboBox.getItemAt(typComboBox.getSelectedIndex());
                if(card_nr.equals("")||!isNumeric(card_nr)||card_nr.length()!=16) {
                    JOptionPane.showMessageDialog(AddCard.this,"Podaj prawidłowy numer karty!");
                } else {
                    //TODO: dodaj do bazy
                    JOptionPane.showMessageDialog(AddCard.this,"Prawidłowy numer!");
                }

            }
        });
    }

    private Pattern pattern = Pattern.compile("[0-9]+");
    private boolean isNumeric(String card_nr) {
        if (card_nr == null) {
            return false;
        }
        return pattern.matcher(card_nr).matches();
    }
}


