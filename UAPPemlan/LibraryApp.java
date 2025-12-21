package UAPPemlan;

import javax.swing.*;
import java.awt.*;
import java.io.File;

public class LibraryApp {
    public static void main(String[] args) {
        // Buat folder data jika belum ada
        new File("data").mkdirs();
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception e) {
                e.printStackTrace();
            }
            new LoginFrame();
        });
    }
}