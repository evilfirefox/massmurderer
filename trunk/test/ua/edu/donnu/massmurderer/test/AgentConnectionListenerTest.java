/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ua.edu.donnu.massmurderer.test;

import java.io.File;
import java.io.FileInputStream;
import java.util.Arrays;
import java.util.Properties;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import ua.edu.donnu.massmurderer.agent.AgentConnectionListener;
import ua.edu.donnu.massmurderer.agent.Executor;
import ua.edu.donnu.massmurderer.agent.Main;
import ua.edu.donnu.massmurderer.common.Connection;
import ua.edu.donnu.massmurderer.common.FileRecord;
import ua.edu.donnu.massmurderer.common.Message;
import ua.edu.donnu.massmurderer.common.Message.MessageType;
import static org.junit.Assert.*;

/**
 *
 * @author Ajax
 */
public class AgentConnectionListenerTest {
    private AgentConnectionListener listener;
    private TestExecutor executor;
    private Message answer;
    private Connection conn;
    private File application;
    private Properties properties;

    private class TestExecutor extends Executor{
        public TestExecutor() {
            super(null,null,null);
        }
        @Override
        public void executorError(Exception ex) {}
        @Override
        public void shutdown() {
        }
        @Override
        public void restart() {
        }
        public void setSuccess(boolean success) {
            this.success = success;
        }
        @Override
        public void systemExit() {
            success = true;
        }
    }

    public AgentConnectionListenerTest() {
    }

    @Before
    public void setUp() throws Exception {
        properties = new Properties();
        properties.load(new FileInputStream("src/ua/edu/donnu/massmurderer/agent/mma.properties"));
        listener = new AgentConnectionListener(executor = new TestExecutor(),
            application = new File("dist/MMAgent.jar"), new File("dist/MMLoader.jar"),properties);
        conn = new Connection(null, 0, listener){
            @Override
            public void send(Message message) {
                AgentConnectionListenerTest.this.answer = message;
            }
        };
    }

    @After
    public void tearDown() {
    }

    @Test
    public void shutdown() {
        executor.setSuccess(true);
        listener.messageReceived(conn, new Message(Message.MessageType.SHUTDOWN));
        assertEquals(MessageType.SUCCESS, answer.getType());
        executor.setSuccess(false);
        listener.messageReceived(conn, new Message(Message.MessageType.SHUTDOWN));
        assertEquals(MessageType.FAIL, answer.getType());
    }

    @Test
    public void restart() {
        executor.setSuccess(true);
        listener.messageReceived(conn, new Message(Message.MessageType.RESTART));
        assertEquals(MessageType.SUCCESS, answer.getType());
        executor.setSuccess(false);
        listener.messageReceived(conn, new Message(Message.MessageType.RESTART));
        assertEquals(MessageType.FAIL, answer.getType());
    }

    @Test
    public void versionDigest() throws Exception{
        FileRecord rec = new FileRecord(application);
        answer = null;
        listener.messageReceived(conn, new Message(Message.MessageType.VERSION_DIGEST,rec.getMd5Sum()));
        assertNull(answer);
        rec.getMd5Sum()[0] += 1;
        listener.messageReceived(conn, new Message(Message.MessageType.VERSION_DIGEST,rec.getMd5Sum()));
        assertEquals(MessageType.GET_UPDATE, answer.getType());
    }

    @Test
    public void update() throws Exception{
        FileRecord updApp = new FileRecord(new File("dist/MMAgent.jar"));
        executor.setSuccess(false);
        listener.messageReceived(conn, new Message(Message.MessageType.UPDATE,updApp.getFileMessage()));
        assertTrue(executor.isSuccess());
        Thread.sleep(1000 * (Integer.parseInt(properties.getProperty("loaderWaitSec"))+1));
        assertArrayEquals(updApp.getMd5Sum(), new FileRecord(application).getMd5Sum());
    }

}