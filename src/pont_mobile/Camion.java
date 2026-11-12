package pont_mobile;

public class Camion extends Vehicule {
    public Camion(Pont pont, String nom, int sens, int delaiArrivee) {
        super(pont, nom, sens, 2, delaiArrivee);
    }
}
