package UAPPemlan;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;
import java.util.Map;

public class AdminFrame extends JFrame {

    private DefaultTableModel modelBuku;
    private DefaultTableModel modelAnggota;
    private DefaultTableModel modelPeminjaman;

    public AdminFrame() {
        setTitle("Admin Dashboard - Perpustakaan UMM");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1000, 650);
        setLocationRelativeTo(null);

        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.addTab("Dashboard", createDashboardPanel());
        tabbedPane.addTab("Kelola Buku", createKelolaBukuPanel());
        tabbedPane.addTab("Kelola Anggota", createKelolaAnggotaPanel());
        tabbedPane.addTab("Data Peminjaman", createPeminjamanPanel());

        add(tabbedPane);
        setVisible(true);
    }

    // =============================
    // DASHBOARD
    // =============================
    private JPanel createDashboardPanel() {
        JPanel panel = new JPanel(new GridLayout(2, 2, 20, 20));
        panel.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));

        JLabel lblBuku = new JLabel();
        JLabel lblAnggota = new JLabel();
        JLabel lblPinjam = new JLabel();
        JLabel lblPelanggaran = new JLabel();

        updateDashboard(lblBuku, lblAnggota, lblPinjam, lblPelanggaran);

        panel.add(lblBuku);
        panel.add(lblAnggota);
        panel.add(lblPinjam);
        panel.add(lblPelanggaran);

        return panel;
    }

    private void updateDashboard(JLabel buku, JLabel anggota, JLabel pinjam, JLabel pelanggaran) {
        buku.setText("📚 Total Buku: " + CSVManager.loadBuku().size());
        anggota.setText("👤 Total Anggota: " + CSVManager.loadAnggota().size());

        long aktif = CSVManager.loadPeminjaman()
                .stream()
                .filter(p -> "active".equals(p.getStatus()))
                .count();

        pinjam.setText("📄 Peminjaman Aktif: " + aktif);
        pelanggaran.setText("⚠️ Total Pelanggaran: " + CSVManager.loadPelanggaran().size());
    }

    // =============================
    // KELOLA BUKU
    // =============================
    private JPanel createKelolaBukuPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));

        JTextField txtKode = new JTextField(10);
        txtKode.setEditable(false);

        JTextField txtJudul = new JTextField(15);
        JTextField txtPenulis = new JTextField(15);
        JTextField txtISBN = new JTextField(15);
        JTextField txtStok = new JTextField(5);

        JComboBox<String> cbKategori = new JComboBox<>(
                new String[]{"A","B","C","D","E","F","G","H","I","J"}
        );

        JButton btnGenerate = new JButton("Generate Kode");
        JButton btnTambah = new JButton("Tambah Buku");

        btnGenerate.addActionListener(e -> {
            char kategori = cbKategori.getSelectedItem().toString().charAt(0);
            txtKode.setText(CSVManager.generateKodeBuku(kategori));
        });

        btnTambah.addActionListener(e ->
                tambahBuku(txtKode, txtJudul, txtPenulis, txtISBN, txtStok)
        );

        JPanel form = new JPanel(new GridLayout(6, 2, 10, 10));
        form.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        form.add(new JLabel("Kategori"));
        form.add(cbKategori);
        form.add(new JLabel("Kode Buku"));
        form.add(txtKode);
        form.add(new JLabel("Judul"));
        form.add(txtJudul);
        form.add(new JLabel("Penulis"));
        form.add(txtPenulis);
        form.add(new JLabel("ISBN"));
        form.add(txtISBN);
        form.add(new JLabel("Stok"));
        form.add(txtStok);

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(btnGenerate);
        buttonPanel.add(btnTambah);

        modelBuku = new DefaultTableModel(
                new String[]{"Kode", "Judul", "Penulis", "ISBN", "Stok", "Rating"}, 0
        );
        JTable table = new JTable(modelBuku);
        JScrollPane scroll = new JScrollPane(table);

        panel.add(form, BorderLayout.NORTH);
        panel.add(buttonPanel, BorderLayout.CENTER);
        panel.add(scroll, BorderLayout.SOUTH);

        loadBukuTable();
        return panel;
    }

    private void tambahBuku(JTextField kode, JTextField judul,
                            JTextField penulis, JTextField isbn, JTextField stok) {

        if (kode.getText().isEmpty() || judul.getText().isEmpty()
                || penulis.getText().isEmpty() || isbn.getText().isEmpty()
                || stok.getText().isEmpty()) {

            JOptionPane.showMessageDialog(this, "Semua field harus diisi!");
            return;
        }

        int stokInt;
        try {
            stokInt = Integer.parseInt(stok.getText());
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Stok harus angka!");
            return;
        }

        Buku buku = new Buku(
                kode.getText(),
                judul.getText(),
                penulis.getText(),
                isbn.getText(),
                stokInt
        );

        List<Buku> list = CSVManager.loadBuku();
        list.add(buku);
        CSVManager.saveBuku(list);

        JOptionPane.showMessageDialog(this, "Buku berhasil ditambahkan!");
        loadBukuTable();

        kode.setText("");
        judul.setText("");
        penulis.setText("");
        isbn.setText("");
        stok.setText("");
    }

    private void loadBukuTable() {
        modelBuku.setRowCount(0);
        for (Buku b : CSVManager.loadBuku()) {
            modelBuku.addRow(new Object[]{
                    b.getKodeBuku(),
                    b.getTitle(),
                    b.getAuthor(),
                    b.getIsbn(),
                    b.getStock(),
                    String.format("%.1f", b.getAvgRating())
            });
        }
    }

    // =============================
    // KELOLA ANGGOTA
    // =============================
    private JPanel createKelolaAnggotaPanel() {
        JPanel panel = new JPanel(new BorderLayout());

        modelAnggota = new DefaultTableModel(
                new String[]{"ID", "Username", "Nama", "Status"}, 0
        );
        JTable table = new JTable(modelAnggota);
        JScrollPane scroll = new JScrollPane(table);

        panel.add(scroll);
        loadAnggotaTable();

        return panel;
    }

    private void loadAnggotaTable() {
        modelAnggota.setRowCount(0);
        Map<String, Pelanggaran> pelMap = CSVManager.loadPelanggaran();

        for (Anggota a : CSVManager.loadAnggota()) {
            Pelanggaran p = pelMap.get(a.getId());
            String status = (p != null) ? p.getStatus() : "aman";

            modelAnggota.addRow(new Object[]{
                    a.getId(),
                    a.getUsername(),
                    a.getNama(),
                    status
            });
        }
    }

    // =============================
    // DATA PEMINJAMAN
    // =============================
    private JPanel createPeminjamanPanel() {
        JPanel panel = new JPanel(new BorderLayout());

        modelPeminjaman = new DefaultTableModel(
                new String[]{"ID", "Anggota", "Buku", "Pinjam", "Batas", "Status"}, 0
        );
        JTable table = new JTable(modelPeminjaman);
        JScrollPane scroll = new JScrollPane(table);

        panel.add(scroll);
        loadPeminjamanTable();

        return panel;
    }

    private void loadPeminjamanTable() {
        modelPeminjaman.setRowCount(0);
        for (Peminjaman p : CSVManager.loadPeminjaman()) {
            modelPeminjaman.addRow(new Object[]{
                    p.getId(),
                    p.getAnggotaId(),
                    p.getBukuId(),
                    p.getTglPinjam(),
                    p.getTglBatasKembali(),
                    p.getStatus()
            });
        }
    }
}
