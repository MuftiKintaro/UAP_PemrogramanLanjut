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
        setJMenuBar(createMenuBar());

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

        int totalBuku = CSVManager.loadBuku().size();
        int totalAnggota = CSVManager.loadAnggota().size();

        long peminjamanAktif = CSVManager.loadPeminjaman()
                .stream()
                .filter(p -> "active".equals(p.getStatus()))
                .count();

        int totalPelanggaran = CSVManager.loadPelanggaran().size();

        panel.add(createInfoCard("📚 Total Buku", totalBuku));
        panel.add(createInfoCard("👤 Total Anggota", totalAnggota));
        panel.add(createInfoCard("📄 Peminjaman Aktif", peminjamanAktif));
        panel.add(createInfoCard("⚠️ Pelanggaran", totalPelanggaran));

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

    private JPanel createInfoCard(String title, long value) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.LIGHT_GRAY),
                BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));

        JLabel lblTitle = new JLabel(title);
        lblTitle.setFont(new Font("SansSerif", Font.BOLD, 16));

        JLabel lblValue = new JLabel(String.valueOf(value));
        lblValue.setFont(new Font("SansSerif", Font.BOLD, 32));
        lblValue.setHorizontalAlignment(SwingConstants.CENTER);

        card.add(lblTitle, BorderLayout.NORTH);
        card.add(lblValue, BorderLayout.CENTER);

        return card;
    }

    // =============================
    // KELOLA BUKU
    // =============================
    private JPanel createKelolaBukuPanel() {
        JPanel panel = new JPanel(new BorderLayout(15, 15));
        panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        // =============================
        // FORM INPUT (KIRI)
        // =============================
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createTitledBorder("Form Buku"));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JTextField txtKode = new JTextField(12);
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

        // Row 0
        gbc.gridx = 0; gbc.gridy = 0;
        formPanel.add(new JLabel("Kategori"), gbc);
        gbc.gridx = 1;
        formPanel.add(cbKategori, gbc);

        // Row 1
        gbc.gridx = 0; gbc.gridy++;
        formPanel.add(new JLabel("Kode Buku"), gbc);
        gbc.gridx = 1;
        formPanel.add(txtKode, gbc);

        // Row 2
        gbc.gridx = 0; gbc.gridy++;
        formPanel.add(new JLabel("Judul"), gbc);
        gbc.gridx = 1;
        formPanel.add(txtJudul, gbc);

        // Row 3
        gbc.gridx = 0; gbc.gridy++;
        formPanel.add(new JLabel("Penulis"), gbc);
        gbc.gridx = 1;
        formPanel.add(txtPenulis, gbc);

        // Row 4
        gbc.gridx = 0; gbc.gridy++;
        formPanel.add(new JLabel("ISBN"), gbc);
        gbc.gridx = 1;
        formPanel.add(txtISBN, gbc);

        // Row 5
        gbc.gridx = 0; gbc.gridy++;
        formPanel.add(new JLabel("Stok"), gbc);
        gbc.gridx = 1;
        formPanel.add(txtStok, gbc);

        // Row 6 - Buttons
        gbc.gridx = 0; gbc.gridy++;
        formPanel.add(btnGenerate, gbc);
        gbc.gridx = 1;
        formPanel.add(btnTambah, gbc);

        // =============================
        // EVENT
        // =============================
        btnGenerate.addActionListener(e -> {
            char kategori = cbKategori.getSelectedItem().toString().charAt(0);
            txtKode.setText(CSVManager.generateKodeBuku(kategori));
        });

        btnTambah.addActionListener(e ->
                tambahBuku(txtKode, txtJudul, txtPenulis, txtISBN, txtStok)
        );

        // =============================
        // TABEL BUKU (KANAN)
        // =============================
        modelBuku = new DefaultTableModel(
                new String[]{"Kode", "Judul", "Penulis", "ISBN", "Stok", "Rating"}, 0
        );
        JTable table = new JTable(modelBuku);
        table.setRowHeight(26);
        table.setFillsViewportHeight(true);

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createTitledBorder("Daftar Buku"));

        // =============================
        // SPLIT PANEL
        // =============================
        JSplitPane splitPane = new JSplitPane(
                JSplitPane.HORIZONTAL_SPLIT,
                formPanel,
                scrollPane
        );
        splitPane.setDividerLocation(330);
        splitPane.setResizeWeight(0);

        panel.add(splitPane, BorderLayout.CENTER);

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
        JPanel panel = new JPanel(new BorderLayout(10,10));
        panel.setBorder(BorderFactory.createEmptyBorder(15,15,15,15));

        // FORM
        JTextField txtNama = new JTextField(15);
        JTextField txtUsername = new JTextField(15);
        JTextField txtPassword = new JTextField(15);

        JButton btnTambah = new JButton("Tambah Anggota");

        JPanel form = new JPanel(new GridLayout(4,2,10,10));
        form.setBorder(BorderFactory.createTitledBorder("Form Anggota"));
        form.add(new JLabel("Nama"));
        form.add(txtNama);
        form.add(new JLabel("Username"));
        form.add(txtUsername);
        form.add(new JLabel("Password"));
        form.add(txtPassword);
        form.add(new JLabel());
        form.add(btnTambah);

        // TABLE
        DefaultTableModel model = new DefaultTableModel(
                new String[]{"ID","Nama","Username","Status"},0
        );
        JTable table = new JTable(model);
        JScrollPane scroll = new JScrollPane(table);

        JButton btnHapus = new JButton("Hapus Anggota");

        JPanel right = new JPanel(new BorderLayout(5,5));
        right.add(scroll, BorderLayout.CENTER);
        right.add(btnHapus, BorderLayout.SOUTH);

        panel.add(form, BorderLayout.WEST);
        panel.add(right, BorderLayout.CENTER);

        // LOAD
        Runnable load = () -> {
            model.setRowCount(0);
            for (Anggota a : CSVManager.loadAnggota()) {
                model.addRow(new Object[]{
                        a.getId(), a.getNama(), a.getUsername(), a.getStatus()
                });
            }
        };
        load.run();

        // EVENT TAMBAH
        btnTambah.addActionListener(e -> {

            String nama = txtNama.getText().trim();
            String username = txtUsername.getText().trim();
            String password = txtPassword.getText().trim();

            // VALIDASI INPUT KOSONG
            if (nama.isEmpty() || username.isEmpty() || password.isEmpty()) {
                JOptionPane.showMessageDialog(
                        this,
                        "Nama, Username, dan Password tidak boleh kosong!",
                        "Input Tidak Valid",
                        JOptionPane.ERROR_MESSAGE
                );
                return;
            }

            String id = CSVManager.generateIdAnggota();

            Anggota a = new Anggota(
                    id,
                    nama,
                    username,
                    password,
                    "aktif"
            );

            if (CSVManager.tambahAnggota(a)) {
                JOptionPane.showMessageDialog(this,"Anggota berhasil ditambahkan");

                // reset field
                txtNama.setText("");
                txtUsername.setText("");
                txtPassword.setText("");

                loadAnggotaTable();
            } else {
                JOptionPane.showMessageDialog(
                        this,
                        "Username sudah digunakan",
                        "Gagal",
                        JOptionPane.WARNING_MESSAGE
                );
            }
        });

        // EVENT HAPUS
        btnHapus.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row < 0) return;

            String id = model.getValueAt(row,0).toString();

            if (!CSVManager.hapusAnggota(id)) {
                JOptionPane.showMessageDialog(this,
                        "Anggota tidak dapat dihapus karena masih memiliki pinjaman aktif");
            } else {
                JOptionPane.showMessageDialog(this,"Anggota berhasil dihapus");
                load.run();
            }
        });

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
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        // =============================
        // JUDUL
        // =============================
        JLabel lblTitle = new JLabel("Data Peminjaman Buku");
        lblTitle.setFont(new Font("SansSerif", Font.BOLD, 18));

        JPanel titlePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        titlePanel.add(lblTitle);

        // =============================
        // TABEL PEMINJAMAN
        // =============================
        modelPeminjaman = new DefaultTableModel(
                new String[]{"ID", "Anggota", "Buku", "Tgl Pinjam", "Batas", "Status"}, 0
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // tabel read-only
            }
        };

        JTable table = new JTable(modelPeminjaman);
        table.setRowHeight(26);
        table.setFillsViewportHeight(true);

        // Lebar kolom (biar enak dibaca)
        table.getColumnModel().getColumn(0).setPreferredWidth(80);   // ID
        table.getColumnModel().getColumn(1).setPreferredWidth(120);  // Anggota
        table.getColumnModel().getColumn(2).setPreferredWidth(120);  // Buku
        table.getColumnModel().getColumn(3).setPreferredWidth(100);  // Pinjam
        table.getColumnModel().getColumn(4).setPreferredWidth(100);  // Batas
        table.getColumnModel().getColumn(5).setPreferredWidth(80);   // Status

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createTitledBorder("Daftar Peminjaman"));

        // =============================
        // INFO PANEL (BAWAH)
        // =============================
        JLabel lblInfo = new JLabel(
                "Keterangan: Status 'active' = masih dipinjam, 'returned' = sudah dikembalikan"
        );
        lblInfo.setFont(new Font("SansSerif", Font.ITALIC, 12));

        JPanel infoPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        infoPanel.add(lblInfo);

        // =============================
        // ADD KE PANEL UTAMA
        // =============================
        panel.add(titlePanel, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);
        panel.add(infoPanel, BorderLayout.SOUTH);

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

    private JMenuBar createMenuBar() {
        JMenuBar menuBar = new JMenuBar();

        JMenu menuAkun = new JMenu("Akun");
        JMenuItem menuLogout = new JMenuItem("Logout");

        menuLogout.addActionListener(e -> logout());

        menuAkun.add(menuLogout);
        menuBar.add(menuAkun);

        return menuBar;
    }

    private void logout() {
        int confirm = JOptionPane.showConfirmDialog(
                this,
                "Apakah Anda yakin ingin logout?",
                "Konfirmasi Logout",
                JOptionPane.YES_NO_OPTION
        );

        if (confirm == JOptionPane.YES_OPTION) {
            dispose();
            new LoginFrame();
        }
    }
}
