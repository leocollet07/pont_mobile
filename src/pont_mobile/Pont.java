package pont_mobile;

import java.util.ArrayList;
import java.util.List;

public class Pont {
    private Etat etat = Etat.FERME;
    private int surlePont = 0;
    private int nbBateauAttente; 
    private boolean bateauQuiPasse = false;
    private boolean cycleOuverture = false; 
    private final int[] voie = new int[2];
    
    synchronized void changerEtat(Etat nouveau) {
    	if(nouveau != etat.suivant()) {
    		throw new IllegalStateException("Transition interdite " + etat + " -> " + nouveau);
    	}
    	etat = nouveau;
    }
    
    synchronized void attendreVidange() throws InterruptedException {
        while (!(cycleOuverture && voie[0] + voie[1] == 0)) wait();
    }
    
    synchronized void autoriserOuvertureSiPossible() {
        if (etat == etat.FERME && !cycleOuverture && !(nbBateauAttente == 0)) {
            cycleOuverture = true;
        }
    }
    
    private boolean admissible(Vehicule v) {
    	if (etat != Etat.FERME || cycleOuverture) {
    		return false; 
    	}
    return true; 
    }
    
    private void verifier() {
    	if (etat != Etat.FERME && !(surlePont == 0)) {
    		throw new IllegalStateException("Vehicules sur le pont alors que l'etat est " + etat);
    	}
    	if (bateauQuiPasse == true && etat != Etat.OUVERT)
            throw new IllegalStateException("Bateau en transit alors que le pont n'est pas ouvert");
    }
}


