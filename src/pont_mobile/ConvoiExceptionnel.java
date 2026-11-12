package pont_mobile;

public class ConvoiExceptionnel extends Vehicule {
    public ConvoiExceptionnel(Pont pont, String nom, int sens, int delaiArrivee) {
        super(pont, nom, sens, 4, delaiArrivee);
    }

    @Override
    public boolean estConvoi() { 
    	return true; 
    }
}
