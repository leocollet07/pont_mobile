package pont_mobile;

public enum Etat {
	FERME, 
	OUVERTURE, 
	OUVERT, 
	FERMETURE; 
	
	public Etat suivant() {
	    switch (this) {
	        case FERME:     return OUVERTURE;
	        case OUVERTURE: return OUVERT;
	        case OUVERT:    return FERMETURE;
	        default:        return FERME;
	    }
	}
}
