package pont_mobile;

import java.util.ArrayList;
import java.util.Random;

public class Main {
    public static void main(String[] args) throws InterruptedException {
        long delai = 60000;
        if (args.length > 0) delai = Long.parseLong(args[0]) * 1000;

        Pont pont = new Pont(delai);
        Random r = new Random();
        ArrayList<Thread> threads = new ArrayList<>();

        for (int i = 1; i <= 6; i++)
            threads.add(new Voiture(pont, "V" + i, r.nextInt(2), r.nextInt(15000)));
        for (int i = 1; i <= 3; i++)
            threads.add(new Camion(pont, "C" + i, r.nextInt(2), r.nextInt(15000)));
        for (int i = 1; i <= 2; i++)
            threads.add(new ConvoiExceptionnel(pont, "X" + i, r.nextInt(2), 2000 + r.nextInt(13000)));
        threads.add(new Prioritaire(pont, "A1", r.nextInt(2), 5000 + r.nextInt(5000)));
        threads.add(new Prioritaire(pont, "P1", r.nextInt(2), 10000 + r.nextInt(5000)));

        for (int i = 1; i <= 3; i++)
            threads.add(new Bateau(pont, "B" + i, r.nextInt(2), 3000 + r.nextInt(5000)));
        for (int i = 4; i <= 5; i++)
            threads.add(new Bateau(pont, "B" + i, r.nextInt(2), 20000 + r.nextInt(5000)));

        for (Thread t : threads) {
            t.start();
        }
        for (Thread t : threads) {
            t.join();
        }
        pont.attendreFermeture();
        System.out.println("=== Simulation terminee ===");
        System.exit(0);
    }
}