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
package ua.edu.donnu.massmurderer.admin.models;

import java.net.Inet4Address;
import java.util.Vector;
import javax.swing.AbstractListModel;
import ua.edu.donnu.massmurderer.admin.MurderConsole;

/**
 * clients ip address list data model
 * 13:01:14 02.04.2010
 * @author Dan A. "devastator" Haman <dan.haman at yahoo.co.uk>
 * @version 1.0
 */
public class IpListModel extends AbstractListModel {

    /**
     * request ips delimiter
     */
    public static final String REQ_DELIM = ",";
    /**
     * ip address list
     */
    private Vector vIps = new Vector();

    /**
     * default constructor
     * @since 1.0
     */
    public IpListModel() {
    }

    /**
     * getting list size
     * @since 1.0
     * @return number of elements in list
     */
    public int getSize() {
        return vIps.size();
    }

    /**
     * getting element by index
     * @since 1.0
     * @param index index to get element
     * @return specified data element value
     */
    public Object getElementAt(int index) {
        return vIps.get(index);
    }

    /**
     * adding element to list
     * @since 1.0
     * @param jnAddress ipv4 address to add as String
     */
    public void addElement(String sAddress) {
        try {
            Inet4Address jnTmp = (Inet4Address) Inet4Address.getByName(sAddress);
            vIps.add(sAddress);
            fireIntervalAdded(this, vIps.size() - 1, vIps.size());
        } catch (Exception ex) {
            //JOptionPane.showMessageDialog(null, ex.getMessage(), MurderConsole.joLocalization.getString("dlgError"), JOptionPane.ERROR_MESSAGE);
            MurderConsole.lgInst.info(ex.getMessage());
        }
    }

    /**
     * adding a group of elements to list
     * @since 1.0
     * @param sAddrs list of addresses to add
     */
    public void addElements(String[] sAddrs) {
        for (int i = 0; i < sAddrs.length; i++) {
            addElement(sAddrs[i]);
        }
    }

    /**
     * removing address from list
     * @since 1.0
     * @param jnAddress ipv4 address to remove
     */
    public void removeElement(String sAddress) {
        int iIndex = vIps.indexOf(sAddress);
        vIps.remove(iIndex);
        fireIntervalRemoved(iIndex, iIndex, iIndex);
    }

    /**
     * generating list of ips formatted for a turn-off request
     * @since 1.0
     * @return list of ips delimited with REQ_DELIM
     * @see IpListModel#REQ_DELIM IPs delimiter
     */
    public String generateRequest() {
        String sResult = "";
        for (int i = 0; i < vIps.size(); i++) {
            sResult += ((sResult.length() == 0) ? "" : REQ_DELIM) + String.valueOf(vIps.get(i));
        }
        return sResult;
    }

    /**
     * removing all data elements from model
     * @since 1.0
     */
    public void clearModel() {
        int iSize = vIps.size();
        vIps.clear();
        fireIntervalRemoved(this, 0, iSize);
    }
}
