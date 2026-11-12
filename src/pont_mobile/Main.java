package pont_mobile;

import java.util.ArrayList;
import java.util.Random;

public class Main {
    public static void main(String[] args) throws InterruptedException {
        Pont pont = new Pont(0);
        Random r = new Random();
        ArrayList<Thread> threads = new ArrayList<>();

        for (int i = 1; i <= 6; i++)
            threads.add(new Voiture(pont, "V" + i, r.nextInt(2), r.nextInt(15000)));
        for (int i = 1; i <= 3; i++)
            threads.add(new Camion(pont, "C" + i, r.nextInt(2), r.nextInt(15000)));

        for (int i = 1; i <= 3; i++)
            threads.add(new Bateau(pont, "B" + i, r.nextInt(2), 3000 + r.nextInt(5000)));
        for (int i = 4; i <= 5; i++)
            threads.add(new Bateau(pont, "B" + i, r.nextInt(2), 10000 + r.nextInt(5000)));

        for (Thread t : threads) t.start();
        for (Thread t : threads) t.join();
        pont.attendreFermeture();
        System.out.println("=== Simulation terminee ===");
        System.exit(0);
    }
}
