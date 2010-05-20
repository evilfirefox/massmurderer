/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ua.edu.donnu.massmurderer.test;

import java.net.InetAddress;
import java.net.ServerSocket;
import org.junit.Test;
import ua.edu.donnu.massmurderer.common.Connection;
import ua.edu.donnu.massmurderer.common.ConnectionListener;
import ua.edu.donnu.massmurderer.common.Message;
import ua.edu.donnu.massmurderer.common.Message.MessageType;
import static org.junit.Assert.*;

/**
 *
 * @author Ajax
 */
public class ConnectionTest {
    private Connection srvConn;
    private Connection cliConn;
    private ConnectionListener srvConnListener;
    private ConnectionListener cliConnListener;
    private Message srvReceived;
    private Message cliReceived;
    private boolean srvOpened;
    private boolean cliOpened;
    private boolean srvClosed;
    private boolean cliClosed;
    private static final int port = 1024;
    private final ServerSocket server;

    public ConnectionTest() throws  Exception{
        srvConnListener = new ConnectionListener() {
            public void connectionOpened(Connection conn) {
                srvOpened = true;
            }
            public void connectionClosed(Connection conn) {
                srvClosed = true;
            }
            public void messageReceived(Connection conn, Message message) {
                srvReceived = message;
                conn.send(message);
            }
            public void connectionError(Connection conn, Exception ex) {
            }
        };
        cliConnListener = new ConnectionListener() {
            public void connectionOpened(Connection conn) {
                cliOpened = true;
                conn.send(new Message(Message.MessageType.ECHO));
            }
            public void connectionClosed(Connection conn) {
                cliClosed = true;
            }
            public void messageReceived(Connection conn, Message message) {
                cliReceived = message;
            }
            public void connectionError(Connection conn, Exception ex) {
            }
        };
        server = new ServerSocket(port);
        Runnable connListener = new Runnable() {
            public void run() {
                while(true){
                    try{
                        srvConn = new Connection(server.accept(),srvConnListener);
                    } catch (Exception ex){
                    }
                }
            }
        };
        new Thread(connListener).start();
        cliConn = new Connection(InetAddress.getLocalHost(), port, cliConnListener);
    }

    @Test
    public void connectionTest() throws Exception {
        cliConn.open();
        new Thread(srvConn).start();
        new Thread(cliConn).start();
        Thread.sleep(1000);
        assertTrue(srvOpened);
        assertTrue(cliOpened);
        assertEquals(MessageType.ECHO, srvReceived.getType());
        assertEquals(MessageType.ECHO, cliReceived.getType());
        for (MessageType mt : MessageType.values()){
            if (!mt.equals(MessageType.CLOSE)){
                cliConn.send(new Message(mt));
                Thread.sleep(100);
                assertEquals(mt, srvReceived.getType());
                assertEquals(mt, cliReceived.getType());
            }
        }
        byte [] data;
        for (int i=1; i<=10; i++){
            data = new byte[i * 1024 * 1024];
            cliConn.send(new Message(MessageType.UPDATE,data));
            Thread.sleep(100 * i);
            assertArrayEquals(data, (byte[])srvReceived.getBody());
            assertArrayEquals(data, (byte[])cliReceived.getBody());
        }
        cliConn.close();
        Thread.sleep(100);
        assertTrue(srvClosed);
        assertTrue(cliClosed);
    }

}