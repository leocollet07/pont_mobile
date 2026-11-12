package pont_mobile;

import java.util.LinkedList;
import java.util.Timer;

public class Pont {
    private Etat etat = Etat.FERME;
    private LinkedList<Bateau> file = new LinkedList<>();
    private Bateau bateauQuiPasse = null;
    private boolean ouvertureDemandee = false;
    private int convoiSens = -1;
    public static final int N = 8;  //Segment
    private String[][] segments = new String[2][N];
    private String[][] urgence = new String[2][N];
    
	private long delaiOuverture;
	private long prochaineOuverture = 0;  
	private Timer timer = new Timer(true);
	
	private int nbConvois;
	private int prioritaires = 0;
    
    public Pont(long delaiOuverture) {
        this.delaiOuverture = delaiOuverture;
        System.out.print("\033[2J"); 
    }
    
    
    
    public synchronized void changerEtat(Etat nouveau) {
    	if(nouveau != etat.suivant()) {
    		throw new IllegalStateException("Transition interdite " + etat + " -> " + nouveau);
    	}
    	etat = nouveau;
    	afficher();
    	notifyAll();
    }
    
    public synchronized boolean avancer(Vehicule v) throws InterruptedException {
        int s = v.getSens();
        int L = v.getLongueur();
        int d = v.getDebut();
        boolean sortie = (d + L - 1 == N - 1); 
        
        while (prioritaires > 0 || (!sortie && segments[s][d + L] != null)) {
            wait();
        }
        
        if (sortie) {
            for (int i = d; i < d + L; i++) segments[s][i] = null;
            if (v.estConvoi()) {
                nbConvois--;
                if (nbConvois == 0) {
                	convoiSens = -1; 
                }
            }
            afficher();
            notifyAll();
            return true;
        }
        
        while (segments[s][d + L] != null) {
            wait();
        }
        
        segments[s][d + L] = v.getNom(); 
        segments[s][d] = null;         
        v.setDebut(d + 1);
        afficher();
        notifyAll();
        return false;
    }
   
    public synchronized void entrer(Vehicule v) throws InterruptedException {
        int s = v.getSens();
        if (v.estConvoi()) {
            while (convoiSens != -1 && convoiSens != s) {
            	wait();
            }
            convoiSens = s;
            nbConvois++; 
        }
        while (!peutEntrer(v)) {
        	wait();
        }
        for (int i = 0; i < v.getLongueur(); i++) {
            segments[s][i] = v.getNom();
        }
        afficher();
        v.setDebut(0);
    }
    
    private boolean peutEntrer(Vehicule v) {
        int s = v.getSens();
        if (etat != Etat.FERME) {
        	return false;           
        }
        if (ouvertureDemandee) {
        	return false;          
        }
        if (convoiSens == 1 - s) {
        	return false;           
        }
        if (!libre(s, 0, v.getLongueur())) {
        	return false; 
        }
        if (prioritaires > 0) {
            return false;
        }
        if (v.estConvoi() && !voieVide(1 - s)) {
        	return false;
        }
        return true;
    }
    

    
    private boolean libre(int sens, int debut, int nb) {
        for (int i = debut; i < debut + nb; i++) {
            if (segments[sens][i] != null) {
            	return false;
            }
        }
        return true;
    }
    
    private boolean voieVide(int sens) { 
    	return libre(sens, 0, N); 
    }
    
    private boolean pontVide() { 
    	return voieVide(0) && voieVide(1); 
    }
    
    public synchronized void tenterOuverture() {
    	if (etat == Etat.FERME && !ouvertureDemandee && !file.isEmpty() && System.currentTimeMillis() >= prochaineOuverture) {
            ouvertureDemandee = true; 
            new CycleOuverture(this).start();
            notifyAll();
        }
    }
    
    public synchronized void attendrePontVide() throws InterruptedException {
        while (!pontVide() || prioritaires > 0) {
        	wait();
        }
        changerEtat(Etat.OUVERTURE);
    }

