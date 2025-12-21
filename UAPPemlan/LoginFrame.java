package UAPPemlan;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.List;

public class LoginFrame extends JFrame {
    private JTextField txtUsername;
    private JPasswordField txtPassword;
    private Anggota currentUser;

    public LoginFrame() {
        setTitle("Sistem Perpustakaan UMM");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(400, 300);
        setLocationRelativeTo(null);

        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);

        gbc.gridx = 0; gbc.gridy = 0;
        panel.add(new JLabel("Username:"), gbc);
        gbc.gridx = 1;
        txtUsername = new JTextField(15);
        panel.add(txtUsername, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        panel.add(new JLabel("Password:"), gbc);
        gbc.gridx = 1;
        txtPassword = new JPasswordField(15);
        panel.add(txtPassword, gbc);

        gbc.gridx = 0; gbc.gridy = 2; gbc.gridwidth = 2;
        JButton btnLogin = new JButton("Login");
        btnLogin.addActionListener(this::login);
        panel.add(btnLogin, gbc);

        add(panel);
        setVisible(true);
    }

    private void login(ActionEvent e) {
        List<Anggota> anggotaList = CSVManager.loadAnggota();
        for (Anggota a : anggotaList) {
            if (a.getUsername().equals(txtUsername.getText()) &&
                    a.getPassword().equals(new String(txtPassword.getPassword()))) {
                currentUser = a;
                dispose();
                if (a.getUsername().equals("admin")) {
                    new AdminFrame();
                } else {
                    new UserFrame(a);
                }
                return;
            }
        }
        JOptionPane.showMessageDialog(this, "Username/password salah!");
    }
}

