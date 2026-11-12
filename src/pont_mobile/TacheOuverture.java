package pont_mobile;

import java.util.TimerTask;

public class TacheOuverture extends TimerTask {
    private Pont pont;

    public TacheOuverture(Pont pont) { this.pont = pont; }

    @Override
    public void run() {
        pont.tenterOuverture(); 
    }
}
