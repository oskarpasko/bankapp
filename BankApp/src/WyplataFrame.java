import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class WyplataFrame extends JFrame {
    private JPanel panel1;
    private JTextField textField1;
    private JButton OKButton;

    public WyplataFrame() {
        super("WyplataFrame");
        this.setContentPane(this.panel1); // wyswietlanie okienka na ekranie
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setSize(700, 600);

        OKButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
                MainFrame n = new MainFrame();
                n.setVisible(true);
            }
        });
    }
}
