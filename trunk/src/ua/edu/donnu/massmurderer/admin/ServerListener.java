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

import java.net.Socket;

/**
 * interface for server events listener
 * @see Server server to listen
 * @since 1.0
 * @author Ajax
 */
public interface ServerListener {

    /**
     * new connection established
     * @since 1.0
     * @param socket connection socket
     */
    public void newConnection(Socket socket);

    /**
     * server started event
     * @since 1.0
     * @param srv server instance
     */
    public void serverStarted(Server srv);

    /**
     * server stopped event
     * @since 1.0
     * @param srv server instance
     */
    public void serverStopped(Server srv);

    /**
     * an error occured on server while executing
     * @param srv server instance
     * @param ex exception object
     */
    public void serverError(Server srv, Exception ex);
}
