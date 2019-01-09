package hsurobots.components;

import georegression.metric.ClosestPoint2D_F64;
import georegression.metric.Intersection2D_F64;
import georegression.metric.UtilAngle;
import georegression.struct.line.LineSegment2D_F64;
import georegression.struct.point.Point2D_F64;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Area;
import java.awt.geom.Rectangle2D;
import java.io.File;
import java.io.FileNotFoundException;
import java.lang.reflect.Field;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;

public class Environment {
    private static final String OBJ_PART_SEPARATOR = ";";
    private static final int OBJ_PART_COUNT = 6;

    private final int width;
    private final int height;
    private String name;
    private List<EnvironmentObject> objects = new LinkedList<>();
    private List<LineSegment2D_F64> objectSegments = new LinkedList<>();


    public Environment(String name, int width, int height) {
        this.name = name;
        this.width = width;
        this.height = height;
        objects = new LinkedList<>();
    }



    public EnvironmentObject checkCollosion(BaseRobot robot) {
        int width = robot.getWidth();
        int length = robot.getLength();
        int x = robot.getPosX();
        int y = robot.getPosY();

        // create rectangle with transformed center of robot (reference of rectangle is top-left corner)
        Rectangle rBot = new Rectangle(x-length/2,y-width/2,length, width);

        for(EnvironmentObject obj : objects) {
            // Transform robot rectangle into coordinate system of object
            AffineTransform rt = new AffineTransform();
            // rotate robot rectangle around center of object opposite to object orientation
            rt.rotate(-obj.getOrientation(), obj.getX(), obj.getY());
            // rotate around robot center
            rt.rotate(robot.getOrientation(), x,y);
            Area aBotTemp = new Area(rBot);
            // create transformed area of robot rectangle
            Area aBot = aBotTemp.createTransformedArea(rt);
            // check intersection
            Rectangle2D objRect = obj.getRectangle();
            if(aBot.intersects(objRect)){
                return obj;
            }
        }
        return null;
    }


    protected List<SensorData> simulateSensorData(BaseRobot r, Sensor s){
        List<SensorData> sensData = new LinkedList<>();
        int xr = r.getPosX();
        int yr = r.getPosY();
        double robotOrientation = r.getOrientation();
        double globalOrientationOfSensor =robotOrientation+s.getOrientationToRobot();

        double beamWidth = s.getBeamWidth();
        int maxRange = calculateMaxRange(s);

        Point2D_F64 robotCenter = new Point2D_F64(xr,yr);

        double minDist = 99999;
        Point2D_F64 measPoint = null;

        double minAngle = globalOrientationOfSensor-beamWidth/2.;
        double maxAngle = globalOrientationOfSensor+beamWidth/2.;



        for(LineSegment2D_F64 objL : objectSegments) {
            // first check if objL is a valid candidate (for speedup)
            Point2D_F64 cpCheck = ClosestPoint2D_F64.closestPoint(objL, robotCenter, null);
            Point2D_F64 cpToRobot = new Point2D_F64(cpCheck.getX() - xr, cpCheck.getY() - yr);
            double distCheck = cpToRobot.norm();
            // if closest point of object segment is beyond range: we cannot see this segment
            if (distCheck > maxRange) {
                continue;
            }


            for (double a = minAngle; a <= maxAngle; a = a + 0.01) {
                LineSegment2D_F64 ray = new LineSegment2D_F64(xr, yr, xr + maxRange * Math.cos(a), yr + maxRange * Math.sin(a));
                // intersect ray with object line
                Point2D_F64 ip = Intersection2D_F64.intersection(ray, objL, null);
                if(ip==null){
                    continue;
                }

                Point2D_F64 vecToRobot = new Point2D_F64(ip.getX() - xr, ip.getY() - yr);
                double distToSensor = vecToRobot.norm();

                // only use the minimum distance in all rays
                if(distToSensor < minDist){
                    minDist = distToSensor;
                    measPoint= ip;
                }
            }
        }

        if(measPoint != null){
            Point2D_F64 vecToRobot = new Point2D_F64(measPoint.getX() - xr, measPoint.getY() - yr);
            double angleToGlobal= Math.atan2(vecToRobot.getY(), vecToRobot.getX());
            double angleToSensor = angleToGlobal - globalOrientationOfSensor;
            double distToSensor = vecToRobot.norm();
            SensorData sd = new SensorData(angleToSensor, (int)distToSensor, s);
            sensData.add(sd);
        }



        return sensData;
    }

    private int calculateMaxRange(Sensor s) {
        // max range depends on:
        // beam width and measurement rate
        // Area scanned per second is constant k:
        // (pi*r^2*alpha)/(2*pi)*f=k
        // => r = sqrt(2*k/(f*alpha))
        double k = 40000;
        double r = Math.sqrt(2*k/(s.getMeasurementRate()*s.getBeamWidth()));


        return (int)r;
    }

    public void simulateSensorData(BaseRobot r){

        List<Sensor> sensors = r.getSensors();
        for(Sensor s : sensors){
            LinkedList<SensorData> data = new LinkedList<SensorData>();
            List<SensorData> sd = simulateSensorData(r, s);
            data.addAll(sd);
            s.measurementFromEnvironment(sd);
        }


    }

