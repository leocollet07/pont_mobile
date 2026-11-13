package pont_mobile;

/**
 * Camion : véhicule occupant deux segments consécutifs du pont.
 *
 * Avant d'entrer, il doit pouvoir réserver ses deux segments simultanément.
 * Ensuite, il avance en libérant le segment qu'il quitte à l'arrière et en réservant 
 * le suivant à l'avant, jusqu'à sa sortie complète. Les mêmes règles s'appliquent aux
 * deux sens de circulation.
 */
public class Camion extends Vehicule {
    /**
     * Crée un camion.
     * @param pont         le pont partagé
     * @param nom          le nom affiché dans la simulation
     * @param sens         GAUCHE_DROITE ou DROITE_GAUCHE
     * @param delaiArrivee délai avant l'arrivée au pont, en millisecondes
     */
    public Camion(Pont pont, String nom, int sens, int delaiArrivee) {
        super(pont, nom, sens, 2, delaiArrivee);
    }
}
