package pont_mobile;

/**
 * Voiture : véhicule occupant un seul segment du pont à la fois.
 *
 * Pour traverser, elle attend que le premier segment soit libre, le réserve,
 * puis avance de segment en segment : elle réserve le suivant dès qu'il est
 * disponible et libère le précédent une fois qu'elle l'a effectivement
 * quitté, jusqu'à sa sortie complète du pont.
 */
public class Voiture extends Vehicule {
    /**
     * Crée une voiture.
     * @param pont         le pont partagé
     * @param nom          le nom affiché dans la simulation.
     * @param sens         GAUCHE_DROITE ou DROITE_GAUCHE
     * @param delaiArrivee délai avant l'arrivée au pont, en millisecondes
     */
    public Voiture(Pont pont, String nom, int sens, int delaiArrivee) {
        super(pont, nom, sens, 1, delaiArrivee);
    }
}
