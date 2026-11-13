package pont_mobile;

import java.util.LinkedList;

import java.util.Timer;
/**
 * Ressource partagée centrale de la simulation.
 *
 * Le pont gère :
 * - son état (FERME, OUVERTURE, OUVERT, FERMETURE) ;
 * - les N segments de chaque sens, réservés par les véhicules ;
 * - les convois exceptionnels (blocage du sens opposé) ;
 * - les véhicules prioritaires (bande d'urgence, gel des autres) ;
 * - la file FIFO des bateaux et le délai entre deux ouvertures.
 *
 * Toutes les méthodes publiques sont synchronized : les threads attendent
 * avec wait() et sont réveillés par notifyAll() à chaque changement d'état.
 */
public class Pont {
	/** État courant du pont. */
    private Etat etat = Etat.FERME;
    
    /** File FIFO des bateaux en attente. */
    private LinkedList<Bateau> file = new LinkedList<>();
    
    /** Bateau en train de passer sous le pont. */
    private Bateau bateauQuiPasse = null;
    
    /** Vrai dès qu'une ouverture est prise en charge. */
    private boolean ouvertureDemandee = false;
    
    /** Sens du convoi exceptionnel présent ou en attente. */
    private int convoiSens = -1;
    
    /** Nombre de segments du pont. */
    public static final int N = 8;  //Segment
    
    /** Occupation des segments : segments[sens][i] contient le nom du véhicule. */
    private String[][] segments = new String[2][N];
    
    /** Position des véhicules prioritaires sur la bande d'urgence, par sens. */
    private String[][] urgence = new String[2][N];
    
    /** Vrai si la bande d'urgence du sens est utilisée . */
    private boolean[] urgenceOccupee = new boolean[2];
    
	/** Délai minimal entre deux ouvertures, en millisecondes. */
	private long delaiOuverture;
	
	/** Heure (en ms) à partir de laquelle une nouvelle ouverture est autorisée. */
	private long prochaineOuverture = 0;  
	
	/** Timer qui déclenche la tentative d'ouverture une fois le délai écoulé. */
	private Timer timer = new Timer(true);
	
	/** Nombre de convois exceptionnels présents ou en attente d'entrer. */
	private int nbConvois;
	
	/** Nombre de véhicules prioritaires arrivés et pas encore sortis. */
	private int prioritaires = 0;
    
	/**
    * Crée le pont et efface l'écran de la console.
    * @param delaiOuverture délai minimal entre deux ouvertures, en millisecondes
    */
    public Pont(long delaiOuverture) {
        this.delaiOuverture = delaiOuverture;
        System.out.print("\033[2J"); 
    }
    
    /**
     * Change l'état du pont en vérifiant que la transition est autorisée
     * (FERME -> OUVERTURE -> OUVERT -> FERMETURE -> FERME), puis réveille
     * tous les threads en attente.
     * @param nouveau le nouvel état
     * @throws IllegalStateException si la transition est interdite
     */
    public synchronized void changerEtat(Etat nouveau) {
    	if(nouveau != etat.suivant()) {
    		throw new IllegalStateException("Transition interdite " + etat + " -> " + nouveau);
    	}
    	etat = nouveau;
    	afficher();
    	notifyAll();
    }
    
    /**
     * Fait avancer un véhicule d'un segment, ou le fait sortir du pont s'il
     * est sur son dernier segment. Bloque tant qu'un prioritaire est présent
     * ou que le segment suivant est occupé. Réserve le nouveau segment avant
     * de libérer celui qu'il quitte à l'arrière.
     * Si le véhicule est un convoi qui sort, le blocage du sens opposé est
     * levé quand il n'y a plus de convoi.
     * @param v le véhicule à faire avancer
     * @return true si le véhicule a quitté le pont
     */
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
        
