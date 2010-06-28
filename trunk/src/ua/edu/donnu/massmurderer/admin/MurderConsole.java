/*
 * Copyright Dan A. "devastator" Haman <dan.haman at yahoo.co.uk>, 2010.
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

/*
 * MurderConsole.java
 * 11:51:30 02.04.2010
 */
package ua.edu.donnu.massmurderer.admin;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Locale;
import java.util.Properties;
import java.util.ResourceBundle;
import java.util.Timer;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.UIManager;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;
import ua.edu.donnu.massmurderer.admin.models.ConnListModel;
import ua.edu.donnu.massmurderer.admin.models.GroupsModel;
import ua.edu.donnu.massmurderer.admin.models.IpListModel;
import ua.edu.donnu.massmurderer.common.Connection;
import ua.edu.donnu.massmurderer.common.ConnectionListener;
import ua.edu.donnu.massmurderer.common.DatagramConnection;
import ua.edu.donnu.massmurderer.common.FileRecord;
import ua.edu.donnu.massmurderer.common.Message;

/**
 * the main form of MM Admin UI
 * 11:51:30 02.04.2010
 * @author Dan A. "devastator" Haman <dan.haman at yahoo.co.uk>
 * @version 1.0
 */
public class MurderConsole extends JFrame implements ListDataListener, ServerListener, ConnectionListener {

    // <editor-fold defaultstate="collapsed" desc="general const">
    /**
     * localization properties path
     */
    private static final String RES_LOCALIZATION = "ua/edu/donnu/massmurderer/admin/mma_strings";
    /**
     * relative path to configuration file
     */
    private static final String CFG_GENERAL = "mma.conf";
    /**
     * mask to replace. should be replaced with configuration file name
     */
    private static final String RPM_CONF = "{CONFIG}";
    /**
     * mask to replace. should be replaced with address, according to replacement context
     */
    private static final String RPM_ADDR = "{ADDR}";
    /**
     * logger name
     */
    private static final String LOG_ID = "mc_log";
    /**
     * general log file name
     */
    private static final String LOG_GENERAL = "general.log";
    /**
     * network interaction log
     */
    private static final String LOG_NET = "network.log";
    /**
     * broadcast address
     */
    private static final String BRD_ALL = "255.255.255.255";
    /**
     * machine shutdown command
     */
    public static final String REQ_KILLCMD = "KILL";
    /**
     * machine(s) restart command
     */
    public static final String REQ_RESTCMD = "RESTART";
    /**
     * echo command
     */
    public static final String REQ_ECHOCMD = "ECHO";
    /**
     * property changed in case network scanning is cancelled
     */
    public static final String PRO_CANCEL = "SCAN_CANCELED";
    /**
     * default error message
     */
    private static final String ERR_DFT = "Failed to load configuration, using default settings...";
    /**
     * configuration saving failed message
     */
    private static final String ERR_SVE = "Failed to save configuration settings...";
    /**
     * charset settings
     */
    private static final String CHARSET = "ASCII";
    /**
     * end of address list
     */
    private static final String EOLIST = "#";
    /**
     * request parts delimiter
     */
    public static final String REQ_CMDDELIM = ":";
    /**
     * specific symbol to mark all addresses
     */
    public static final String REQ_ALLMASK = "*";
    /**
     * groups file extension
     */
    public static final String FL_GRPEXT = ".grp";
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="default values">
    /**
     * default locale id
     */
    private static final String DFV_LOCALE = "default";
    /**
     * default port value
     */
    private static final String DFV_PORT = "22022";
    /**
     * server port
     */
    private static final String DFV_SRVPORT = "22021";
    /**
     * default logs path
     */
    private static final String DFV_LOGS = "";
    /**
     * default network scan timeout
     */
    private static final String DFV_TIMEOUT = "2000";
    /**
     * default latest agent version file name
     */
    private static final String DFV_LATESTJAR = "MMAgent.jar";
    /**
     * number of connections in queue
     */
    private static final String DFV_BACKLOG = "10";
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="config keys">
    /**
     * config key for locale
     */
    private static final String CFGS_LOCALE = "locale";
    /**
     * config key for interaction port
     */
    private static final String CFGS_PORT = "port";
    /**
     * server port
     */
    private static final String CFGS_SRVPORT = "srvport";
    /**
     * config key for logs path
     */
    private static final String CFGS_LOGDIR = "logs";
    /**
     * network scan timout
     */
    private static final String CFGS_TIMEOUT = "timeout";
    /**
     * latest agent version file name
     */
    private static final String CFGS_LATESTJAR = "latest_agent";
    /**
     * config key for backlog
     */
    private static final String CFGS_BACKLOG = "backlog";
    // </editor-fold>
    /**
     * admin module configuration settings
     */
    public static Properties joConfiguration = new Properties();
    /**
     * localization data
     */
    public static ResourceBundle joLocalization;
    /**
     * list of subnet addresses to broadcast to
     */
    private Inet4Address[] jnSendTo;
    /**
     * logger
     */
    public static Logger lgInst = Logger.getLogger(LOG_ID);
    /**
     * echo listener
     */
    private Server uSrv;
    /**
     * separate server thread
     */
    private Thread jThread;
    /**
     * list of available addresses in the net
     */
    private Vector vAvailableAddr = new Vector();
    /**
     * table datamodel
     */
    public static ConnListModel lmModel = new ConnListModel();
    /**
     * timer to end echo scan after timeout
     */
    private Timer joTimer = new Timer();
    /**
     * agent.jar content
     */
    private FileRecord jFileInfoPlus = null;
    /**
     * groups combobox datamodel
     */
    private GroupsModel cmGroups = new GroupsModel();

