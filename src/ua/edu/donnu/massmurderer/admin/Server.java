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
package ua.edu.donnu.massmurderer.admin;

import java.net.ServerSocket;

/**
 * tcp server implementation
 * @version 1.0
 * @author Ajax
 */
public class Server implements Runnable {

    private ServerListener listener;
    private int listeningPort;
    private int backlog;
    private ServerSocket serverSocket;
    private boolean running = false;

    /**
     * Server logic. Starts listening on a specified port with specified backlog parameter.
     * Object of Server class may be running in new thread.
     * <i>listener</i> must implement connection and error handling.
     * @since 1.0
     * @param port the port on a local machine which will be listened to.
     * @param backlog total connections number in the queue.
     * @param listener interface implementation for server event handling
     * @param connectionListener interface implementation for new connection's event handling
     */
    public Server(int port, int backlog, ServerListener listener) {
        this.listener = listener;
        if (this.listener == null) {
            throw new NullPointerException("Listener must be specified");
        }
        this.listeningPort = port;
        try {
            serverSocket = new ServerSocket(port, backlog);
            running = true;
            listener.serverStarted(this);
        } catch (Exception ex) {
            this.listener.serverError(this, ex);
        }
    }

    /**
     * Thread method for listening for incoming connections.
     * @since 1.0
     */
    public void run() {
        while (running) {
            try {
                listener.newConnection(serverSocket.accept());
            } catch (Exception ex) {
                if (running) {
                    listener.serverError(this, ex);
                }
            }
        }
    }

    /**
     * Stop listening.
     * @since 1.0
     */
    public void stop() {
        running = false;
        try {
            serverSocket.close();
            listener.serverStopped(this);
        } catch (Exception ex) {
            listener.serverError(this, ex);
        }
    }
}
