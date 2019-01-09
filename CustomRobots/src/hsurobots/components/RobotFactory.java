package hsurobots.components;

public class RobotFactory {

    public static BaseRobot createRobot() {
        // Dann müssen die Sensoren instantiiert werden, die dann mit der Methode addSensor der Roboter-Instanz übergeben werden
        Stearing st = new Steuerung();
        Roboter robot = new Roboter("Marvin", st, 10, 20);
        robot.addSensor(new MySensor(0, 45. / 180 * Math.PI, 10));
        robot.addSensor(new MySensor(Math.PI / 4.0, 45. / 180 * Math.PI, 10));
        robot.addSensor(new MySensor((7. / 4.) * Math.PI, 45. / 180 * Math.PI, 10));
        robot.addSensor(new MySensor(Math.PI / 2.0, 45. / 180 * Math.PI, 10));
        robot.addSensor(new MySensor((Math.PI / 2.0) + Math.PI, 45. / 180 * Math.PI, 10));
        return robot;
    }
}