    /**
     * configuring admin module
     * @since 1.0
     */
    protected void configureServer() throws IOException, Exception {
        try {
            joConfiguration.load(new FileInputStream(CFG_GENERAL));
        } catch (IOException ex) {
            joConfiguration.setProperty(CFGS_LOCALE, DFV_LOCALE);
            joConfiguration.setProperty(CFGS_PORT, DFV_PORT);
            joConfiguration.setProperty(CFGS_LOGDIR, DFV_LOGS);
            joConfiguration.setProperty(CFGS_SRVPORT, DFV_SRVPORT);
            joConfiguration.setProperty(CFGS_TIMEOUT, DFV_TIMEOUT);
            joConfiguration.setProperty(CFGS_LATESTJAR, DFV_LATESTJAR);
            joConfiguration.setProperty(CFGS_BACKLOG, DFV_BACKLOG);
        }
        joLocalization = ResourceBundle.getBundle(RES_LOCALIZATION, new Locale(joConfiguration.getProperty(CFGS_LOCALE)));
        // FileHandler fhLogger = new FileHandler(joConfiguration.getProperty(CFGS_LOGDIR) + File.separatorChar + LOG_GENERAL);
        // lgInst.addHandler(fhLogger);
        this.setLocationRelativeTo(null);
        lgInst.info(joLocalization.getString("msgConfig"));
        // <editor-fold defaultstate="collapsed" desc="server">
        uSrv = new Server(Integer.parseInt(joConfiguration.getProperty(CFGS_SRVPORT)), Integer.parseInt(joConfiguration.getProperty(CFGS_BACKLOG)), this);
        jThread = new Thread(uSrv);
        jThread.start();
        // </editor-fold>
        lmModel.addListDataListener(this);
        // <editor-fold defaultstate="collapsed" desc="loading latest agent">
        jFileInfoPlus = new FileRecord(new File(joConfiguration.getProperty(CFGS_LATESTJAR)));
        // </editor-fold>
        loadGroups();
    }

    /**
     * sending echo command to net in order to get a list of available clients
     * @throws SocketException
     * @throws UnknownHostException
     * @throws IOException
     */
    protected void getListAvailables() throws SocketException, UnknownHostException, IOException, Exception {
        lmModel.clearAll(true);
        DatagramConnection.send(new Message(Message.MessageType.ECHO, Integer.parseInt(joConfiguration.getProperty(CFGS_SRVPORT))), (Inet4Address) InetAddress.getByName(BRD_ALL), Integer.parseInt(joConfiguration.getProperty(CFGS_PORT)));
    }

