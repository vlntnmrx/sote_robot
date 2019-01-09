package hsurobots.components;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.LinkedList;
import java.util.List;

public class Roboter extends BaseRobot implements ISensorDataObserver {

    private int width;
    private int length;
    private List<Sensor> sensorList;

    /**
     * erstellt einen Roboter
     *
     * @param name
     * @param stearing Steuerungs-Objekt zur Steuerung des Roboters
     */
    public Roboter(String name, Stearing stearing, int width, int length) {
        super(name, stearing);
        sensorList = new LinkedList<>();
        this.width = width;
        this.length = length;
    }

    @Override
    public List<Sensor> getSensors() {
        return sensorList;
    }

    @Override
    public void addSensor(Sensor sensor) {
        sensor.registerSensorDataObserver(this);
        sensorList.add(sensor);
    }

    @Override
    public void setAutonomousStearing(boolean enabled) {
        int px = (int) this.stearing.getPosX();
        int py = (int) this.stearing.getPosY();
        double po = this.stearing.getOrientation();
        if (enabled) {
            this.stearing = new AutoSteuerung();
            this.stearing.setInitialPose(px, py, po);
        } else {
            this.stearing = new Steuerung();
            this.stearing.setInitialPose(px, py, po);
        }
    }

    @Override
    public Image getImage() {
        BufferedImage img = new BufferedImage(this.width, this.length, BufferedImage.TYPE_INT_BGR);
        Graphics g = img.createGraphics();
        g.setColor(new Color(1, 87, 155));
        g.fillRect(0, 0, 100, 100);
        g.setColor(new Color(8, 230, 90));
        if (this.stearing instanceof AutoSteuerung)
            g.setColor(new Color(183, 28, 28));
        g.fillOval(0, 0, 6, 10);
        g.fillOval(0, 10, 6, 10);
        return img;
    }

    @Override
    public int getWidth() {
        return width;
    }

    @Override
    public int getLength() {
        return length;
    }

    @Override
    public void onSensorMeasurement(SensorData data) {
        if (stearing instanceof AutoSteuerung) {
            ((AutoSteuerung) stearing).updateData(data);
        }
    }
}
