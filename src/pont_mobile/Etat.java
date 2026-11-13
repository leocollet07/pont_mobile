package pont_mobile;

/**
 * États possibles du pont mobile.
 * Les transitions autorisées forment un cycle :
 * FERME -> OUVERTURE -> OUVERT -> FERMETURE -> FERME.
 */
public enum Etat {
	/** Pont fermé aux bateaux : les véhicules terrestres peuvent circuler. */
	FERME, 
	/** Pont en cours d'ouverture : plus aucun véhicule ne peut entrer. */
	OUVERTURE, 
	/** Pont ouvert : les bateaux traversent un par un dans l'ordre FIFO. */
	OUVERT, 
	/** Pont en cours de fermeture : les véhicules ne peuvent pas encore entrer. */
	FERMETURE; 
	
	/**
	 * Donne l'état qui suit l'état courant dans le cycle du pont.
	 * Permet de vérifier qu'une transition demandée est bien autorisée.
	 * @return l'état suivant
	 */
	public Etat suivant() {
	    switch (this) {
	        case FERME:     return OUVERTURE;
	        case OUVERTURE: return OUVERT;
	        case OUVERT:    return FERMETURE;
	        default:        return FERME;
	    }
	}
}
