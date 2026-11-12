package pont_mobile;


public class Prioritaire extends Vehicule {
    public Prioritaire(Pont pont, String nom, int sens, int delaiArrivee) {
        super(pont, nom, sens, 1, delaiArrivee);
    }

    @Override
    public void run() {
        try {
            Thread.sleep(delaiArrivee);
            pont.arriveePrioritaire(this);
            pont.entrerPrioritaire(this);  
            for (int i = 0; i < Pont.N; i++) {
                pont.positionPrioritaire(this, i);
                Thread.sleep(300);
            }
            pont.sortiePrioritaire(this);   
        } catch (InterruptedException e) {
        }
    }
}
