package pont_mobile;

public class Vehicule extends Thread{
    public static final int GAUCHE_DROITE = 0;
    public static final int DROITE_GAUCHE = 1;
    
	private Pont pont; 
	private String nom;
	private int sens; 
	private int longueur; 
	int delaiArrivee; 
	private int debut;   
	
    public Vehicule(Pont pont, String nom, int sens, int longueur, int delaiArrivee) {
        this.pont = pont;
        this.nom = nom;
        this.sens = sens;
        this.longueur = longueur;
        this.delaiArrivee = delaiArrivee;
    }

	public String getNom() {
		return nom;
	}

	public int getSens() {
		return sens;
	}

	public int getLongueur() {
		return longueur;
	}
	
	public int getDebut() {
		return debut;
	}
	
	public void setDebut(int debut) {
		this.debut = debut; 
	}
	
	public boolean estConvoi() { 
		return false; 
	}
	
	public String sensTexte() {
		if (sens == GAUCHE_DROITE) {
			return "G->D";
		}
		return "D->G"; 
	}
	
    @Override
    public void run() {
        try {
            Thread.sleep(delaiArrivee);
            pont.entrer(this);
            boolean sorti = false;
            while (!sorti) {
                sorti = pont.avancer(this);
            }
        } catch (InterruptedException e) {
        }
    }
    
    
}
