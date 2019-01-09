package hsurobots.gui;

import hsurobots.components.BaseRobot;
import hsurobots.internal.Controller;

public class UpdateControlThread extends Thread {

    private final double DELTA_T = 0.01;
    private final ControlGUI gui;
    private final Controller controller;
    private boolean doExit = false;

    public UpdateControlThread( Controller controller, ControlGUI gui){
        this.controller = controller;
        this.gui = gui;
    }

    public void run(){
        while (!doExit) {
            controller.calculate(DELTA_T);
            gui.repaint();
            try {
                Thread.sleep((long) (DELTA_T * 1000));
            } catch (InterruptedException ex) {
                doExit = true;
                break;
            }
        }
    }

    public void doExit() {
        this.doExit = true;
    }
}
