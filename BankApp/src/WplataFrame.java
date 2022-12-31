import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class WplataFrame extends JFrame {
    private JPanel panel1;
    private JTextField textValue;
    private JSpinner spinnerAmount;
    private JButton OKButton;
    private JComboBox comboCards;
    float balance;
    int spinner;

//    public static void main(String[] args) {
//        WplataFrame panel = new WplataFrame("1");
//        panel.setVisible(true);
//    }
// komentarz
    public WplataFrame(String client_nr) {
        super("WplataFrame");
        this.setContentPane(this.panel1); // wyswietlanie okienka na ekranie
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setSize(700, 600);

        System.out.println(client_nr);

        OKButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                balance = Float.parseFloat(textValue.getText());
                spinner = (Integer) spinnerAmount.getValue();
                if(balance>=10&&balance<=1000&&spinner>=1&&spinner<=10) {
                    if((balance!=10&&balance!=20&&balance!=50&&balance!=100&&balance!=200&&balance!=500)&&spinner==1) {
                        System.out.println("Nie wpłaci kwoty");
                    }
                    else {

                dispose();
                MainFrame main = new MainFrame(client_nr);
                main.setVisible(true);
                    }
                }

            }
        });
    }
}
