package UAPPemlan;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class Peminjaman {
    private String id, anggotaId, bukuId, tglPinjam, tglBatasKembali, tglKembali, status;
    private String ratingKondisi, ratingRekomendasi;

    public Peminjaman(String id, String anggotaId, String bukuId, String tglPinjam, String tglBatasKembali) {
        this.id = id;
        this.anggotaId = anggotaId;
        this.bukuId = bukuId;
        this.tglPinjam = tglPinjam;
        this.tglBatasKembali = tglBatasKembali;
        this.status = "active";
    }

    // Getters & Setters
    public String getId() { return id; }
    public String getAnggotaId() { return anggotaId; }
    public String getBukuId() { return bukuId; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getTglPinjam() { return tglPinjam; }
    public String getRatingKondisi() { return ratingKondisi; }
    public void setRatingKondisi(String ratingKondisi) { this.ratingKondisi = ratingKondisi; }
    public String getRatingRekomendasi() { return ratingRekomendasi; }
    public void setRatingRekomendasi(String ratingRekomendasi) { this.ratingRekomendasi = ratingRekomendasi; }
    public String getTglKembali() { return tglKembali; }
    public void setTglKembali(String tglKembali) { this.tglKembali = tglKembali; }

    public String toCSV() {
        return String.format("%s,%s,%s,%s,%s,%s,%s,%s,%s",
                id, anggotaId, bukuId, tglPinjam, tglBatasKembali, tglKembali, status,
                ratingKondisi, ratingRekomendasi);
    }

    public static Peminjaman fromCSV(String line) {
        String[] parts = line.split(",");
        if (parts.length >= 9) {
            Peminjaman p = new Peminjaman(parts[0], parts[1], parts[2], parts[3], parts[4]);
            p.setTglKembali(parts[5]);
            p.setStatus(parts[6]);
            p.setRatingKondisi(parts[7]);
            p.setRatingRekomendasi(parts[8]);
            return p;
        }
        return null;
    }
}
