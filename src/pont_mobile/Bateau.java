package pont_mobile;
/**
 * Représente un bateau naviguant sous le pont, dans un sens (nord->sud ou sud->nord).
 * Chaque bateau est un thread indépendant.
 *
 * Cycle de vie d'un bateau :
 * 1. il arrive après un délai aléatoire ;
 * 2. il s'enregistre dans la file FIFO commune du pont (tous sens confondus) ;
 * 3. il attend que le pont soit ouvert et que ce soit son tour (premier de la file) ;
 * 4. il traverse (DUREE_PASSAGE) ;
 * 5. il signale la fin de son passage au pont, qui le retire de la file.
 */

public class Bateau extends Thread {
	
	/** Direction : le bateau va du nord vers le sud. */
    public static final int NORD_SUD = 0;
    
    /** Direction : le bateau va du sud vers le nord. */
    public static final int SUD_NORD = 1;
    
    /** Durée de la traversée sous le pont, en millisecondes. */
    private static final int DUREE_PASSAGE = 2500; 
    
    /** Pont partagé (ressource commune). */
    private Pont pont;
    
    /** Nom d'affichage du bateau. */
    private String nom;
    
    /** Direction du bateau : NORD_SUD ou SUD_NORD. */
    private int sens;
    
    /** Délai avant l'arrivée du bateau au pont, en millisecondes. */
    private int delaiArrivee;

    /**
     * Crée un bateau.
     * @param pont         le pont partagé
     * @param nom          le nom affiché dans la simulation
     * @param sens         NORD_SUD ou SUD_NORD
     * @param delaiArrivee délai avant l'arrivée au pont, en millisecondes
     */
    public Bateau(Pont pont, String nom, int sens, int delaiArrivee) {
        this.pont = pont;
        this.nom = nom;
        this.sens = sens;
        this.delaiArrivee = delaiArrivee;
    }
    
    /** @return le nom du bateau */
    public String getNom() { 
    	return nom; 
    }
    
    /**
     * Donne la direction du bateau sous forme de texte pour l'affichage.
     * @return "N->S" ou "S->N"
     */
    public String sensTexte() {
    	if (sens == NORD_SUD){
    		return "N->S";
    	}
    	return "S->N";
    }
    
    /**
     * Corps du thread : arrivée, enregistrement dans la file FIFO,
     * attente de son tour, traversée, puis fin de passage.
     * Les attentes sont gérées par le pont (wait/notifyAll).
     */
    @Override
    public void run() {
        try {
            Thread.sleep(delaiArrivee);
            pont.enregistrerBateau(this); 
            pont.attendrePassage(this);    
            Thread.sleep(DUREE_PASSAGE);    
            pont.finPassage(this);
        } catch (InterruptedException e) {
        }
    }
}
