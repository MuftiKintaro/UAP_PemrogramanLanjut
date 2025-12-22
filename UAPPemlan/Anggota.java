package UAPPemlan;

public class Anggota {
    private String id, username, password, nama, status;

    public Anggota(String id, String username, String password, String nama,String status) {
        this.id = id;
        this.username = username;
        this.password = password; // hashed
        this.nama = nama;
        this.status = status;
    }

    // Getters & Setters
    public String getId() {
        return id;
    }
    public String getUsername() {
        return username;
    }
    public String getPassword() {
        return password;
    }
    public String getNama() {
        return nama;
    }
    public String getStatus() {
        return status;
    }

    public String toCSV() {
        return String.format("%s,%s,%s,%s,%s", id, username, password, nama, status);
    }

    public static Anggota fromCSV(String line) {
        String[] parts = line.split(",");
        if (parts.length >= 5) {
            return new Anggota(parts[0], parts[1], parts[2], parts[3], parts[4]);
        }
        return null;
    }
}
