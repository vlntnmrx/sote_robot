package hsurobots.internal;

import hsurobots.components.BaseRobot;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;

public class RobotLoader {
    public static BaseRobot loadRobotFromJar(File file) {
        BaseRobot robot = null;
        URL url = null;
        try {
            url = new URL("jar:"+file.toURI().toURL().toString()+"!/");
        } catch (MalformedURLException e) {
            throw new IllegalArgumentException("No valid URL: "+e);
        }

        URLClassLoader cl = new URLClassLoader( new URL[]{ url });
        try {
            Class.forName("hsurobots.components.RobotFactory", true,   cl);
        } catch (ClassNotFoundException e) {
            throw new IllegalArgumentException(e);
        }

        Class<?> c = null;
        try {
            c = cl.loadClass( "hsurobots.components.RobotFactory" );
        } catch (ClassNotFoundException e) {
            throw new IllegalArgumentException("Cannot load class RobotFactory from jar: "+e);
        }

        try {
            Method createMethod = c.getMethod("createRobot", null);
            Object obj= createMethod.invoke(null);
            try {
                robot = (BaseRobot) obj;
            }
            catch(ClassCastException e){
                throw new IllegalArgumentException(e);
            }
        } catch (NoSuchMethodException e) {
            throw new IllegalArgumentException("Cannot find method createRobot in class RobotFactory: "+e);
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new IllegalArgumentException("Cannot invoke method createRobot in class RobotFactory: "+e);
        }
        return robot;

    }
}
