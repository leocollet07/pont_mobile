package pont_mobile;

public class CycleOuverture extends Thread {
    private Pont pont;
    private static final int DUREE_OUVERTURE = 2000; 
    private static final int DUREE_FERMETURE = 2000; 

    public CycleOuverture(Pont pont) { 
        
    	this.pont = pont; 
    }
    
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