    public static Environment loadFromFile(File file){


        Environment env = null;

        Scanner sc = null;
        try {
            sc = new Scanner(file);
        } catch (FileNotFoundException e) {
            throw new IllegalArgumentException("Datei "+file+" konnte nicht gelesen werden.");
        }

        if(sc!=null) {
            if(!sc.hasNext()){
                throw new IllegalArgumentException("Datei "+file+" enthält kein gültiges Environment. Datei ist leer.");
            }

            int lineCount = 0;
            String name = null;

            while (sc.hasNextLine()) {
                lineCount++;
                String objStr = sc.nextLine().trim();
                if(objStr.isEmpty()|| objStr.startsWith("#")){
                    continue;
                }
                String[] parts = objStr.split(OBJ_PART_SEPARATOR);
                if(parts.length == 1){
                    // read name
                    name = parts[0].trim();
                }
                else if (parts.length ==2){
                    // read size and create environment
                    try {
                        int w = Integer.parseInt(parts[0].trim());
                        int h = Integer.parseInt(parts[1].trim());
                        env = new Environment(name, w, h);
                    }
                    catch(Exception e){
                        throw new IllegalArgumentException("Datei "+file+" enthält kein gültiges Environment. Name und/oder Größe ungültig: "+e);
                    }
                }
                else if(parts.length!=OBJ_PART_COUNT){
                    throw new IllegalArgumentException("Datei "+file+" enthält kein gültiges Environment. Anzahl der Elemente pro Objekt ist ungleich "+OBJ_PART_COUNT+": "+parts.length);
                }
                else {
                    if(env == null){
                        throw new IllegalArgumentException("Datei "+file+" enthält kein gültiges Environment. Name und/oder Größe nicht vorhanden!");
                    }
                    try {
                        int x = Integer.parseInt(parts[0].trim());
                        int y = Integer.parseInt(parts[1].trim());
                        int w = Integer.parseInt(parts[2].trim());
                        int len = Integer.parseInt(parts[3].trim());
                        double orientation = Double.parseDouble(parts[4].trim()) * Math.PI / 180.;
                        String colorStr = parts[5].trim();
                        Color color;
                        try {
                            Field field = Color.class.getField(colorStr.toUpperCase());
                            color = (Color) field.get(null);
                        } catch (Exception e) {
                            throw new IllegalArgumentException("Datei " + file + " enthält kein gültiges Environment. Ungültiger Wert für Farbe in Zeile " + lineCount);
                        }

                        EnvironmentObject obj = new EnvironmentObject(x, y, w, len, orientation, color);
                        env.addObject(obj);
                    } catch (NumberFormatException e) {
                        throw new IllegalArgumentException("Datei " + file + " enthält kein gültiges Environment. Ungültiger Wert in Zeile " + lineCount + ": " + e);
                    }
                }

            }
        }

        if(env!=null) {
            env.calculateEnvironmentObjectLineCache();
        }

       return env;
    }

    private void calculateEnvironmentObjectLineCache() {
        for(EnvironmentObject obj : objects){
            // env objects are given with center reference point coordinate (x,y)
            double xRef,x2,x3,x4;
            double yRef,y2,y3,y4;
            // top left, rop right, bottom right, bottom left
            double length = obj.getLength();
            double width = obj.getWidth();
            double alpha = obj.getOrientation();



            xRef = obj.getX();
            yRef = obj.getY();
/*
            // top left, top right
            objectSegments.add(createLineSegmentRotated(xRef,yRef,0,0,length,0,alpha));
            // top right, bottom right
            objectSegments.add(createLineSegmentRotated(xRef,yRef,length,0,length,width,alpha));
            // bottom right, bottom left
            objectSegments.add(createLineSegmentRotated(xRef,yRef,length,width,0,width,alpha));
            // bottom left, top left
            objectSegments.add(createLineSegmentRotated(xRef,yRef,0,width,0,0,alpha));
       */
            // top left, top right
            objectSegments.add(createLineSegmentRotated(xRef,yRef,-length/2.,-width/2.,length/2.,-width/2.,alpha));
            // top right, bottom right
            objectSegments.add(createLineSegmentRotated(xRef,yRef,length/2.,-width/2.,length/2.,width/2.,alpha));
            // bottom right, bottom left
            objectSegments.add(createLineSegmentRotated(xRef,yRef,length/2.,width/2.,-length/2., width/2.,alpha));
            // bottom left, top left
            objectSegments.add(createLineSegmentRotated(xRef,yRef,-length/2.,width/2.,-length/2., -width/2.,alpha));

        }
    }

    private LineSegment2D_F64 createLineSegmentRotated(double xRef, double yRef, double x1, double y1, double x2, double y2,double alpha) {
        double sinA = Math.sin(alpha);
        double cosA = Math.cos(alpha);

        double px1 = x1*cosA -y1*sinA + xRef;
        double py1 = x1*sinA + y1*cosA + yRef;

        double px2 = x2*cosA -y2*sinA + xRef;
        double py2 = x2*sinA + y2*cosA + yRef;

        LineSegment2D_F64 res = new LineSegment2D_F64(px1, py1, px2, py2);
        return res;
    }


    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    private void addObject(EnvironmentObject obj) {
        this.objects.add(obj);
    }

    public String getName() {
        return name;
    }

    public List<EnvironmentObject> getObjects() {
        return objects;
    }

    public boolean checkTargetZone(BaseRobot robot) {
        return(robot.getPosX() >= getWidth());
    }
}