    public synchronized void fermetureTerminee() {
        changerEtat(Etat.FERME);
        ouvertureDemandee = false;
        prochaineOuverture = System.currentTimeMillis() + delaiOuverture;
        timer.schedule(new TacheOuverture(this), delaiOuverture + 100);
        notifyAll();
    }
    
    public synchronized void attendreFermeture() throws InterruptedException {
        while (etat != Etat.FERME || ouvertureDemandee) {
        	wait();
        }
    }
    
    //PRIORITAIRE 
    
    public synchronized void arriveePrioritaire(Vehicule v) {
        prioritaires++;
        notifyAll();
    }

    public synchronized void entrerPrioritaire(Vehicule v) throws InterruptedException {
        while (etat != Etat.FERME) {
            wait();
        }
    }

    public synchronized void positionPrioritaire(Vehicule v, int i) {
        int s = v.getSens();
        if (i > 0 && v.getNom().equals(urgence[s][i - 1])) {
            urgence[s][i - 1] = null;
        }
        urgence[s][i] = v.getNom();
        afficher();
    }

    public synchronized void sortiePrioritaire(Vehicule v) {
        int s = v.getSens();
        if (v.getNom().equals(urgence[s][N - 1])) {
            urgence[s][N - 1] = null;
        }
        prioritaires--;
        afficher();
        notifyAll();
    }
    
    //BATEAU 
    
    public synchronized void enregistrerBateau(Bateau b) {
        file.add(b);
        tenterOuverture();
        afficher();
    }

    public synchronized void attendrePassage(Bateau b) throws InterruptedException {
        while (!(etat == Etat.OUVERT && file.getFirst() == b)) wait();
        bateauQuiPasse = b;
        afficher();
    }

    public synchronized void finPassage(Bateau b) {
        file.removeFirst();
        bateauQuiPasse = null;
        afficher();
        notifyAll();
    }
    
    public synchronized void attendreFileVide() throws InterruptedException {
        while (!file.isEmpty()) {
            wait();
        }
    }
    
    //AFFICHAGE 
    
    private String ligne(String s) { 
    	return s + "\033[K\n"; 
    }
    
    private String dessinerVoie(int sens) {
        StringBuilder sb = new StringBuilder("|");
        for (int i = 0; i < N; i++) {
            String nom = segments[sens][i];
            if (nom == null) {
            	sb.append("[  ]");
            }
            else if (prioritaires > 0) {
                sb.append("(" + String.format("%-2s", nom) + ")");
            }
            else {
            	sb.append("[" + String.format("%-2s", nom) + "]");
            }
        }
        return sb.append("|").toString();
    }

    private String dessinerUrgence(int sens) {
        StringBuilder sb = new StringBuilder("|");
        for (int i = 0; i < N; i++) {
            String nom = urgence[sens][i];
            if (nom == null) {
                sb.append(" .. ");
            } else {
                sb.append("<" + nom + ">");
            }
        }
        return sb.append("|").toString();
    }
    
    private void afficher() {
        StringBuilder sb = new StringBuilder("\033[H");
        sb.append(ligne("===== PONT MOBILE =====   Etat : " + etat));
        sb.append(ligne("Voie G->D    : " + dessinerVoie(0)));
        sb.append(ligne("Urgence G->D : " + dessinerUrgence(0)));
        sb.append(ligne("Voie D->G    : " + dessinerVoie(1)));
        sb.append(ligne("Urgence D->G : " + dessinerUrgence(1)));
        sb.append(ligne(""));
        sb.append(ligne("Convoi : " + (convoiSens == -1 ? "-" : (convoiSens == 0 ? "G->D" : "D->G"))));
        sb.append(ligne(""));
        String bateau = "-";
        if (bateauQuiPasse != null) {
            bateau = bateauQuiPasse.getNom() + " " + bateauQuiPasse.sensTexte();
        }
        sb.append(ligne("Bateau sous le pont : " + bateau));
        StringBuilder f = new StringBuilder();
        for (Bateau b : file) {
            if (b != bateauQuiPasse) f.append(b.getNom() + "(" + b.sensTexte() + ") ");
        }
        sb.append(ligne("File d'attente FIFO : " + f));
        sb.append(ligne(""));
        System.out.print(sb);
    }
    
}


