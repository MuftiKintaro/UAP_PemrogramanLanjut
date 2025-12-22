package UAPPemlan;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDate;
import java.util.List;

public class UserFrame extends JFrame {

    private Anggota user;

    private JTable tableBuku;
    private JTable tableRiwayat;

    private DefaultTableModel modelBuku;
    private DefaultTableModel modelRiwayat;

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

        tabbedPane.addTab("Cari Buku", createCariBukuPanel());
        tabbedPane.addTab("Riwayat Peminjaman", createRiwayatPanel());

        add(tabbedPane);
        setVisible(true);
    }

    // ==============================
    // TAB CARI BUKU
    // ==============================
    private JPanel createCariBukuPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        // =========================
        // SEARCH PANEL (ATAS)
        // =========================
        JLabel lblCari = new JLabel("Cari Buku:");
        JTextField txtCari = new JTextField(20);
        JButton btnCari = new JButton("Cari");

        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        searchPanel.add(lblCari);
        searchPanel.add(txtCari);
        searchPanel.add(btnCari);

        // =========================
        // TABLE BUKU (TENGAH)
        // =========================
        modelBuku = new DefaultTableModel(
                new String[]{"Kode", "Judul", "Penulis", "Stok", "Rating"}, 0
        );
        tableBuku = new JTable(modelBuku);
        tableBuku.setRowHeight(26);
        tableBuku.setFillsViewportHeight(true);

        JScrollPane scrollPane = new JScrollPane(tableBuku);
        scrollPane.setBorder(BorderFactory.createTitledBorder("Daftar Buku"));

        // =========================
        // ACTION PANEL (BAWAH)
        // =========================
        JButton btnPinjam = new JButton("Pinjam Buku");
        btnPinjam.setPreferredSize(new Dimension(140, 30));

        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        actionPanel.add(btnPinjam);

        // =========================
        // ADD KE PANEL UTAMA
        // =========================
        panel.add(searchPanel, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);
        panel.add(actionPanel, BorderLayout.SOUTH);

        // =========================
        // EVENT
        // =========================
        btnCari.addActionListener(e -> loadBuku(txtCari.getText()));
        btnPinjam.addActionListener(e -> pinjamBuku());

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
                        b.getKodeBuku(),
                        b.getTitle(),
                        b.getAuthor(),
                        b.getStock(),
                        String.format("%.1f", b.getAvgRating())
                });
            }
        }
    }

    private void pinjamBuku() {
        int row = tableBuku.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Pilih buku terlebih dahulu!");
            return;
        }

        String kodeBuku = modelBuku.getValueAt(row, 0).toString();

        int confirm = JOptionPane.showConfirmDialog(
                this,
                "Apakah Anda yakin ingin meminjam buku ini?",
                "Konfirmasi Peminjaman",
                JOptionPane.YES_NO_OPTION
        );

        if (confirm != JOptionPane.YES_OPTION) return;

        // ==== CEK STOK ====
        int stok = Integer.parseInt(modelBuku.getValueAt(row, 3).toString());
        if (stok <= 0) {
            JOptionPane.showMessageDialog(this,
                    "Gagal meminjam buku.\nStok buku sudah habis.");
            return;
        }

        // ==== PROSES PINJAM ====
        boolean success = CSVManager.pinjamBuku(user.getId(), kodeBuku);

        if (success) {
            JOptionPane.showMessageDialog(this,
                    "Buku berhasil dipinjam.\nSilakan kembalikan sebelum batas waktu.");
            loadBuku("");
            loadRiwayat();
        } else {
            JOptionPane.showMessageDialog(this,
                    "Gagal meminjam buku.\nAkun Anda sedang dibatasi (suspend/blacklist).");
        }
    }


    // ==============================
    // TAB RIWAYAT
    // ==============================
    private JPanel createRiwayatPanel() {
        JPanel panel = new JPanel(new BorderLayout());

        modelRiwayat = new DefaultTableModel(
                new String[]{"ID", "Buku", "Tanggal Pinjam", "Batas Kembali", "Status"}, 0
        );
        tableRiwayat = new JTable(modelRiwayat);
        JScrollPane scroll = new JScrollPane(tableRiwayat);

        JButton btnKembali = new JButton("Kembalikan Buku");

        JPanel bottomPanel = new JPanel();
        bottomPanel.add(btnKembali);

        panel.add(scroll, BorderLayout.CENTER);
        panel.add(bottomPanel, BorderLayout.SOUTH);

        btnKembali.addActionListener(e -> kembalikanBuku());

        loadRiwayat();
        return panel;
    }

    private void loadRiwayat() {
        modelRiwayat.setRowCount(0);
        List<Peminjaman> list = CSVManager.loadPeminjaman();

        for (Peminjaman p : list) {
            if (p.getAnggotaId().equals(user.getId())) {
                modelRiwayat.addRow(new Object[]{
                        p.getId(),
                        p.getBukuId(),
                        p.getTglPinjam(),
                        p.getTglBatasKembali(),
                        p.getStatus()
                });
            }
        }
    }

    private void kembalikanBuku() {
        int row = tableRiwayat.getSelectedRow();

        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Pilih data peminjaman terlebih dahulu!");
            return;
        }

        String pinjamId = modelRiwayat.getValueAt(row, 0).toString();
        String today = java.time.LocalDate.now().toString();

        int confirm = JOptionPane.showConfirmDialog(
                this,
                "Apakah Anda yakin ingin mengembalikan buku ini?",
                "Konfirmasi Pengembalian",
                JOptionPane.YES_NO_OPTION
        );

        if (confirm != JOptionPane.YES_OPTION) {
            return;
        }

        boolean success = CSVManager.prosesKembali(pinjamId, today);

        if (success) {
            JOptionPane.showMessageDialog(this, "Buku berhasil dikembalikan!");
            loadRiwayat();
            loadBuku("");
        } else {
            JOptionPane.showMessageDialog(this, "Gagal mengembalikan buku!");
        }
    }
}
