package UAPPemlan;

import java.io.BufferedReader;
import java.io.FileReader;

public class testCSV {
    public static void main(String[] args) {
        try {
            BufferedReader br = new BufferedReader(new FileReader("data/buku.csv"));
            String line;
            while ((line = br.readLine()) != null) {
                System.out.println(line);
            }
            br.close();
        } catch (Exception e) {
            System.out.println("Gagal membaca CSV");
            e.printStackTrace();
        }
    }
}
