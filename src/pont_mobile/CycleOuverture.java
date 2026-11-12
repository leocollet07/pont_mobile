package pont_mobile;

public class CycleOuverture extends Thread {
    private Pont pont;

    public CycleOuverture(Pont pont) { 
    	this.pont = pont; 
    }
    
    @Override
    public void run() {
        try {
            pont.attendrePontVide();              
            //thread sleep de duree d'ouverture
            pont.changerEtat(Etat.OUVERT);
            //bateau ajoutés plus tard            
            pont.changerEtat(Etat.FERMETURE);
            //thread sleep de dureee de fermeture 
            pont.fermetureTerminee();              
        } catch (InterruptedException e) {}
    }
}