    protected void loadGroups() throws FileNotFoundException, IOException, ClassNotFoundException {
        cmGroups.addElement(new AdrGroup(" "));
        File[] arFiles = new File(System.getProperty("user.dir")).listFiles((FileFilter) new GFileFilter(FL_GRPEXT));
        for (File f : arFiles) {
            ObjectInputStream srOIn = new ObjectInputStream(new FileInputStream(f));
            Object oObj = srOIn.readObject();
            AdrGroup uGroup = (AdrGroup) oObj;
            cmGroups.addElement(uGroup);
        }
    }

    /**
     * converts list of IPv4 addresses into a string line, delimited in a certain way
     * @param jnAddr address list to convert
     * @return string line, containing address list
     * @see IpListModel#REQ_DELIM list delimiter symbol
     */
    protected String listToString(Inet4Address[] jnAddr) {
        String sResult = "";
        for (int i = 0; i < jnAddr.length; i++) {
            sResult += ((sResult.isEmpty()) ? "" : IpListModel.REQ_DELIM) + jnAddr[i].getHostAddress();
        }
        return sResult;
    }

    /**
     * broadcasting udp packet to a number of subnets
     * @param jnRecipients address list
     * @param sData data to send
     * @since 1.0
     * @throws SocketException
     * @throws UnknownHostException
     * @throws IOException
     * @deprecated the method does not support the defined protocol
     */
    protected void sendPacket(Inet4Address[] jnRecipients, String sData) throws SocketException, UnknownHostException, IOException {
        DatagramSocket jnSocket = new DatagramSocket();
        DatagramPacket jnPacket;
        for (int i = 0; i < jnRecipients.length; i++) {
            byte[] bIp = jnRecipients[i].getAddress();
            bIp[bIp.length - 1] = (byte) 255;
            Inet4Address jnSub = (Inet4Address) InetAddress.getByAddress(bIp);
            ByteArrayOutputStream srBOut = new ByteArrayOutputStream();
            DataOutputStream srDOut = new DataOutputStream(srBOut);
            srDOut.writeUTF(sData);
            srDOut.close();
            byte[] bRes = srBOut.toByteArray();
            jnPacket = new DatagramPacket(bRes, 0, bRes.length, jnSub, Integer.valueOf(joConfiguration.getProperty(CFGS_PORT)).intValue());
            jnSocket.send(jnPacket);
            srBOut.close();
        }
    }

    /**
     * sending messages through UDP protocol regarding the current networking protocol
     * @since 1.0
     * @param jnRecipients list of recipients
     * @param jType the type of message to be sent
     * @throws Exception
     * @see DatagramConnection#send(ua.edu.donnu.massmurderer.common.Message, java.net.InetAddress, int) sending method
     */
    protected void sendPacket(Inet4Address[] jnRecipients, Message.MessageType jType) throws Exception {
        for (int i = 0; i < jnRecipients.length; i++) {
            DatagramConnection.send(new Message(jType), jnRecipients[i], Integer.parseInt(joConfiguration.getProperty(CFGS_PORT)));
        }
    }

