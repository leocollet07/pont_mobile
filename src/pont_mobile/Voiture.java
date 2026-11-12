package pont_mobile;

public class Voiture extends Vehicule {
    public Voiture(Pont pont, String nom, int sens, int delaiArrivee) {
        super(pont, nom, sens, 1, delaiArrivee);
    }
}
