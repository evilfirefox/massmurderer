/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ua.edu.donnu.massmurderer.test;

import org.junit.Before;
import org.junit.Test;
import ua.edu.donnu.massmurderer.common.Connection;
import ua.edu.donnu.massmurderer.common.ConnectionListener;
import ua.edu.donnu.massmurderer.common.FileMessage;
import ua.edu.donnu.massmurderer.common.Message;
import ua.edu.donnu.massmurderer.common.Message.MessageType;
import static org.junit.Assert.*;

/**
 *
 * @author Ajax
 */
public class ServerConnectionListenerTest {
    private Message answer;
    private Connection conn;
    private ConnectionListener listener = null;

    public ServerConnectionListenerTest() {
    }

    @Before
    public void setUp() throws Exception {
        // TODO : Create listener for server-side connection
        // An Exception will be thrown in next line if listener won't be created.
        conn = new Connection(null, 0, listener){
            @Override
            public void send(Message message) {
                ServerConnectionListenerTest.this.answer = message;
            }
        };
    }

    @Test
    public void connectionOpened(){
        answer = null;
        listener.connectionOpened(conn);
        assertEquals(MessageType.VERSION_DIGEST, answer.getType());
        byte[] md5sum = (byte[]) answer.getBody();
        assertEquals(16, md5sum.length);
    }

    @Test
    public void getUpdate(){
        answer = null;
        listener.messageReceived(conn, new Message(MessageType.GET_UPDATE));
        assertEquals(MessageType.UPDATE, answer.getType());
        FileMessage fm = (FileMessage) answer.getBody();
        assertNotNull(fm.getFileName());
        assertNotNull(fm.getData());
        assertTrue(fm.getData().length>0);
    }

}