    /**
     * default constructor
     * @since 1.0
     */
    public MurderConsole() {
        try {
            configureServer();
            initComponents();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(null, ex.getLocalizedMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            Logger.getAnonymousLogger().severe(ex.getLocalizedMessage());
            System.exit(33);
        }
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        pBottom = new javax.swing.JPanel();
        cbActivatePrecise = new javax.swing.JCheckBox();
        bRefresh = new javax.swing.JButton();
        bSelAll = new javax.swing.JButton();
        bDeselAll = new javax.swing.JButton();
        bSaveAsGrp = new javax.swing.JButton();
        pTop = new javax.swing.JPanel();
        bCustNet = new javax.swing.JButton();
        cbGroups = new javax.swing.JComboBox();
        bRest = new javax.swing.JButton();
        bKill = new javax.swing.JButton();
        bAbout = new javax.swing.JButton();
        bExit = new javax.swing.JButton();
        pData = new javax.swing.JPanel();
        pLabels = new javax.swing.JPanel();
        lSendLab = new javax.swing.JLabel();
        lRequest = new javax.swing.JLabel();
        lReqLab = new javax.swing.JLabel();
        lSendTo = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        liConnections = new javax.swing.JList();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle(joLocalization.getString("ttlMain"));
        setLocationByPlatform(true);
        setMinimumSize(new java.awt.Dimension(640, 480));
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosed(java.awt.event.WindowEvent evt) {
                formWindowClosed(evt);
            }
        });

        pBottom.setMinimumSize(new java.awt.Dimension(405, 35));
        pBottom.setOpaque(false);
        pBottom.setPreferredSize(new java.awt.Dimension(405, 35));

