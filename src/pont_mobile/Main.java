package pont_mobile;

import java.util.ArrayList;
import java.util.Random;

/**
 * Point d'entrée de la simulation du pont mobile.
 * Crée le pont, puis tous les threads (voitures, camions, convois,
 * prioritaires, bateaux) et attend la fin de la simulation.
 *
 * Utilisation : java -jar pont_mobile.jar [delaiEntreOuverturesEnSecondes]
 * (60 secondes par défaut)
 */
public class Main {

    /**
     * Lance la simulation.
     */
    public static void main(String[] args) throws InterruptedException {
        // Délai minimal entre deux ouvertures (60 s par défaut, modifiable en argument)
        long delai = 60000;
        if (args.length > 0) delai = Long.parseLong(args[0]) * 1000;

        Pont pont = new Pont(delai);

        // Graine fixe : les arrivées et les sens sont identiques à chaque lancement.
        // Pour varier la simulation, changer la valeur (ou utiliser new Random()).
        Random r = new Random(42);

        ArrayList<Thread> threads = new ArrayList<>();

        // 6 voitures (1 segment), sens et heure d'arrivée aléatoires
        for (int i = 1; i <= 6; i++)
            threads.add(new Voiture(pont, "V" + i, r.nextInt(2), r.nextInt(15000)));

        // 3 camions (2 segments)
        for (int i = 1; i <= 3; i++)
            threads.add(new Camion(pont, "C" + i, r.nextInt(2), r.nextInt(15000)));

        // 2 convois exceptionnels (4 segments, sans véhicule en sens opposé)
        for (int i = 1; i <= 2; i++)
            threads.add(new ConvoiExceptionnel(pont, "X" + i, r.nextInt(2), 2000 + r.nextInt(13000)));

        // 2 véhicules prioritaires (ambulance A1, pompiers P1)
        threads.add(new Prioritaire(pont, "A1", r.nextInt(2), 5000 + r.nextInt(5000)));
        threads.add(new Prioritaire(pont, "P1", r.nextInt(2), 10000 + r.nextInt(5000)));

        // Premier groupe de bateaux : déclenche la première ouverture
        for (int i = 1; i <= 3; i++)
            threads.add(new Bateau(pont, "B" + i, r.nextInt(2), 3000 + r.nextInt(5000)));

        // Second groupe : arrive pendant le délai minimal, doit attendre la prochaine ouverture
        for (int i = 4; i <= 5; i++)
            threads.add(new Bateau(pont, "B" + i, r.nextInt(2), 20000 + r.nextInt(5000)));

        // Démarrage de tous les threads
        for (Thread t : threads) {
            t.start();
        }
        // Attente de la fin de tous les véhicules et bateaux
        for (Thread t : threads) {
            t.join();
        }
        // Attente du retour à l'état FERME (fin de la dernière fermeture)
        pont.attendreFermeture();
        System.out.println("=== Simulation terminee ===");
        System.exit(0);
    }
}