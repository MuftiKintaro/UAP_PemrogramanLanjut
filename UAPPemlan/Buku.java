package UAPPemlan;

public class Buku {
    private String kodeBuku, title, author, isbn;
    private int stock;
    private double avgRating = 0.0;

    public Buku(String kodeBuku, String title, String author, String isbn, int stock) {
        this.kodeBuku = kodeBuku;
        this.title = title;
        this.author = author;
        this.isbn = isbn;
        this.stock = stock;
    }

    // Getters & Setters
    public String getKodeBuku() { return kodeBuku; }
    public void setKodeBuku(String kodeBuku) { this.kodeBuku = kodeBuku; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getAuthor() { return author; }
    public void setAuthor(String author) { this.author = author; }
    public String getIsbn() { return isbn; }
    public void setIsbn(String isbn) { this.isbn = isbn; }
    public int getStock() { return stock; }
    public void setStock(int stock) { this.stock = stock; }
    public double getAvgRating() { return avgRating; }
    public void setAvgRating(double avgRating) { this.avgRating = avgRating; }

    public String toCSV() {
        return String.format("%s,%s,%s,%s,%d,%.1f", kodeBuku, title, author, isbn, stock, avgRating);
    }

    public static Buku fromCSV(String line) {
        String[] parts = line.split(",", -1);
        if (parts.length >= 5) {
            try {
                Buku buku = new Buku(parts[0], parts[1], parts[2], parts[3], Integer.parseInt(parts[4]));
                if (parts.length > 5 && !parts[5].trim().isEmpty()) {
                    buku.setAvgRating(Double.parseDouble(parts[5].trim()));
                }
                return buku;
            } catch (Exception e) {
                return null;
            }
        }
        return null;
    }
}

