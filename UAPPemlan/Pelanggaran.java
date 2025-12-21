package UAPPemlan;

import java.time.LocalDate;

public class Pelanggaran {
    private String userId;
    private int jumlahTelat;
    private String status; // "aman", "peringatan", "suspend", "blacklist"
    private String hariSuspendBerakhir;

    public Pelanggaran(String userId) {
        this.userId = userId;
        this.jumlahTelat = 0;
        this.status = "aman";
    }

    public String getStatus() {
        return status;
    }

    public void tambahPelanggaran() {
        jumlahTelat++;
        updateStatus();
    }

    private void updateStatus() {
        if (jumlahTelat >= 8) status = "blacklist";
        else if (jumlahTelat >= 5) status = "suspend";
        else if (jumlahTelat >= 3) status = "peringatan";
        else status = "aman";
    }

    // Getters & Setters
    public String getUserId() { return userId; }
    public void setJumlahTelat(int jumlahTelat) {
        this.jumlahTelat = jumlahTelat;
        updateStatus();
    }
    public void setStatus(String status) {
        this.status = status;
    }
    public boolean isSuspend() { return "suspend".equals(status); }
    public boolean isBlacklist() { return "blacklist".equals(status); }
    public int getBatasPinjam() {
        if (isSuspend() || isBlacklist()) return 0;
        if ("peringatan".equals(status)) return 2;
        return 5;
    }

    public String toCSV() {
        return String.format("%s,%d,%s,%s", userId, jumlahTelat, status, hariSuspendBerakhir);
    }
}
