package pont_mobile;

/**
 * Convoi exceptionnel : véhicule occupant quatre segments consécutifs du pont.
 *
 * Sa largeur interdit la présence simultanée de véhicules circulant en sens
 * opposé. À son arrivée, le pont bloque donc l'entrée du sens opposé, attend
 * que les véhicules déjà engagés soient sortis et que les quatre segments
 * nécessaires soient libres, puis laisse entrer le convoi. Les véhicules du
 * même sens peuvent continuer à circuler. L'entrée opposée est débloquée
 * après la sortie complète du convoi.
 */
public class ConvoiExceptionnel extends Vehicule {
	
    /**
     * Crée un convoi exceptionnel (longueur : 4 segments).
     * @param pont         le pont partagé
     * @param nom          le nom affiché dans la simulation (ex. "X1")
     * @param sens         GAUCHE_DROITE ou DROITE_GAUCHE
     * @param delaiArrivee délai avant l'arrivée au pont, en millisecondes
     */
    public ConvoiExceptionnel(Pont pont, String nom, int sens, int delaiArrivee) {
        super(pont, nom, sens, 4, delaiArrivee);
    }
    
    /**
     * Indique que ce véhicule est un convoi exceptionnel, ce qui déclenche
     * dans le pont les règles.
     * @return toujours true
     */
    @Override
    public boolean estConvoi() { 
    	return true; 
    }
}
