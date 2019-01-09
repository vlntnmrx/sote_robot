package hsurobots.components;

import java.util.LinkedList;
import java.util.List;

public class MySensor extends Sensor {
    List<ISensorDataObserver> spamListe;

    /**
     * Erstellt einen neuen Sensor
     *
     * @param orientationToRobot Orientierung (Blickrichtung) des Sensors relativ zur Roboter-Ausrichtung
     * @param beamWidth          Strahlbreite in rad
     * @param measurementRate    Messrate in Hertz
     */
    public MySensor(double orientationToRobot, double beamWidth, int measurementRate) {
        super(orientationToRobot, beamWidth, measurementRate);
        spamListe = new LinkedList<>();
    }

    @Override
    public void registerSensorDataObserver(ISensorDataObserver obs) {
        spamListe.add(obs);
    }

    @Override
    public void measurementFromEnvironment(List<SensorData> data) {
        if (data.size() == 0) {
            return;
        }
        for (ISensorDataObserver aSpamListe : spamListe) {
            aSpamListe.onSensorMeasurement(data.get(0));
        }
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof MySensor) {
            return this.orientationToRobot == ((MySensor) obj).getBeamWidth() && this.beamWidth == ((MySensor) obj).getBeamWidth();
        }
        return false;
    }
}
