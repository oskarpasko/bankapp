import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class WyplataFrame extends JFrame {
    private JPanel panel1;
    private JTextField textField1;
    private JButton OKButton;

    public WyplataFrame(String client_nr) {
        super("WyplataFrame");
        this.setContentPane(this.panel1); // wyswietlanie okienka na ekranie
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setSize(700, 600);

        System.out.println(client_nr);

        OKButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

            }
        });
    }
}
