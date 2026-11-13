package pont_mobile;
/**
 * Classe abstraite commune à tous les véhicules terrestres (voitures, camions,
 * convois exceptionnels, véhicules prioritaires). Chaque véhicule est un
 * thread indépendant.
 *
 * Un véhicule est caractérisé par son sens, sa longueur (nombre de segments
 * occupés) et sa position sur le pont (indice du segment de queue, "debut").
 * Les sous-classes ne diffèrent que par leur longueur et leurs règles
 * spécifiques, gérées par le pont.
 */
public abstract class Vehicule extends Thread{
	/** Sens de circulation*/
    public static final int GAUCHE_DROITE = 0;
	/** Sens de circulation*/
    public static final int DROITE_GAUCHE = 1;
    
	/** Pont partagé (ressource commune synchronisée). */
	protected Pont pont; 
	
	/** Nom d'affichage du véhicule. */
	private String nom;
	
	/** Sens de circulation*/
	private int sens; 
	
	/** Nombre de segments consécutifs occupés*/
	private int longueur; 
	
	/** Délai avant l'arrivée à l'entrée du pont, en millisecondes. */
	int delaiArrivee; 
	
	/** Indice du segment le plus en arrière occupé par le véhicule sur le pont. */
	private int debut;   
	
    /**
     * Crée un véhicule.
     * @param pont         le pont partagé
     * @param nom          le nom affiché dans la simulation
     * @param sens         GAUCHE_DROITE ou DROITE_GAUCHE
     * @param longueur     nombre de segments occupés
     * @param delaiArrivee délai avant l'arrivée au pont, en millisecondes
     */
    public Vehicule(Pont pont, String nom, int sens, int longueur, int delaiArrivee) {
        this.pont = pont;
        this.nom = nom;
        this.sens = sens;
        this.longueur = longueur;
        this.delaiArrivee = delaiArrivee;
    }

	/** @return le nom du véhicule */
	public String getNom() {
		return nom;
	}

	/** @return le sens de circulation (GAUCHE_DROITE ou DROITE_GAUCHE) */
	public int getSens() {
		return sens;
	}

	/** @return le nombre de segments occupés par le véhicule */
	public int getLongueur() {
		return longueur;
	}
	
	/** @return l'indice du segment de queue du véhicule sur le pont */
	public int getDebut() {
		return debut;
	}
	
	/**
	 * Met à jour la position du véhicule sur le pont.
	 * @param debut nouvel indice du segment de queue
	 */
	public void setDebut(int debut) {
		this.debut = debut; 
	}
	
	/**
	 * Indique si le véhicule est un convoi exceptionnel.
	 * Redéfinie par ConvoiExceptionnel pour retourner true.
	 * @return false par défaut
	 */
	public boolean estConvoi() { 
		return false; 
	}
	
	/**
	 * Donne le sens de circulation sous forme de texte pour l'affichage.
	 * @return "G->D" ou "D->G"
	 */
	public String sensTexte() {
		if (sens == GAUCHE_DROITE) {
			return "G->D";
		}
		return "D->G"; 
	}
	
	/**
	 * Corps du thread : arrivée, entrée sur le pont (avec attente éventuelle),
	 * puis avancée d'un segment par seconde jusqu'à la sortie complète.
	 * Toutes les attentes et réservations de segments sont gérées par le pont.
	 */
    @Override
    public void run() {
        try {
            Thread.sleep(delaiArrivee);
            pont.entrer(this);
            boolean sorti = false;
            while (!sorti) {
            	Thread.sleep(1000);
                sorti = pont.avancer(this);
            }
        } catch (InterruptedException e) {
        }
    }
    
    
}
