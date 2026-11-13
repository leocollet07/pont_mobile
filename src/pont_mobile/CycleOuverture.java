package pont_mobile;

/**
 * Thread qui exécute un cycle complet d'ouverture du pont pour laisser
 * passer les bateaux. Il est créé par le Pont lorsqu'une ouverture est
 * autorisée et qu'au moins un bateau attend.
 *
 * Déroulement du cycle :
 * 1. attente que le pont soit vide puis passage à l'état OUVERTURE ;
 * 2. durée d'ouverture puis état OUVERT ;
 * 3. attente que la file FIFO des bateaux soit vide ;
 * 4. état FERMETURE, durée de fermeture;
 * 5. retour à l'état FERME, mémorisation de l'heure de fermeture et
 *    programmation de la prochaine ouverture autorisée.
 */

public class CycleOuverture extends Thread {
	/** Pont partagé dont ce thread pilote l'ouverture et la fermeture. */
    private Pont pont;
    
    /** Durée de l'état OUVERTURE, en millisecondes. */
    private static final int DUREE_OUVERTURE = 2000;
    
    /** Durée de l'état FERMETURE, en millisecondes. */
    private static final int DUREE_FERMETURE = 2000; 

    /**
     * Crée le cycle d'ouverture pour un pont donné.
     * @param pont le pont partagé
     */
    public CycleOuverture(Pont pont) { 
        
    	this.pont = pont; 
    }
    
    /**
     * Corps du thread : enchaîne les états du pont
     * (OUVERTURE -> OUVERT -> FERMETURE -> FERME).
     * Les attentes sont gérées par le pont.
     */
    @Override
    public void run() {
        try {
            pont.attendrePontVide();              
            Thread.sleep(DUREE_OUVERTURE);
            pont.changerEtat(Etat.OUVERT);
            pont.attendreFileVide();           
            pont.changerEtat(Etat.FERMETURE);
            Thread.sleep(DUREE_FERMETURE); 
            pont.fermetureTerminee();              
        } catch (InterruptedException e) {
        	Thread.currentThread().interrupt();
        }
    }
}
