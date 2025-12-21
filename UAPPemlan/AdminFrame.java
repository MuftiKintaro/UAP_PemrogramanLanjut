package UAPPemlan;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;
import java.util.Map;

public class AdminFrame extends JFrame {
    private JTabbedPane tabbedPane;
    private DefaultTableModel modelBuku, modelAnggota, modelPeminjaman;

    public AdminFrame() {
        setTitle("Admin Dashboard - Perpustakaan UMM");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1000, 700);
        setLocationRelativeTo(null);

        tabbedPane = new JTabbedPane();
        createTabs();
        add(tabbedPane);
        setVisible(true);
    }

    private void createTabs() {
        tabbedPane.addTab("Dashboard", createDashboard());
        tabbedPane.addTab("Kelola Buku", createKelolaBuku());
        tabbedPane.addTab("Kelola Anggota", createKelolaAnggota());
        tabbedPane.addTab("Peminjaman", createPeminjaman());
    }

    private JPanel createDashboard() {
        JPanel panel = new JPanel(new GridLayout(2, 2));
        // Stats placeholder
        panel.add(new JLabel("Total Buku: " + CSVManager.loadBuku().size()));
        panel.add(new JLabel("Total Anggota: " + CSVManager.loadAnggota().size()));
        panel.add(new JLabel("Peminjaman Aktif: 5"));
        panel.add(new JLabel("Pelanggaran: 2"));
        return panel;
    }

    private JPanel createKelolaBuku() {
        JPanel panel = new JPanel(new BorderLayout());

        JPanel inputPanel = new JPanel(new GridLayout(5, 2));
        JTextField txtKode = new JTextField();
        txtKode.setEditable(false); // Auto generate
        JTextField txtTitle = new JTextField();
        JTextField txtAuthor = new JTextField();
        JTextField txtIsbn = new JTextField();
        JTextField txtStock = new JTextField();

        JComboBox<String> cbKategori = new JComboBox<>(new String[]{"A", "B", "C", "D", "E", "F", "G", "H", "I", "J"});
        JButton btnGenerateKode = new JButton("Generate Kode");

        btnGenerateKode.addActionListener(e -> {
            String kategoriStr = (String) cbKategori.getSelectedItem();
            if (kategoriStr != null) {
                char kategori = kategoriStr.charAt(0);
                txtKode.setText(CSVManager.generateKodeBuku(kategori));
            }
        });

        inputPanel.add(new JLabel("Kategori:"));
        inputPanel.add(cbKategori);
        inputPanel.add(new JLabel("Kode Buku:"));
        inputPanel.add(txtKode);
        inputPanel.add(btnGenerateKode, new JLabel(""));
        inputPanel.add(new JLabel("Judul:"));
        inputPanel.add(txtTitle);
        inputPanel.add(new JLabel("Penulis:"));
        inputPanel.add(txtAuthor);
        inputPanel.add(new JLabel("ISBN:"));
        inputPanel.add(txtIsbn);
        inputPanel.add(new JLabel("Stock:"));
        inputPanel.add(txtStock);

        JButton btnTambah = new JButton("Tambah Buku");
        btnTambah.addActionListener(e -> tambahBuku(txtKode, txtTitle, txtAuthor, txtIsbn, txtStock));

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(btnTambah);

        modelBuku = new DefaultTableModel(new String[]{"Kode", "Judul", "Penulis", "ISBN", "Stock", "Rating"}, 0);
        JTable table = new JTable(modelBuku);
        JScrollPane scroll = new JScrollPane(table);

        panel.add(inputPanel, BorderLayout.NORTH);
        panel.add(buttonPanel, BorderLayout.CENTER);
        panel.add(scroll, BorderLayout.SOUTH);

        loadBukuTable();
        return panel;
    }


    private void tambahBuku(JTextField... fields) {
        Buku buku = new Buku(fields[0].getText(), fields[1].getText(),
                fields[2].getText(), fields[3].getText(),
                Integer.parseInt(fields[4].getText()));
        List<Buku> list = CSVManager.loadBuku();
        list.add(buku);
        CSVManager.saveBuku(list);
        JOptionPane.showMessageDialog(this, "Buku ditambahkan!");
        loadBukuTable();
    }

    private void loadBukuTable() {
        modelBuku.setRowCount(0);
        List<Buku> list = CSVManager.loadBuku();
        for (Buku b : list) {
            modelBuku.addRow(new Object[]{
                    b.getKodeBuku(), b.getTitle(), b.getAuthor(),
                    b.getIsbn(), b.getStock(), b.getAvgRating()
            });
        }
    }

    // Implementasi serupa untuk tab lain...
    private JPanel createKelolaAnggota() {
        JPanel panel = new JPanel(new BorderLayout());
        modelAnggota = new DefaultTableModel(new String[]{"ID", "Username", "Nama", "Status"}, 0);
        JTable table = new JTable(modelAnggota);
        JScrollPane scroll = new JScrollPane(table);
        panel.add(scroll);
        loadAnggotaTable();
        return panel;
    }

    private void loadAnggotaTable() {
        modelAnggota.setRowCount(0);
        List<Anggota> list = CSVManager.loadAnggota();
        Map<String, Pelanggaran> pelanggaran = CSVManager.loadPelanggaran();
        for (Anggota a : list) {
            Pelanggaran p = pelanggaran.get(a.getId());
            String status = p != null ? p.getStatus() : "aman";
            modelAnggota.addRow(new Object[]{a.getId(), a.getUsername(), a.getNama(), status});
        }
    }

    private JPanel createPeminjaman() {
        JPanel panel = new JPanel(new BorderLayout());
        modelPeminjaman = new DefaultTableModel(new String[]{"ID", "Anggota", "Buku", "Pinjam", "Batas", "Status"}, 0);
        JTable table = new JTable(modelPeminjaman);
        JScrollPane scroll = new JScrollPane(table);
        panel.add(scroll);
        loadPeminjamanTable();
        return panel;
    }

    private void loadPeminjamanTable() {
        modelPeminjaman.setRowCount(0);
        List<Peminjaman> list = CSVManager.loadPeminjaman();
        for (Peminjaman p : list) {
            modelPeminjaman.addRow(new Object[]{
                    p.getId(), p.getAnggotaId(), p.getBukuId(),
                    p.getTglPinjam(), p.getTglKembali(), p.getStatus()
            });
        }
    }
}

