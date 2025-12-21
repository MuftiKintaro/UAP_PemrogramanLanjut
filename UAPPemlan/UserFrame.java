package UAPPemlan;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.List;

public class UserFrame extends JFrame {
    private Anggota user;
    private JTable tableBuku, tableRiwayat;
    private DefaultTableModel modelBuku, modelRiwayat;

    public UserFrame(Anggota user) {
        this.user = user;
        initUI();
    }

    private void initUI() {
        setTitle("User Dashboard - " + user.getNama());
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(900, 600);
        setLocationRelativeTo(null);

        JTabbedPane tabbedPane = new JTabbedPane();

        // Tab Cari Buku
        JPanel cariPanel = createCariBukuPanel();
        tabbedPane.addTab("Cari Buku", cariPanel);

        // Tab Riwayat
        JPanel riwayatPanel = createRiwayatPanel();
        tabbedPane.addTab("Riwayat Peminjaman", riwayatPanel);

        add(tabbedPane);
        setVisible(true);
    }

    private JPanel createCariBukuPanel() {
        JPanel panel = new JPanel(new BorderLayout());

        JTextField txtCari = new JTextField();
        JButton btnCari = new JButton("Cari");
        JPanel searchPanel = new JPanel();
        searchPanel.add(new JLabel("Cari: "));
        searchPanel.add(txtCari);
        searchPanel.add(btnCari);

        modelBuku = new DefaultTableModel(new String[]{"Kode", "Judul", "Penulis", "Stok", "Rating"}, 0);
        tableBuku = new JTable(modelBuku);
        JScrollPane scrollBuku = new JScrollPane(tableBuku);

        panel.add(searchPanel, BorderLayout.NORTH);
        panel.add(scrollBuku, BorderLayout.CENTER);

        btnCari.addActionListener(e -> loadBuku(txtCari.getText()));
        tableBuku.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                if (evt.getClickCount() == 2) pinjamBuku();
            }
        });

        loadBuku("");
        return panel;
    }

    private void loadBuku(String keyword) {
        modelBuku.setRowCount(0);
        List<Buku> bukuList = CSVManager.loadBuku();
        for (Buku b : bukuList) {
            if (b.getTitle().toLowerCase().contains(keyword.toLowerCase()) ||
                    b.getKodeBuku().toLowerCase().contains(keyword.toLowerCase())) {
                modelBuku.addRow(new Object[]{
                        b.getKodeBuku(), b.getTitle(), b.getAuthor(),
                        b.getStock(), String.format("%.1f", b.getAvgRating())
                });
            }
        }
    }

    private void pinjamBuku() {
        int row = tableBuku.getSelectedRow();
        if (row >= 0) {
            String kodeBuku = (String) modelBuku.getValueAt(row, 0);
            if (CSVManager.pinjamBuku(user.getId(), kodeBuku)) {
                JOptionPane.showMessageDialog(this, "Buku berhasil dipinjam!");
                loadBuku("");
            } else {
                JOptionPane.showMessageDialog(this, "Gagal pinjam: stok habis atau akun dibatasi!");
            }
        }
    }

    private JPanel createRiwayatPanel() {
        modelRiwayat = new DefaultTableModel(new String[]{"ID", "Buku", "Pinjam", "Batas", "Status"}, 0);
        tableRiwayat = new JTable(modelRiwayat);
        JScrollPane scroll = new JScrollPane(tableRiwayat);
        loadRiwayat();
        return new JPanel(new BorderLayout()) {{
            add(scroll);
        }};
    }

    private void loadRiwayat() {
        modelRiwayat.setRowCount(0);
        List<Peminjaman> list = CSVManager.loadPeminjaman();
        for (Peminjaman p : list) {
            if (p.getAnggotaId().equals(user.getId())) {
                modelRiwayat.addRow(new Object[]{
                        p.getId(), p.getBukuId(), p.getTglPinjam(), p.getTglKembali(), p.getStatus()
                });
            }
        }
    }
}
