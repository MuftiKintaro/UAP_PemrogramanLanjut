package UAPPemlan;

import java.io.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.List;

public class CSVManager {
    private static final DateTimeFormatter DF = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final String DATA_PATH = "data/";
    private static final String ANGGOTA_FILE = DATA_PATH + "anggota.csv";

    public static List<Buku> loadBuku() {
        List<Buku> list = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(DATA_PATH + "buku.csv"))) {
            String line;
            boolean first = true;
            while ((line = br.readLine()) != null) {
                if (first) { first = false; continue; }
                Buku buku = Buku.fromCSV(line);
                if (buku != null) list.add(buku);
            }
        } catch (IOException e) {
            createSampleData();
            return loadBuku();
        }
        return list;
    }

    public static void saveBuku(List<Buku> list) {
        try (PrintWriter pw = new PrintWriter(new FileWriter(DATA_PATH + "buku.csv"))) {
            pw.println("kode_buku,title,author,isbn,stock,avg_rating");
            for (Buku b : list) pw.println(b.toCSV());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static List<Anggota> loadAnggota() {
        List<Anggota> list = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new FileReader(ANGGOTA_FILE))) {
            String line;

            // skip header
            br.readLine();

            while ((line = br.readLine()) != null) {

                // SKIP baris kosong
                if (line.trim().isEmpty()) {
                    continue;
                }

                String[] data = line.split(",");

                // VALIDASI JUMLAH KOLOM
                if (data.length < 5) {
                    System.err.println("Baris anggota tidak valid: " + line);
                    continue;
                }

                list.add(new Anggota(
                        data[0].trim(), // id
                        data[1].trim(), // nama
                        data[2].trim(), // username
                        data[3].trim(), // password
                        data[4].trim()  // status
                ));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return list;
    }

    public static String generateIdAnggota() {
        List<Anggota> list = loadAnggota();
        int next = list.size() + 1;
        return String.format("A%03d", next);
    }

    public static boolean tambahAnggota(Anggota a) {
        for (Anggota ag : loadAnggota()) {
            if (ag.getUsername().equalsIgnoreCase(a.getUsername())) {
                return false;
            }
        }

        try (BufferedWriter bw = new BufferedWriter(new FileWriter(ANGGOTA_FILE, true))) {
            bw.newLine();
            bw.write(String.join(",",
                    a.getId(),
                    a.getNama(),
                    a.getUsername(),
                    a.getPassword(),
                    a.getStatus()
            ));
            return true;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static void saveAnggota(List<Anggota> list) {
        try (PrintWriter pw = new PrintWriter(new FileWriter(DATA_PATH + "anggota.csv"))) {
            pw.println("id,username,password,nama,alamat");
            for (Anggota a : list) pw.println(a.toCSV());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static List<Peminjaman> loadPeminjaman() {
        List<Peminjaman> list = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new FileReader(DATA_PATH + "peminjaman.csv"))) {
            String line;
            boolean first = true;

            while ((line = br.readLine()) != null) {
                if (first) {
                    first = false;
                    continue;   // lewati header
                }

                Peminjaman p = Peminjaman.fromCSV(line);
                if (p != null) {
                    list.add(p);
                }
            }

        } catch (IOException e) {
            // Jika file belum ada / gagal dibaca:
            // Optional: buat sample data sekali saja
            createSampleData();   // boleh dihapus kalau tidak ingin auto-sample
            // JANGAN memanggil loadPeminjaman() lagi di sini
        }

        return list;
    }

    public static void savePeminjaman(List<Peminjaman> list) {
        try (PrintWriter pw = new PrintWriter(new FileWriter(DATA_PATH + "peminjaman.csv"))) {
            pw.println("id,anggota_id,buku_id,tgl_pinjam,tgl_batas_kembali,tgl_kembali,status,rating_kondisi,rating_rekomendasi");
            for (Peminjaman p : list) pw.println(p.toCSV());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static Map<String, Pelanggaran> loadPelanggaran() {
        Map<String, Pelanggaran> map = new HashMap<>();
        try (BufferedReader br = new BufferedReader(new FileReader(DATA_PATH + "pelanggaran.csv"))) {
            String line;
            boolean first = true;
            while ((line = br.readLine()) != null) {
                if (first) { first = false; continue; }
                String[] parts = line.split(",");
                if (parts.length >= 1) {
                    Pelanggaran pel = new Pelanggaran(parts[0]);
                    if (parts.length >= 2) pel.setJumlahTelat(Integer.parseInt(parts[1]));
                    if (parts.length >= 3) pel.setStatus(parts[2]);
                    map.put(parts[0], pel);
                }
            }
        } catch (IOException e) {
            // empty
        }
        return map;
    }

    public static void savePelanggaran(Map<String, Pelanggaran> map) {
        try (PrintWriter pw = new PrintWriter(new FileWriter(DATA_PATH + "pelanggaran.csv"))) {
            pw.println("user_id,jumlah_telat,status,hari_suspend_berakhir");
            for (Pelanggaran p : map.values()) {
                pw.println(p.toCSV());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String generateKodeBuku(char kategori) {
        List<Buku> buku = loadBuku();
        int max = 0;
        for (Buku b : buku) {
            if (b.getKodeBuku().startsWith(String.valueOf(kategori))) {
                try {
                    int num = Integer.parseInt(b.getKodeBuku().substring(1));
                    max = Math.max(max, num);
                } catch (NumberFormatException ignored) {}
            }
        }
        return String.format("%c%03d", kategori, max + 1);
    }

    public static boolean pinjamBuku(String userId, String kodeBuku) {
        List<Buku> bukuList = loadBuku();
        List<Peminjaman> pinjamList = loadPeminjaman();
        Map<String, Pelanggaran> pelanggaranMap = loadPelanggaran();

        // Cek pelanggaran
        Pelanggaran pel = pelanggaranMap.getOrDefault(userId, new Pelanggaran(userId));
        if (pel.isSuspend() || pel.isBlacklist()) return false;

        // Cek stok
        Buku targetBuku = null;
        for (Buku b : bukuList) {
            if (b.getKodeBuku().equals(kodeBuku)) {
                targetBuku = b;
                break;
            }
        }
        if (targetBuku == null || targetBuku.getStock() <= 0) return false;

        // Buat peminjaman baru
        String idPinjam = "P" + String.format("%04d", pinjamList.size() + 1);
        LocalDate now = LocalDate.now();
        LocalDate batas = now.plusDays(7);
        Peminjaman pinjam = new Peminjaman(idPinjam, userId, kodeBuku,
                now.format(DF), batas.format(DF));
        pinjamList.add(pinjam);

        // Update stok
        targetBuku.setStock(targetBuku.getStock() - 1);
        saveBuku(bukuList);
        savePeminjaman(pinjamList);
        savePelanggaran(pelanggaranMap);
        return true;
    }

    public static boolean prosesKembali(String pinjamId, String tglKembaliStr) {
        List<Peminjaman> pinjamList = loadPeminjaman();
        List<Buku> bukuList = loadBuku();
        Map<String, Pelanggaran> pelanggaranMap = loadPelanggaran();

        Peminjaman target = null;
        for (Peminjaman p : pinjamList) {
            if (p.getId().equals(pinjamId)) {
                target = p;
                break;
            }
        }
        if (target == null || !"active".equals(target.getStatus())) return false;

        target.setTglKembali(tglKembaliStr);
        target.setStatus("returned");

        // Cek telat
        LocalDate batas = LocalDate.parse(target.getTglKembali(), DF);
        LocalDate kembali = LocalDate.parse(tglKembaliStr, DF);
        if (kembali.isAfter(batas)) {
            Pelanggaran pel = pelanggaranMap.getOrDefault(target.getAnggotaId(),
                    new Pelanggaran(target.getAnggotaId()));
            pel.tambahPelanggaran();
            pelanggaranMap.put(target.getAnggotaId(), pel);
        }

        // Tambah stok
        for (Buku b : bukuList) {
            if (b.getKodeBuku().equals(target.getBukuId())) {
                b.setStock(b.getStock() + 1);
                break;
            }
        }

        savePeminjaman(pinjamList);
        saveBuku(bukuList);
        savePelanggaran(pelanggaranMap);
        return true;
    }

    public static void createSampleData() {
        // Buat data sample
        List<Buku> sampleBuku = Arrays.asList(
                new Buku("A001", "Pengantar Pemrograman", "John Doe", "1234567890", 5),
                new Buku("B001", "Al-Quran Terjemah", "Kemenag", "0987654321", 3)
        );
        saveBuku(sampleBuku);

        List<Anggota> sampleAnggota = Arrays.asList(
                new Anggota("1", "admin", "admin123", "Admin Perpustakaan", "UMM"),
                new Anggota("2", "user1", "user123", "Andi", "Malang")
        );
        saveAnggota(sampleAnggota);
    }
    public static boolean hapusAnggota(String anggotaId) {
        if (anggotaPunyaPinjamanAktif(anggotaId)) {
            return false;
        }

        List<Anggota> list = loadAnggota();

        try (BufferedWriter bw = new BufferedWriter(new FileWriter(ANGGOTA_FILE))) {
            bw.write("id,nama,username,password,status");
            for (Anggota a : list) {
                if (!a.getId().equals(anggotaId)) {
                    bw.newLine();
                    bw.write(String.join(",",
                            a.getId(), a.getNama(), a.getUsername(), a.getPassword(), a.getStatus()
                    ));
                }
            }
            return true;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static boolean anggotaPunyaPinjamanAktif(String anggotaId) {
        for (Peminjaman p : loadPeminjaman()) {
            if (p.getAnggotaId().equals(anggotaId)
                    && p.getStatus().equals("active")) {
                return true;
            }
        }
        return false;
    }
}