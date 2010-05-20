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

import java.net.InetAddress;
import ua.edu.donnu.massmurderer.common.Connection;
import ua.edu.donnu.massmurderer.common.ConnectionListener;
import ua.edu.donnu.massmurderer.common.DatagramConnection;
import ua.edu.donnu.massmurderer.common.Message;

/**
 * DatagramListener implementation for MMAgent.
 * Listens for datagrams on specified port.
 * @author Ajax
 */
public class AgentDatagramListener
        extends DatagramConnection{
    private Executor executor;
    private ConnectionListener connectionListener;
    private Connection connection = null;
    /**
     * Initialization of datagram listener components.
     * @param port Port to listen for UDP datagrams.
     * @param exec Executor mechanism.
     * @param connectionListener Listener implementation for MMAgent connection with server.
     * @throws Exception If datagram socket cannot be created.
     */
    public AgentDatagramListener(int port, Executor exec, ConnectionListener connectionListener) throws Exception {
        super(port);
        executor = exec;
        this.connectionListener = connectionListener;
    }
    /**
     * Message handling for MMAgent.
     * @param ia sender's address.
     * @param message Received message object.
     */
    @Override
    public void messageRecieved(InetAddress ia, Message message) {
        switch (message.getType()){
            case SHUTDOWN:
                executor.shutdown();
                break;
            case RESTART:
                executor.restart();
                break;
            case ECHO:
                if (connection !=null && connection.isRunning())
                    connection.close();
                connection = new Connection(ia, (Integer) message.getBody(), connectionListener);
                connection.open();
                new Thread(connection).start();
                break;
        }
    }
    /**
     * Sets the connection of MMAgent.
     * @param connection Specifid connection.
     */
    public void setConnection(Connection connection) {
        this.connection = connection;
    }
    /**
     * Returns the connection.
     * @return Connection with server.
     */
    public Connection getConnection() {
        return connection;
    }
    /**
     * Datagram error handling.
     * @param ex Exception thrown.
     */
    @Override
    public void datagramError(Exception ex) {
        System.err.println("Datatgram error:");
        ex.printStackTrace(System.err);
    }
}
