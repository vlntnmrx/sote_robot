package hsurobots.components;

import java.util.List;


public abstract class Sensor {
    protected double orientationToRobot;
    protected int measurementRate;
    protected double beamWidth;

    /**
     * Erstellt einen neuen Sensor
     * @param orientationToRobot Orientierung (Blickrichtung) des Sensors relativ zur Roboter-Ausrichtung
     * @param beamWidth Strahlbreite in rad
     * @param measurementRate Messrate in Hertz
     */
    public Sensor(double orientationToRobot, double beamWidth, int measurementRate) {
        this.orientationToRobot = orientationToRobot;
        this.beamWidth = beamWidth;
        this.measurementRate = measurementRate;
    }

    /**
     * Gibt die Strahlbreite zurück
     * @return Strahlbreite in Radiant
     */
    public double getBeamWidth() {
        return beamWidth;
    }

    /**
     * Gibt die Orientierung relativ zum Roboter zurück
     * @return Orientierung in radiant
     */
    public double getOrientationToRobot() {
        return orientationToRobot;
    }

    /**
     * Gibt die Messrate in Hertz zurück
     * @return Messrate in Hertz
     */
    public int getMeasurementRate() {
        return measurementRate;
    }

    /**
     * Registrieren /Eintragen eines Observers, der bei Vorliegen neuer Sensordaten benachrichtigt werden soll
     * @param obs der Observer
     */
    public abstract void registerSensorDataObserver(ISensorDataObserver obs);

    /**
     * Wird aufgerufen, wenn neue Messdaten aus der Umgebung simuliert wurden
     * @param data die empfangenen / simulierten Messdaten aus der Umgebung des Roboters für diesen Sensor
     */
    public abstract void measurementFromEnvironment(List<SensorData> data);


}
