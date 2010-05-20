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

import java.util.ArrayList;
import javax.swing.AbstractListModel;
import ua.edu.donnu.massmurderer.common.Connection;

/**
 * client connections list data model
 * 20:55:32 12.05.2010
 * @author Dan A. "devastator" Haman <dan.haman at yahoo.co.uk>
 * @version 1.0
 */
public class ConnListModel extends AbstractListModel {

    /**
     * connections list
     */
    private ArrayList<Connection> jaConnections = new ArrayList<Connection>();

    /**
     * default constructor
     * @since 1.0
     */
    public ConnListModel() {
    }

    /**
     * getting number of data elements in model
     * @since 1.0
     * @return data elements in model
     */
    public int getSize() {
        return jaConnections.size();
    }

    /**
     * getting element specified by index
     * @since 1.0
     * @param iIndex index of the element needed
     * @return specified data element value
     */
    public Object getElementAt(int iIndex) {
        return jaConnections.get(iIndex);
    }

    /**
     * adding element to model
     * @since 1.0
     * @param jConn connection to add to model
     */
    public void addElement(Connection jConn) {
        jaConnections.add(jConn);
        fireIntervalAdded(this, jaConnections.size() - 1, jaConnections.size());
    }

    /**
     * removing specified data element
     * @since 1.0
     * @param jConn data element to remove from model
     */
    public void removeElement(Connection jConn) {
        if (jaConnections.contains(jConn)) {
            int iIndex = jaConnections.indexOf(jConn);
            jaConnections.remove(jConn);
            fireIntervalRemoved(this, iIndex, iIndex);
        }
    }

    /**
     * removing specified connection object. if <i>bCloseAlive</i> is set to true
     * and connection is alive it's being closed, otherwise just it's just being removed.
     * @since 1.0
     * @param jConn connection to remove (and close)
     * @param bCloseAlive flag, shows whether alive connection should be closed before removal
     */
    public void removeElement(Connection jConn, boolean bCloseAlive) {
        if (jaConnections.contains(jConn)) {
            int iIndex = jaConnections.indexOf(jConn);
            if (jConn.isRunning() && bCloseAlive) {
                jConn.close();
            }
            jaConnections.remove(jConn);
            fireIntervalRemoved(this, iIndex, iIndex);
        }
    }

    /**
     * removing all data elements from model
     * @since 1.0
     */
    public void clearAll() {
        int iSize = jaConnections.size();
        jaConnections.clear();
        fireIntervalRemoved(this, 0, iSize);
    }

    /**
     * removing all data, regarding <i>bCloseAlive</i> flag. connection removal method is similar to the one
     * implemented in ConnListModel#removeElement(Connection, boolean).
     * @since 1.0
     * @see ConnListModel#removeElement(ua.edu.donnu.massmurderer.common.Connection, boolean) connection removal method
     * @param bCloseAlive flag, shows whether alive connection should be closed before removal
     */
    public void clearAll(boolean bCloseAlive) {
        while (jaConnections.size() > 0) {
            removeElement(jaConnections.get(0), bCloseAlive);
        }
    }
}
