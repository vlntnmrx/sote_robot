package hsurobots.components;

public abstract class Stearing {
    protected int velocity=0;
    protected double posX=0;
    protected double posY=0;
    protected double orientation=0;


    public Stearing(){
    }

    /**
     * Gibt die aktuelle X-Position zurück
     * @return
     */
    public double getPosX() {
        return posX;
    }

    /**
     * Gibt die aktuelle Y-Position zurück
     * @return
     */
    public double getPosY() {
        return posY;
    }

    /**
     * Gibt die aktuelle Orientierung in Radiant zurück
     * @return
     */
    public double getOrientation() {
        return orientation;
    }

    /**
     * Gibt die aktuelle Geschwindigkeit in Pixel/Sekunde zurück
     * @return
     */
    public int getVelocity() {
        return velocity;
    }



    /**
     * Berechnet und aktualisiert die neue Position des Roboters (wird weitergeleitet/delegiert an Stearing)
     * @param deltaTimeSec Zeitdifferenz, für die die Bewegung berechnet werden soll
     */
    public abstract void move(double deltaTimeSec);

    /**
     * Beschleundigt um den Betrag amount
     * @param amount
     */
    public abstract void accelerate(int amount);

    /**
     * Verlangsamt um den Betrag amount
     * @param amount
     */
    public abstract void decelerate(int amount);

    /**
     *Legt die initiale Position und Orientierung fest
     * @param posX
     * @param posY
     * @param orientation Orientierung in Radiant
     */
    public abstract void setInitialPose(int posX, int posY, double orientation);

    /**
     * Rotiert den Roboter
     * @param rotate positive Drehungen nach rechts, negative nach links
     */
    public abstract void rotate(double rotate);


}
