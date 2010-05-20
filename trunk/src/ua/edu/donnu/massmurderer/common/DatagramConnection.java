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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

/**
 *
 * @author Ajax
 */
public abstract class DatagramConnection
        implements Runnable{
    public final static int BUFFER_LENGTH = 65507;
    private byte[] buffer = new byte[BUFFER_LENGTH];
    private DatagramSocket socket;
    private boolean running;
    /**
     * Creates Datagram sendSocket for listening
     * @param port sprcifies port for listening
     */
    public DatagramConnection(int port) throws Exception {
        socket = new DatagramSocket(port);
        running = true;
    }
    /**
     * Static method for sending data to specified client
     * @param message message object
     * @param address specified client's address
     * @param port specified client's port
     */
    public static void send(Message message, InetAddress address, int port) throws Exception{
        DatagramSocket sendSocket = new DatagramSocket();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(baos);
        oos.writeObject(message);
        oos.close();
        baos.close();
        byte [] buffer = baos.toByteArray();
        DatagramPacket packet = new DatagramPacket(buffer, buffer.length, address, port);
        sendSocket.send(packet);
        sendSocket.close();
    }
    /**
     * Message listening mechanism. Runnable.run() implementation, can be runned in new thread.
     */
    public void run() {
        DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
        ObjectInputStream ois = null;
        ByteArrayInputStream bais = new ByteArrayInputStream(buffer);
        Message message;
        while (running){
            try{
                socket.receive(packet);
                bais.reset();
                ois = new ObjectInputStream(bais);
                message = (Message) ois.readObject();
                ois.close();
                messageRecieved(packet.getAddress(), message);
            } catch (Exception ex){
                if (running)
                    datagramError(ex);
            }
        }
        try{
            bais.close();
        } catch (Exception ex){
            ex.printStackTrace();
        }
    }
    /**
     * Closes Datagram connection.
     */
    public void close(){
        running = false;
        socket.close();
    }
    /**
     * Returns local port on which datagram socket is created.
     * @return the local port of datagram socket.
     */
    public int getPort(){
        return socket.getLocalPort();
    }
    /**
     * Message handling mechanism implementation
     * @param ia sender address
     * @param message received message
     */
    public abstract void messageRecieved(InetAddress ia, Message message);
    /**
     * * Error handling mechanism implementation
     * @param ex Exception throws
     */
    public abstract void datagramError(Exception ex);

}
