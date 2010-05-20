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

package ua.edu.donnu.massmurderer.common;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketException;
import ua.edu.donnu.massmurderer.common.Message.MessageType;

/**
 * Base class for TCP connections. ConnectionListener must be created and specified.
 * @author Ajax
 */
public class Connection
        implements Runnable{
    private InetAddress serverAddress;
    private int serverPort;
    private ConnectionListener listener;
    private Socket socket;
    private ObjectOutputStream oos;
    private ObjectInputStream ois;
    private boolean running = false;
    /**
     * Initialization for manual connection to server
     * @param address server address
     * @param port server port
     * @param listener interface implementation for event handling
     */
    public Connection(InetAddress address, int port, ConnectionListener listener){
        this(listener);
        serverAddress = address;
        serverPort = port;
    }
    /**
     * Opens manual connection
     */
    public void open(){
        try{
            socket = new Socket(serverAddress, serverPort);
            streamInitialization();
            running = true;
            listener.connectionOpened(this);
        } catch (Exception ex){
            listener.connectionError(this, ex);
        }
    }
    /**
     * Initialization for connections accepted by server side
     * @param socket accepted socket
     * @param listener interface implementation for event handling
     */
    public Connection(Socket socket, ConnectionListener listener){
        this(listener);
        this.socket = socket;
        try{
            streamInitialization();
            running = true;
            this.listener.connectionOpened(this);
        } catch (Exception ex){
            this.listener.connectionError(this, ex);
        }
    }
    /**
     * Returns the address to which the socket is connected.
     * @return the remote IP address, or null if the socket is not connected.
     */
    public InetAddress getRemoteAddress() {
        return socket.getInetAddress();
    }
    /**
     * Returns the port to which the socket is connected.
     * @return the remote port number, or 0 if the socket is not connected.
     */
    public int getRemotePort() {
        return socket.getPort();
    }
    /**
     * Returns specified connection listener
     * @return the connection listener
     */
    public ConnectionListener getListener() {
        return listener;
    }
    /**
     * Returns the running state of the connection.
     * @return true if the connection is succesfully running.
     */
    public boolean isRunning(){
        return running;
    }
    /**
     * Sends message to other point
     * @param message message containing data
     */
    public void send(Message message){
        try{
            oos.writeObject(message);
            oos.flush();
        } catch (Exception ex){
            listener.connectionError(this, ex);
        }
    }
    /**
     * Thread method implementation.
     * Waiting for incoming data, calling listener's messageReceived() method,
     * processing connection closing with other side.
     */
    public void run() {
        Message message=null;
        while(running){
            try{
                message = (Message) ois.readObject();
                switch (message.getType()){
                    case CLOSE:
                        shut();
                        break;
                    default:
                        listener.messageReceived(this, message);
                }
            } catch (SocketException ex){
                shut();
            }
            catch (Exception ex){
                if (running)
                    listener.connectionError(this, ex);
            }
        }
    }
    /**
     * Proper connection closing. A CLOSE message is sending.
     * Both sides sends CLOSE message to each other,
     * then the connection will be closed.
     */
    public void close(){
        send(new Message(MessageType.CLOSE));
        shut();
    }
    /**
     * Initializes interface implementation for event handling
     * @param listener
     */
    private Connection(ConnectionListener listener){
        this.listener = listener;
        if (this.listener==null)
            throw new NullPointerException("Listener must be specified");
    }
    /**
     * Initializes Object Input & Output Streams, based on socket streams
     * @throws Exception if the socket is invalid for some reason
     */
    private void streamInitialization() throws Exception{
        oos = new ObjectOutputStream(socket.getOutputStream());
        ois = new ObjectInputStream(socket.getInputStream());
    }
    /**
     * Forced connection shut down.
     * Used only after connection's proper closing in either way.
     */
    private void shut(){
        try{
            running = false;
            oos.close();
            ois.close();
            socket.close();
            listener.connectionClosed(this);
        } catch (Exception ex){
            listener.connectionError(this, ex);
        }
    }
}
