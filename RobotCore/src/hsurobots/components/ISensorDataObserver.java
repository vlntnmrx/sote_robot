package hsurobots.components;

/**
 * Muss von jedem Empfänger für Sensordaten implementiert werden.
 */
public interface ISensorDataObserver {
    /**
     * Wird aufgerufen, wenn neue Sensordaten vorliegen
     * @param data die neuen Sensordaten
     */
    public void onSensorMeasurement(SensorData data);
}