        segments[s][d + L] = v.getNom(); 
        segments[s][d] = null;         
        v.setDebut(d + 1);
        afficher();
        notifyAll();
        return false;
    }
   
    /**
     * Fait entrer un véhicule sur le pont. Bloque d'abord si convoi
     * l'entrée du sens opposé. Le véhicule attend que toutes les
     * conditions soient réunies, puis réserve d'un coup tous ses
     * segments de départ.
     * @param v le véhicule qui veut entrer
     */
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
    
    /**
     * Vérifie si un véhicule peut entrer sur le pont.
     * @param v le véhicule
     * @return true si l'entrée est autorisée
     */
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
    
    /**
     * Teste si nb segments consécutifs d'un sens sont libres.
     * @param sens  sens de circulation
     * @param debut indice du premier segment testé
     * @param nb    nombre de segments à tester
     * @return true si tous ces segments sont libres
     */
    private boolean libre(int sens, int debut, int nb) {
        for (int i = debut; i < debut + nb; i++) {
            if (segments[sens][i] != null) {
            	return false;
            }
        }
        return true;
    }
    
    /**
     * @param sens sens de circulation (0 ou 1)
     * @return true si aucun véhicule n'occupe ce sens du pont
     */
    private boolean voieVide(int sens) { 
    	return libre(sens, 0, N); 
    }
    
    /** @return true si aucun véhicule n'est sur le pont, dans aucun sens */
    private boolean pontVide() { 
    	return voieVide(0) && voieVide(1); 
    }
    
    /**
     * Déclenche une ouverture si c'est autorisé
     * Appelée à l'arrivée d'un bateau et par le Timer (TacheOuverture).
     */
    public synchronized void tenterOuverture() {
    	if (etat == Etat.FERME && !ouvertureDemandee && !file.isEmpty() && System.currentTimeMillis() >= prochaineOuverture) {
            ouvertureDemandee = true; 
            new CycleOuverture(this).start();
            notifyAll();
        }
    }
    
    /**
     * Attend que le pont soit vide et qu'aucun prioritaire ne soit présent,
     * puis passe à l'état OUVERTURE. Les véhicules déjà engagés terminent
     * donc leur traversée avant le début de l'ouverture.
     */
    public synchronized void attendrePontVide() throws InterruptedException {
        while (!pontVide() || prioritaires > 0) {
        	wait();
        }
        changerEtat(Etat.OUVERTURE);
    }

    /**
     * Termine la fermeture, calcul le reste.
     */
    public synchronized void fermetureTerminee() {
        changerEtat(Etat.FERME);
        ouvertureDemandee = false;
        prochaineOuverture = System.currentTimeMillis() + delaiOuverture;
        timer.schedule(new TacheOuverture(this), delaiOuverture + 100);
        notifyAll();
    }
    
    /**
     * Attend que le pont soit revenu à l'état FERME sans ouverture en cours
     */
    public synchronized void attendreFermeture() throws InterruptedException {
        while (etat != Etat.FERME || ouvertureDemandee) {
        	wait();
        }
    }
    
    //PRIORITAIRE 
    
    /**
     * Signale l'arrivée d'un véhicule prioritaire : les autres véhicules
     * s'arrêtent et aucune nouvelle entrée n'est autorisée.
     * @param v le véhicule prioritaire
     */
    public synchronized void arriveePrioritaire(Vehicule v) {
        prioritaires++;
        notifyAll();
    }
    
    /**
     * Attend que le pont soit fermé et que la bande d'urgence de son sens
     * soit libre, puis la réserve pour ce prioritaire.
     * @param v le véhicule prioritaire
     */
    public synchronized void entrerPrioritaire(Vehicule v) throws InterruptedException {
        int s = v.getSens();
        while (etat != Etat.FERME || urgenceOccupee[s]) {
            wait();
        }
        urgenceOccupee[s] = true;
    }
    
    /**
     * Place le prioritaire sur le segment i de la bande d'urgence
     * (en libérant le segment précédent) et met à jour l'affichage.
     * @param v le véhicule prioritaire
     * @param i indice du segment atteint
     */
    public synchronized void positionPrioritaire(Vehicule v, int i) {
        int s = v.getSens();
        if (i > 0 && v.getNom().equals(urgence[s][i - 1])) {
            urgence[s][i - 1] = null;
        }
        urgence[s][i] = v.getNom();
        afficher();
    }
    
    /**
     * Le prioritaire quitte le pont : libère la bande d'urgence et réveille
     * les véhicules gelés, qui reprennent là ou ils en étaient.
     * @param v le véhicule prioritaire
     */
    public synchronized void sortiePrioritaire(Vehicule v) {
        int s = v.getSens();
        if (v.getNom().equals(urgence[s][N - 1])) {
            urgence[s][N - 1] = null;
        }
        urgenceOccupee[s] = false;
        prioritaires--;
        afficher();
        notifyAll();
    }
    
    //BATEAU 
    
    /**
     * Enregistre un bateau à la fin de la file FIFO et tente de
     * déclencher une ouverture.
     * @param b le bateau qui arrive
     */
    public synchronized void enregistrerBateau(Bateau b) {
        file.add(b);
        tenterOuverture();
        afficher();
    }

    /**
     * Attend que le pont soit ouvert et que le bateau soit le premier de
     * la file, puis l'autorise à passer.
     * @param b le bateau en attente
     */
    public synchronized void attendrePassage(Bateau b) throws InterruptedException {
        while (!(etat == Etat.OUVERT && file.getFirst() == b)) wait();
        bateauQuiPasse = b;
        afficher();
    }
    
    /**
     * Fin de passage d'un bateau : il est retiré de la file, ce qui laisse
     * passer le suivant (ou ferme si la file est vide).
     * @param b le bateau qui a fini de passer
     */
    public synchronized void finPassage(Bateau b) {
        file.removeFirst();
        bateauQuiPasse = null;
        afficher();
        notifyAll();
    }
    
    /** Attend que la file soit vide (avant de fermer le pont). */
    public synchronized void attendreFileVide() throws InterruptedException {
        while (!file.isEmpty()) {
            wait();
        }
    }
    
    //AFFICHAGE 
    
    /**
     * Formate une ligne d'affichage et efface la fin.
     * @param s le texte de la ligne
     * @return la ligne prête à être affichée
     */
    private String ligne(String s) { 
    	return s + "\033[K\n"; 
    }
    
    /**
     * Dessine une voie du pont. 0 est l'entrée du pont : pour le
     * sens D->G, l'affichage est inversé.
     * @param sens 0 (G->D) ou 1 (D->G)
     * @return la voie sous forme de texte
     */

    private String dessinerVoie(int sens) {
        StringBuilder sb = new StringBuilder("|");
        for (int k = 0; k < N; k++) {
            int i;
            if (sens == 0) {
                i = k;           
            } else {
                i = N - 1 - k;  
            }
            String nom = segments[sens][i];
            if (nom == null) {
                sb.append("[  ]");
            } else if (prioritaires > 0) {
                sb.append("(" + String.format("%-2s", nom) + ")");
            } else {
                sb.append("[" + String.format("%-2s", nom) + "]");
            }
        }
        return sb.append("|").toString();
    }

    /**
     * Dessine la bande d'urgence d'un sens, dans le même ordre d'affichage
     * que la voie correspondante.
     * @param sens 0 (G->D) ou 1 (D->G)
     * @return la bande d'urgence sous forme de texte
     */
    private String dessinerUrgence(int sens) {
        StringBuilder sb = new StringBuilder("|");
        for (int k = 0; k < N; k++) {
            int i = (sens == 0) ? k : N - 1 - k;
            String nom = urgence[sens][i];
            if (nom == null) {
                sb.append(" .. ");
            } else {
                sb.append("<" + nom + ">");
            }
        }
        return sb.append("|").toString();
    }
    
    /**
     * Redessine tout l'écran en console : état du pont,
     * voies et bandes d'urgence, convoi, bateau en cours de passage et file
     * d'attente FIFO. Appelée à chaque changement, sous verrou, donc
     * l'affichage ne s'entremêle pas entre threads.
     */
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


