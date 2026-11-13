package pont_mobile;

/**
 * Véhicule prioritaire (ambulance, pompiers).
 *
 * Dès son arrivée, il est signalé au pont : les véhicules présents sur le
 * pont interrompent leur progression et aucun nouveau véhicule n'entre.
 * Le prioritaire traverse alors le pont dans son sens, sur la bande
 * d'urgence, sans gêner le sens opposé. À sa sortie, les véhicules
 * interrompus reprennent leur progression là où ils s'étaient arrêtés.
 */
public class Prioritaire extends Vehicule {
	
    /**
     * Crée un véhicule prioritaire.
     * @param pont         le pont partagé
     * @param nom          le nom affiché dans la simulation.
     * @param sens         GAUCHE_DROITE ou DROITE_GAUCHE
     * @param delaiArrivee délai avant l'arrivée au pont, en millisecondes
     */
    public Prioritaire(Pont pont, String nom, int sens, int delaiArrivee) {
        super(pont, nom, sens, 1, delaiArrivee);
    }
    
    
    /**
     * Corps du thread : arrivée, signalement au pont (ce qui gèle les autres
     * véhicules), attente de l'autorisation d'entrer, traversée segment par
     * segment, puis sortie qui relance les véhicules gelés.
     */
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
