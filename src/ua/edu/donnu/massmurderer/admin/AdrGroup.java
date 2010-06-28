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
package ua.edu.donnu.massmurderer.admin;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.net.InetAddress;
import java.util.ArrayList;
import javax.swing.JList;
import ua.edu.donnu.massmurderer.admin.models.ConnListModel;
import ua.edu.donnu.massmurderer.common.Connection;

/**
 *
 * 12:28:17 25.06.2010 - INTSOL LLC, Donetsk, Ukraine
 * @author Dan A. "devastator" Haman <dan.haman at yahoo.co.uk>
 * @version 1.0
 */
public class AdrGroup implements Externalizable {

    /**
     * protocol version
     */
    private static final int I_VERSION = 10;
    /**
     * group name
     */
    private String sName = null;
    /**
     * list of address belonging to this group
     */
    private ArrayList<InetAddress> jlAddr = new ArrayList<InetAddress>();

    // <editor-fold defaultstate="collapsed" desc="constructors">
    /**
     * default constructor
     * @since 1.0
     */
    public AdrGroup() {
    }

    /**
     * constructor
     * @since 1.0
     * @param sName group name
     */
    public AdrGroup(String sName) {
        this.sName = sName;
    }

    /**
     * constructor
     * @since 1.0
     * @param sName group name
     * @param jAddr list of address to add to group
     */
    public AdrGroup(String sName, InetAddress[] jAddr) {
        this.sName = sName;
        for (InetAddress jAdr : jAddr) {
            jlAddr.add(jAdr);
        }
    }
    // </editor-fold>

    /**
     * apply group to specified list
     * @since 1.0
     * @param liApplyTo list with propert data model to apply group to
     * @see ConnListModel propert list model to use this method
     * @throws Exception
     */
    public void applyGroup(JList liApplyTo) throws Exception {
        if (liApplyTo.getModel() instanceof ConnListModel) {
            ConnListModel lmModel = (ConnListModel) liApplyTo.getModel();
            ArrayList<Integer> jlIndices = new ArrayList<Integer>();
            int[] iIndices;
            for (int i = 0; i < lmModel.getSize(); i++) {
                if (jlAddr.contains(((Connection) lmModel.getElementAt(i)).getRemoteAddress())) {
                    jlIndices.add(i);
                }
            }
            // <editor-fold defaultstate="collapsed" desc="convertion">
            iIndices = new int[jlIndices.size()];
            for (int i = 0; i < jlIndices.size(); i++) {
                iIndices[i] = jlIndices.get(i);
            }
            // </editor-fold>
            liApplyTo.setSelectedIndices(iIndices);
        } else {
            throw new Exception();
        }
    }

    public String getName() {
        return sName;
    }

    /**
     * 
     * @param oOut
     * @throws IOException
     */
    @Override
    public void writeExternal(ObjectOutput oOut) throws IOException {
        oOut.writeInt(I_VERSION);
        oOut.writeUTF(sName);
        oOut.writeObject(jlAddr);
        oOut.flush();
    }

    @Override
    public void readExternal(ObjectInput oIn) throws IOException, ClassNotFoundException {
        int iVersion = oIn.readInt();
        if (iVersion <= I_VERSION) {
            sName = oIn.readUTF();
            Object oRead = oIn.readObject();
            jlAddr = (ArrayList<InetAddress>) oRead;
        }
    }
}
