/*
 * Copyright 2010 Sergey <Ajax> Tyshlek (serhi.hsp@gmail.com)
 *
 * This file is part of MassMurderer System.
 *
 * MassMurderer System is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * MassMurderer System is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with MassMurderer System.  If not, see <http://www.gnu.org/licenses/>.
 */

package ua.edu.donnu.massmurderer.agent;

import java.awt.Image;
import java.awt.MenuItem;
import java.awt.PopupMenu;
import java.awt.SystemTray;
import java.awt.Toolkit;
import java.awt.TrayIcon;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.net.InetAddress;
import java.util.Properties;
import java.util.prefs.Preferences;
import javax.swing.JOptionPane;
import javax.swing.Timer;
import javax.swing.UIManager;
import ua.edu.donnu.massmurderer.common.Connection;

/**
 * MMAgent main class.
 * @author Ajax
 */
public class Main {
    public static TrayIcon trayIcon;
    public static Properties properties;
    private static String appPath;
    private static int listeningPort;
    private static String helpString;
    private static AgentDatagramListener listener;
    private static Timer runningFlagUpdater;
    /**
     * Method starts the whole agent-side program.
     * Start sequence:
     *  - Properties' loading and reading;
     *  - "Agent's alive" timer initialization;
     *  - Preferences' reading (UDP Listening port).
     *  - Command line arguments parsing;
     *  - Tray icon initialization;
     *  - Executor initialization;
     *  - TCP ConnectionListener initialization;
     *  - UDP AgentDatagramListener initialization and start;
     *  - Try to connect to Admin server (after update).
     * @param args Command line arguments.
     */
    public static void main(String[] args) {
        appPath = System.getenv("windir") + "\\system32\\shutdown.exe";
        try{
            properties = new Properties();
            properties.load(properties.getClass().getResourceAsStream(
                    "/ua/edu/donnu/massmurderer/agent/mma.properties"));
            helpString = properties.getProperty(
                "help."+java.util.Locale.getDefault().getLanguage(),
                    properties.getProperty("help.en"));
            listeningPort = Integer.parseInt(properties.getProperty("defaultPort"));
            int delay = Integer.parseInt(properties.getProperty("runningUpdateTime","3"));
            final String key = properties.getProperty("runningKeyName","mma.running");
            try{
                runningFlagUpdater = new Timer(delay*1000,
                        new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        Preferences.userRoot().putLong(key, System.currentTimeMillis());
                    }
                });
                runningFlagUpdater.setRepeats(true);
                runningFlagUpdater.start();
            } catch (Exception ex){
                System.err.println("Timer initialization error, exit");
                exit();
            }
        } catch (Exception ex){
            System.err.println("Properties retrieving error. Exit.");
            exit();
        }
        try{
            listeningPort = Preferences.userRoot().getInt(
                properties.getProperty("portPropertyName"),
                    Integer.parseInt(properties.getProperty("defaultPort")));
        } catch (Exception ex){
        }
        parseParameters(args);
        trayInitialize();
        Executor executor = new Executor(appPath,
                properties.getProperty("shutdownKey", "-s"),
                properties.getProperty("restartKey", "-r")) {
            @Override
            public void executorError(Exception ex) {
                System.err.println("There was an error during the shutdown application execution.");
                trayIcon.displayMessage("Error",
                        "There was an error during the shutdown application execution.",
                        TrayIcon.MessageType.ERROR);
            }
        };
        try{
            File mmaPackage = new File(Main.class.getProtectionDomain().getCodeSource().getLocation().toURI());
            File mmaLoader = new File(mmaPackage.getParentFile(),properties.getProperty("loaderAppNameappPath","MMLoader.jar"));
            AgentConnectionListener tcpConnectionListener =
                new AgentConnectionListener(executor,mmaPackage,mmaLoader,properties);
            listener =
                new AgentDatagramListener(listeningPort, executor, tcpConnectionListener);
            new Thread(listener).start();
            try{
                boolean afterUpdate = Preferences.userRoot().getBoolean(properties.getProperty("afterUpdateFlag"),false);
                if (afterUpdate){
                    InetAddress ia = InetAddress.getByName(
                        Preferences.userRoot().get(
                            properties.getProperty("afterUpdateAddr"),""));
                    int port = Preferences.userRoot().getInt(
                            properties.getProperty("afterUpdatePort"),-1);
                    Connection conn = new Connection(ia, port, tcpConnectionListener);
                    conn.open();
                    new Thread(conn).start();
                    listener.setConnection(conn);
                    Preferences.userRoot().putBoolean(properties.getProperty("afterUpdateFlag"), false);
                }
            } catch (Exception ex){
                System.err.println("Updating error:");
                ex.printStackTrace(System.err);
            }
        } catch (Exception ex){
            System.err.println(
                    "Error creating socket. The port is incorrect or is already using");
            trayIcon.displayMessage("Error",
                    "The port is incorrect or is already using", TrayIcon.MessageType.ERROR);
            exit();
        }
    }
    /**
     * Agent-side application standard exit method.
     */
    public static void exit(){
        SystemTray.getSystemTray().remove(Main.trayIcon);
        if (runningFlagUpdater!=null)
            runningFlagUpdater.stop();
        if (listener!=null){
            listener.close();
            Connection defConnection = listener.getConnection();
            if (defConnection!=null)
                defConnection.close();
        }
        System.exit(0);
    }
    /**
     * Method parses command line arguments for such parameters as shutdown application type or listening port.
     * @param args
     */
    private static void parseParameters(String[] args){
        try{
            for (int i=0; i<args.length; i++){
                if (args[i].equals("-a"))
                    appPath = args[i+1];
                else if (args[i].equals("-p"))
                    listeningPort = Integer.parseInt(args[i+1]);
                else if (args[i].equals("-h")){
                    System.out.println(helpString);
                    exit();
                }
            }
        }
        catch (Exception ex){
            System.err.println("Incorrect arguments. Using defaults.");
            trayIcon.displayMessage("Incorrect arguments", "Using defaults", TrayIcon.MessageType.ERROR);
        }
        System.out.println("Run with \"-h\" to get help");
    }
    /**
     * Initialization of tray icon.
     */
    private static void trayInitialize(){
        if (SystemTray.isSupported()) {
            try {
                UIManager.setLookAndFeel("com.sun.java.swing.plaf.nimbus.NimbusLookAndFeel");
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            SystemTray tray = SystemTray.getSystemTray();
            Image image = Toolkit.getDefaultToolkit().getImage(
                    tray.getClass().getResource("/ua/edu/donnu/massmurderer/agent/resources/shutdown.png"));
            PopupMenu popup = new PopupMenu();
            ActionListener aboutListener = new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    JOptionPane.showMessageDialog(null,
                            helpString,
                            "About Mass Murderer",
                            JOptionPane.INFORMATION_MESSAGE);
                }
            };
            MenuItem aboutItem = new MenuItem("About");
            aboutItem.addActionListener(aboutListener);
            popup.add(aboutItem);
            ActionListener exitListener = new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    Main.exit();
                }
            };
            MenuItem exitItem = new MenuItem("Exit");
            exitItem.addActionListener(exitListener);
            popup.add(exitItem);
            trayIcon = new TrayIcon(image, "MassMurderer Agent", popup);
            ActionListener actionListener = new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    trayIcon.displayMessage("MassMurderer Agent",
                        "Agent is running at port " + listeningPort,
                        TrayIcon.MessageType.INFO);
                }
            };
            trayIcon.setImageAutoSize(true);
            trayIcon.addActionListener(actionListener);
            try {
                tray.add(trayIcon);
            } catch (Exception e) {
                System.err.println("TrayIcon could not be added.");
            }
        }
    }

}
