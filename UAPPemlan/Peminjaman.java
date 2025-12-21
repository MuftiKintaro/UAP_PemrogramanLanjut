package UAPPemlan;

public class Peminjaman {

    private String id;
    private String anggotaId;
    private String bukuId;

    private String tglPinjam;
    private String tglBatasKembali;
    private String tglKembali;

    private String status; // active / returned
    private String ratingKondisi;
    private String ratingRekomendasi;

    // =========================
    // CONSTRUCTOR
    // =========================
    public Peminjaman(String id, String anggotaId, String bukuId,
                      String tglPinjam, String tglBatasKembali) {
        this.id = id;
        this.anggotaId = anggotaId;
        this.bukuId = bukuId;
        this.tglPinjam = tglPinjam;
        this.tglBatasKembali = tglBatasKembali;
        this.status = "active";
        this.tglKembali = "";
        this.ratingKondisi = "";
        this.ratingRekomendasi = "";
    }

    // =========================
    // GETTER
    // =========================
    public String getId() {
        return id;
    }

    public String getAnggotaId() {
        return anggotaId;
    }

    public String getBukuId() {
        return bukuId;
    }

    public String getTglPinjam() {
        return tglPinjam;
    }

    public String getTglBatasKembali() {
        return tglBatasKembali;
    }

    public String getTglKembali() {
        return tglKembali;
    }

    public String getStatus() {
        return status;
    }

    public String getRatingKondisi() {
        return ratingKondisi;
    }

    public String getRatingRekomendasi() {
        return ratingRekomendasi;
    }

    // =========================
    // SETTER
    // =========================
    public void setTglKembali(String tglKembali) {
        this.tglKembali = tglKembali;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setRatingKondisi(String ratingKondisi) {
        this.ratingKondisi = ratingKondisi;
    }

    public void setRatingRekomendasi(String ratingRekomendasi) {
        this.ratingRekomendasi = ratingRekomendasi;
    }

    // =========================
    // CSV HANDLER
    // =========================
    public String toCSV() {
        return id + "," +
                anggotaId + "," +
                bukuId + "," +
                tglPinjam + "," +
                tglBatasKembali + "," +
                tglKembali + "," +
                status + "," +
                ratingKondisi + "," +
                ratingRekomendasi;
    }

    public static Peminjaman fromCSV(String line) {
        String[] p = line.split(",");

        if (p.length < 5) return null;

        Peminjaman pj = new Peminjaman(
                p[0], // id
                p[1], // anggotaId
                p[2], // bukuId
                p[3], // tglPinjam
                p[4]  // tglBatasKembali
        );

        if (p.length > 5) pj.tglKembali = p[5];
        if (p.length > 6) pj.status = p[6];
        if (p.length > 7) pj.ratingKondisi = p[7];
        if (p.length > 8) pj.ratingRekomendasi = p[8];

        return pj;
    }
}
