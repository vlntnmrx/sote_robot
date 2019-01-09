package hsurobots;

import hsurobots.gui.ControlGUI;

import javax.swing.*;

public class Main {



    public static void main(String[] args) {

        try {
            UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
        } catch (ClassNotFoundException e) {
        } catch (InstantiationException e) {
        } catch (IllegalAccessException e) {
        } catch (UnsupportedLookAndFeelException e) {
        }

        ControlGUI guiFrame = new ControlGUI();
        guiFrame.setVisible(true);
    }


}
