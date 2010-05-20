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

import java.io.File;
import java.io.FileOutputStream;
import java.util.Arrays;
import java.util.Properties;
import java.util.prefs.Preferences;
import ua.edu.donnu.massmurderer.common.Connection;
import ua.edu.donnu.massmurderer.common.ConnectionListener;
import ua.edu.donnu.massmurderer.common.FileMessage;
import ua.edu.donnu.massmurderer.common.FileRecord;
import ua.edu.donnu.massmurderer.common.Message;
import ua.edu.donnu.massmurderer.common.Message.MessageType;

/**
 * Implementation of ConnectionListener for agent side.
 * @author Ajax
 */
public class AgentConnectionListener implements ConnectionListener{

    private Executor executor;
    private File application;
    private File loader;
    private Properties properties;
    /**
     * Initializes necessary components for listener.
     * @param executor Shutdown executor
     * @param application Path to MMAgent.jar application (needed for update operation)
     * @param loader Path to MMLoader.jar application (needed for update operation)
     * @param properties
     */
    public AgentConnectionListener(Executor executor, File application, File loader, Properties properties) {
        this.executor = executor;
        this.application = application;
        this.loader = loader;
        this.properties = properties;
    }
    /**
     * Responds with ECHO answer.
     * @param conn
     */
    public void connectionOpened(Connection conn) {
        conn.send(new Message(MessageType.ECHO));
    }
    /**
     * Do nothing.
     * @param conn
     */
    public void connectionClosed(Connection conn) {
    }
    /**
     * Message dispatcher. Handling of messages with types: SHUTDOWN, RESTART, VERSION_DIGEST, UPDATE.
     * @param conn
     * @param message
     */
    public void messageReceived(Connection conn, Message message) {
        switch(message.getType()){
            case SHUTDOWN:
                executor.shutdown();
                conn.send(new Message((executor.isSuccess() ?
                    MessageType.SUCCESS : MessageType.FAIL)));
                break;
            case RESTART:
                executor.restart();
                conn.send(new Message((executor.isSuccess() ?
                    MessageType.SUCCESS : MessageType.FAIL)));
                break;
            case VERSION_DIGEST:
                byte [] newVersionDigest = (byte[])message.getBody();
                try{
                    FileRecord old = new FileRecord(application);
                    if (!Arrays.equals(old.getMd5Sum(), newVersionDigest)){
                        conn.send(new Message(MessageType.GET_UPDATE));
                        if (Main.trayIcon!=null)
                            Main.trayIcon.displayMessage("Old version",
                                "The new version will be requested",
                                java.awt.TrayIcon.MessageType.WARNING);
                    }
                } catch (Exception ex){
                    throw new Error(ex.getMessage());
                }
                break;
            case UPDATE:
                try{
                    FileMessage fm = (FileMessage) message.getBody();
                    File newVersion = new File(application.getParentFile(),
                            properties.getProperty("newVersionPrefix","upd") + fm.getFileName());
                    FileOutputStream fos = new FileOutputStream(newVersion);
                    fos.write(fm.getData());
                    fos.close();
                    String runStr = properties.getProperty("loaderRunString");
                    runStr = runStr.replace("%MMLOADER%", loader.getCanonicalPath());
                    runStr = runStr.replace("%OLDNAME%",application.getCanonicalPath());
                    runStr = runStr.replace("%NEWNAME%",newVersion.getCanonicalPath());
                    Runtime.getRuntime().exec(runStr);
                    try{
                        Preferences.userRoot().put(properties.getProperty("afterUpdateAddr"), conn.getRemoteAddress().getHostAddress());
                        Preferences.userRoot().putInt(properties.getProperty("afterUpdatePort"), conn.getRemotePort());
                        Preferences.userRoot().putBoolean(properties.getProperty("afterUpdateFlag"), true);
                    } catch (Exception ex){
                    }
                    executor.systemExit();
                } catch (Exception ex){
                    throw new Error(ex.getMessage());
                }
                break;
        }
    }
    /**
     * Error handler. Writes exception stack to standard error output stream.
     * @param conn
     * @param ex
     */
    public void connectionError(Connection conn, Exception ex) {
        System.err.println("Connection error:");
        ex.printStackTrace(System.err);
    }

}
