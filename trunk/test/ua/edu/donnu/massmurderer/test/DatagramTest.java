/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ua.edu.donnu.massmurderer.test;

import java.net.InetAddress;
import java.net.Socket;
import java.util.Random;
import org.junit.Test;
import ua.edu.donnu.massmurderer.admin.Server;
import ua.edu.donnu.massmurderer.admin.ServerListener;
import ua.edu.donnu.massmurderer.agent.AgentConnectionListener;
import ua.edu.donnu.massmurderer.agent.AgentDatagramListener;
import ua.edu.donnu.massmurderer.agent.Executor;
import ua.edu.donnu.massmurderer.common.Connection;
import ua.edu.donnu.massmurderer.common.ConnectionListener;
import ua.edu.donnu.massmurderer.common.DatagramConnection;
import static org.junit.Assert.*;
import ua.edu.donnu.massmurderer.common.Message;
import ua.edu.donnu.massmurderer.common.Message.MessageType;

/**
 *
 * @author Ajax
 */
public class DatagramTest {
    public static final int port = 1024;
    private Message mess = null;

    public DatagramTest() {

    }

    @Test
    public void connectionTest() throws Exception{
        DatagramConnection dc = new DatagramConnection(port) {
            @Override
            public void messageRecieved(InetAddress ia, Message message) {
                DatagramTest.this.mess = message;
            }
            @Override
            public void datagramError(Exception ex) {
                ex.printStackTrace();
            }
        };
        new Thread(dc).start();
        DatagramConnection.send(new Message(MessageType.ECHO), InetAddress.getLocalHost(), port);
        Thread.sleep(1000);
        assertEquals(MessageType.ECHO, mess.getType());
        byte [] data = new byte[1024];
        Random r = new Random();
        r.nextBytes(data);
        DatagramConnection.send(new Message(MessageType.SUCCESS, data), InetAddress.getLocalHost(), port);
        Thread.sleep(1000);
        assertEquals(MessageType.SUCCESS, mess.getType());
        assertArrayEquals(data, (byte[])mess.getBody());
        dc.close();
    }

    @Test
    public void echoTest()throws Exception{
        Executor exec = new Executor(null, null, null) {
            @Override
            public void executorError(Exception ex) {
                throw new Error(ex.getMessage());
            }
        };
        Server srv = new Server(port+1, port, new ServerListener() {
            public void newConnection(Socket socket) {
                Connection conn = new Connection(socket,
                        new ConnectionListener() {
                    public void connectionOpened(Connection conn) {}
                    public void connectionClosed(Connection conn) {}
                    public void messageReceived(Connection conn, Message message) {
                        DatagramTest.this.mess = message;
                        assertEquals(MessageType.ECHO, message.getType());
                    }
                    public void connectionError(Connection conn, Exception ex) {
                        throw new Error(ex.getMessage());
                    }
                });
                new Thread(conn).start();
            }
            public void serverStarted(Server srv) {}
            public void serverStopped(Server srv) {}
            public void serverError(Server srv, Exception ex) {
                throw new Error(ex.getMessage());
            }
        });
        AgentDatagramListener listener = new AgentDatagramListener(port, exec,
                new AgentConnectionListener(exec, null, null, null));
        new Thread(srv).start();
        new Thread(listener).start();
        mess = null;
        DatagramConnection.send(new Message(MessageType.ECHO, port+1), InetAddress.getLocalHost(), port);
        Thread.sleep(1000);
        assertEquals(MessageType.ECHO, mess.getType());
        srv.stop();
        listener.close();
    }

}