        cbActivatePrecise.setText(joLocalization.getString("sPrecise"));
        cbActivatePrecise.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cbActivatePreciseActionPerformed(evt);
            }
        });
        pBottom.add(cbActivatePrecise);

        bRefresh.setText(joLocalization.getString("bRefresh"));
        bRefresh.setToolTipText(joLocalization.getString("tpRefresh"));
        bRefresh.setEnabled(false);
        bRefresh.setFocusable(false);
        bRefresh.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        bRefresh.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        bRefresh.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                bRefreshActionPerformed(evt);
            }
        });
        pBottom.add(bRefresh);

        bSelAll.setText(joLocalization.getString("bSelectAll"));
        bSelAll.setToolTipText(joLocalization.getString("tpSelectAll"));
        bSelAll.setEnabled(false);
        bSelAll.setFocusable(false);
        bSelAll.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        bSelAll.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        bSelAll.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                bSelAllActionPerformed(evt);
            }
        });
        pBottom.add(bSelAll);

        bDeselAll.setText(joLocalization.getString("bDeselectAll"));
        bDeselAll.setToolTipText(joLocalization.getString("tpDeselectAll"));
        bDeselAll.setEnabled(false);
        bDeselAll.setFocusable(false);
        bDeselAll.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        bDeselAll.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        bDeselAll.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                bDeselAllActionPerformed(evt);
            }
        });
        pBottom.add(bDeselAll);

        bSaveAsGrp.setText(joLocalization.getString("bSaveAsGrp"));
        bSaveAsGrp.setEnabled(false);
        bSaveAsGrp.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                bSaveAsGrpActionPerformed(evt);
            }
        });
        pBottom.add(bSaveAsGrp);

        getContentPane().add(pBottom, java.awt.BorderLayout.SOUTH);

        pTop.setMinimumSize(new java.awt.Dimension(379, 35));
        pTop.setPreferredSize(new java.awt.Dimension(379, 35));
        pTop.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT));

        bCustNet.setText(joLocalization.getString("bCustSN"));
        bCustNet.setToolTipText(joLocalization.getString("tpCustSN"));
        bCustNet.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                bCustNetActionPerformed(evt);
            }
        });
        pTop.add(bCustNet);

        cbGroups.setModel(cmGroups);
        cbGroups.setRenderer(new GroupRenderer());
        cbGroups.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cbGroupsActionPerformed(evt);
            }
        });
        pTop.add(cbGroups);

        bRest.setText(joLocalization.getString("bSendRest"));
        bRest.setToolTipText(joLocalization.getString("tpApply"));
        bRest.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                bRestActionPerformed(evt);
            }
        });
        pTop.add(bRest);

        bKill.setText(joLocalization.getString("bSendKill"));
        bKill.setToolTipText(joLocalization.getString("tpApply"));
        bKill.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                bKillActionPerformed(evt);
            }
        });
        pTop.add(bKill);

        bAbout.setText(joLocalization.getString("bAbout"));
        bAbout.setToolTipText(joLocalization.getString("tpAbout"));
        bAbout.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                bAboutActionPerformed(evt);
            }
        });
        pTop.add(bAbout);

        bExit.setText(joLocalization.getString("bExit"));
        bExit.setToolTipText(joLocalization.getString("tpExit"));
        bExit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                bExitActionPerformed(evt);
            }
        });
        pTop.add(bExit);

        getContentPane().add(pTop, java.awt.BorderLayout.NORTH);

        pData.setLayout(new java.awt.BorderLayout());

        pLabels.setLayout(new java.awt.GridBagLayout());

        lSendLab.setText(joLocalization.getString("lSendTo"));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        pLabels.add(lSendLab, gridBagConstraints);

        lRequest.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        pLabels.add(lRequest, gridBagConstraints);

        lReqLab.setText(joLocalization.getString("lRequest"));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        pLabels.add(lReqLab, gridBagConstraints);

        lSendTo.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        pLabels.add(lSendTo, gridBagConstraints);

        pData.add(pLabels, java.awt.BorderLayout.SOUTH);

        liConnections.setModel(lmModel);
        liConnections.setCellRenderer(new ConnRenderer());
        liConnections.addListSelectionListener(new javax.swing.event.ListSelectionListener() {
            public void valueChanged(javax.swing.event.ListSelectionEvent evt) {
                liConnectionsValueChanged(evt);
            }
        });
        jScrollPane1.setViewportView(liConnections);

        pData.add(jScrollPane1, java.awt.BorderLayout.CENTER);

        getContentPane().add(pData, java.awt.BorderLayout.CENTER);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void bExitActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_bExitActionPerformed
        try {
            joConfiguration.store(new FileOutputStream(CFG_GENERAL), "Avaliable locale values: default, en, ru");
        } // <editor-fold defaultstate="collapsed" desc="exceptions handling">
        catch (IOException ex) {
            JOptionPane.showMessageDialog(null, ERR_DFT, "Error", JOptionPane.ERROR_MESSAGE);
            lgInst.severe(ex.getMessage());
            System.exit(1);
        }
        // </editor-fold>
        System.exit(0);
    }//GEN-LAST:event_bExitActionPerformed

    /**
     * sending command regarding the mode selected
     * @since 1.0
     * @param jType
     */
    protected void sendCmd(Message.MessageType jType) {
        if (cbActivatePrecise.isSelected()) {
            Object[] jSelected = liConnections.getSelectedValues();
            for (int i = 0; i < jSelected.length; i++) {
                ((Connection) jSelected[i]).send(new Message(jType));
            }
        } else {
            try {
                sendPacket(jnSendTo, jType);
            } // <editor-fold defaultstate="collapsed" desc="exceptions handling">
            catch (Exception ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(), joLocalization.getString("dlgError"), JOptionPane.ERROR_MESSAGE);
                lgInst.severe(ex.getMessage());
            }
            // </editor-fold>
        }
    }

    private void bKillActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_bKillActionPerformed
        sendCmd(Message.MessageType.SHUTDOWN);
    }//GEN-LAST:event_bKillActionPerformed

    private void bRestActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_bRestActionPerformed
        sendCmd(Message.MessageType.RESTART);
    }//GEN-LAST:event_bRestActionPerformed

    private void bCustNetActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_bCustNetActionPerformed
        try {
            CustomNet dlgCNet = new CustomNet(this, true);
            dlgCNet.setLocationRelativeTo(this);
            dlgCNet.setVisible(true);
            cbActivatePrecise.setSelected(false);
            jnSendTo = dlgCNet.getResult();
            lSendTo.setText(listToString(jnSendTo));
            lRequest.setText(REQ_CMDDELIM + REQ_ALLMASK + REQ_CMDDELIM);
        } catch (UnknownHostException ex) {
            lgInst.severe(ex.getLocalizedMessage());
        }
    }//GEN-LAST:event_bCustNetActionPerformed

    private void bAboutActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_bAboutActionPerformed
        JOptionPane.showMessageDialog(this, joLocalization.getString("msgAbout").replace(RPM_CONF, CFG_GENERAL), "About", JOptionPane.INFORMATION_MESSAGE);
    }//GEN-LAST:event_bAboutActionPerformed

    private void bRefreshActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_bRefreshActionPerformed
        try {
            getListAvailables();
        } // <editor-fold defaultstate="collapsed" desc="exceptions handling">
        catch (Exception ex) {
            lgInst.severe(ex.getLocalizedMessage());
        } // </editor-fold>
    }//GEN-LAST:event_bRefreshActionPerformed

    private void bSelAllActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_bSelAllActionPerformed
        liConnections.setSelectionInterval(0, lmModel.getSize() - 1);
    }//GEN-LAST:event_bSelAllActionPerformed

    private void bDeselAllActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_bDeselAllActionPerformed
        liConnections.removeSelectionInterval(0, lmModel.getSize());
    }//GEN-LAST:event_bDeselAllActionPerformed

    /**
     * generating request for udp broadcasting
     * @return request string lines
     */
    protected String generateRequest() {
        if (!liConnections.isSelectionEmpty()) {
            Object[] oSelected = liConnections.getSelectedValues();
            ArrayList jAl = new ArrayList(Arrays.asList(oSelected));
            String sResult = "";
            if (oSelected.length > MurderConsole.lmModel.getSize() - oSelected.length) {
                sResult = REQ_CMDDELIM + REQ_ALLMASK + REQ_CMDDELIM;
                for (int i = 0; i < MurderConsole.lmModel.getSize(); i++) {
                    if (!jAl.contains(MurderConsole.lmModel.getElementAt(i))) {
                        sResult += String.valueOf(MurderConsole.lmModel.getElementAt(i));
                    }
                }
            } else {
                String sList = "";
                for (int i = 0; i < oSelected.length; i++) {
                    sList += ((sList.length() == 0) ? "" : IpListModel.REQ_DELIM) + String.valueOf(oSelected[i]);
                }
                sResult = REQ_CMDDELIM + sList + REQ_CMDDELIM;
            }
            return sResult;
        } else {
            return "";
        }
    }

    private void liConnectionsValueChanged(javax.swing.event.ListSelectionEvent evt) {//GEN-FIRST:event_liConnectionsValueChanged
        lRequest.setText(generateRequest());
        lSendTo.setText(BRD_ALL);
    }//GEN-LAST:event_liConnectionsValueChanged

    private void cbActivatePreciseActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cbActivatePreciseActionPerformed
        liConnections.setEnabled(cbActivatePrecise.isSelected());
        bRefresh.setEnabled(cbActivatePrecise.isSelected());
        bSaveAsGrp.setEnabled(cbActivatePrecise.isSelected());
    }//GEN-LAST:event_cbActivatePreciseActionPerformed

    private void formWindowClosed(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosed
        lmModel.clearAll(true);
    }//GEN-LAST:event_formWindowClosed

    private void cbGroupsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cbGroupsActionPerformed
        try {
            ((AdrGroup) cbGroups.getSelectedItem()).applyGroup(liConnections);
        } catch (Exception ex) {
            Logger.getLogger(MurderConsole.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_cbGroupsActionPerformed

    private void bSaveAsGrpActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_bSaveAsGrpActionPerformed
        if (!liConnections.isSelectionEmpty()) {
            ObjectOutputStream srOOut = null;

            String sName = JOptionPane.showInputDialog(this, joLocalization.getString("msgNameRequest"), joLocalization.getString("dlgRequest"), JOptionPane.INFORMATION_MESSAGE);
            AdrGroup uGroup = new AdrGroup(sName);
            for (Object oConn : liConnections.getSelectedValues()) {
                uGroup.addAddress(((Connection) oConn).getRemoteAddress());
            }
            // <editor-fold defaultstate="collapsed" desc="saving group">
            try {
                srOOut = new ObjectOutputStream(new FileOutputStream(String.valueOf(System.currentTimeMillis()) + FL_GRPEXT));
                srOOut.writeObject(uGroup);
                srOOut.close();
                cmGroups.addElement(uGroup);
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(this, ex.getLocalizedMessage(), joLocalization.getString("dlgError"), JOptionPane.ERROR_MESSAGE);
            } finally {
                try {
                    srOOut.close();
                } catch (IOException ex) {
                    JOptionPane.showMessageDialog(this, ex.getLocalizedMessage(), joLocalization.getString("dlgError"), JOptionPane.ERROR_MESSAGE);
                }
            }// </editor-fold>
        } else {
            JOptionPane.showMessageDialog(this, joLocalization.getString("msgNoAddressSelected"), joLocalization.getString("dlgError"), JOptionPane.WARNING_MESSAGE);
        }
    }//GEN-LAST:event_bSaveAsGrpActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        try {
            UIManager.setLookAndFeel("com.sun.java.swing.plaf.nimbus.NimbusLookAndFeel");
            new MurderConsole().setVisible(true);
        } catch (Exception ex) {
            Logger.getLogger(MurderConsole.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton bAbout;
    private javax.swing.JButton bCustNet;
    private javax.swing.JButton bDeselAll;
    private javax.swing.JButton bExit;
    private javax.swing.JButton bKill;
    private javax.swing.JButton bRefresh;
    private javax.swing.JButton bRest;
    private javax.swing.JButton bSaveAsGrp;
    private javax.swing.JButton bSelAll;
    private javax.swing.JCheckBox cbActivatePrecise;
    private javax.swing.JComboBox cbGroups;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JLabel lReqLab;
    private javax.swing.JLabel lRequest;
    private javax.swing.JLabel lSendLab;
    private javax.swing.JLabel lSendTo;
    private javax.swing.JList liConnections;
    private javax.swing.JPanel pBottom;
    private javax.swing.JPanel pData;
    private javax.swing.JPanel pLabels;
    private javax.swing.JPanel pTop;
    // End of variables declaration//GEN-END:variables

    // <editor-fold defaultstate="collapsed" desc="ListDataListener methods">
    public void intervalAdded(ListDataEvent e) {
        bSelAll.setEnabled(!(lmModel.getSize() == 0));
        bDeselAll.setEnabled(!(lmModel.getSize() == 0));
    }

    public void intervalRemoved(ListDataEvent e) {
        bSelAll.setEnabled(!(lmModel.getSize() == 0));
        bDeselAll.setEnabled(!(lmModel.getSize() == 0));
    }

    public void contentsChanged(ListDataEvent e) {
        bSelAll.setEnabled(!(lmModel.getSize() == 0));
        bDeselAll.setEnabled(!(lmModel.getSize() == 0));
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="ServerListener methods">
    public void newConnection(Socket socket) {
        Connection jnConn = new Connection(socket, this);
        lmModel.addElement(jnConn);
        new Thread(jnConn).start();
    }

    public void serverStarted(Server srv) {
    }

    public void serverStopped(Server srv) {
    }

    public void serverError(Server srv, Exception ex) {
        lgInst.severe(ex.getLocalizedMessage());
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="ConnectionListener methods">
    public void connectionOpened(Connection conn) {
        conn.send(new Message(Message.MessageType.VERSION_DIGEST, jFileInfoPlus.getMd5Sum()));
    }

    public void connectionClosed(Connection conn) {
        lmModel.removeElement(conn);
        lgInst.info(joLocalization.getString("msgConnectionClosed").replace(RPM_ADDR, conn.getRemoteAddress().getHostAddress()));
    }

    public void messageReceived(Connection conn, Message message) {
        switch (message.getType()) {
            case GET_UPDATE:
                conn.send(new Message(Message.MessageType.UPDATE, jFileInfoPlus.getFileMessage()));
                break;
            case SUCCESS:
            default:
                break;
        }
    }

    public void connectionError(Connection conn, Exception ex) {
        lgInst.severe(conn.getRemoteAddress().getHostAddress() + " " + ex.getLocalizedMessage());
    }
    // </editor-fold>
}
