package pont_mobile;

import java.util.TimerTask;

/**
 * Tâche programmée par le Timer du pont à la fin de chaque fermeture.
 * Elle se déclenche lorsque le délai minimal entre deux ouvertures est écoulé
 * (1 heure dans la consigne, réduit pour la simulation) et demande alors au pont
 * de vérifier s'il faut lancer une nouvelle ouverture.
 */
public class TacheOuverture extends TimerTask {
	
	/** Pont partagé sur lequel la tentative d'ouverture est effectuée. */
    private Pont pont;

    /**
     * Crée la tâche pour un pont donné.
     * @param pont le pont partagé
     */
    public TacheOuverture(Pont pont) { this.pont = pont; }
    
    /**
    * Appelée par le Timer à l'heure de la prochaine ouverture autorisée.
    * Le pont déclenche une ouverture seulement si des bateaux attendent
    * et si les conditions sont réunies (pont fermé, pas d'ouverture déjà en cours).
    */
    @Override
    public void run() {
        pont.tenterOuverture(); 
    }
}
