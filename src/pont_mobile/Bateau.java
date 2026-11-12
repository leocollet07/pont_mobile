package pont_mobile;

public class Bateau extends Thread {
    public static final int NORD_SUD = 0;
    public static final int SUD_NORD = 1;
    private static final int DUREE_PASSAGE = 2500; // ms

    private Pont pont;
    private String nom;
    private int sens;
    private int delaiArrivee;

    public Bateau(Pont pont, String nom, int sens, int delaiArrivee) {
        this.pont = pont;
        this.nom = nom;
        this.sens = sens;
        this.delaiArrivee = delaiArrivee;
    }

    public String getNom() { return nom; }
    public String sensTexte() {
    	if (sens == NORD_SUD){
    		return "N->S";
    	}
    	return "S->N";
    }

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
