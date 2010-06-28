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
import javax.swing.MutableComboBoxModel;
import ua.edu.donnu.massmurderer.admin.AdrGroup;

/**
 *
 * 12:08:30 25.06.2010 - INTSOL LLC, Donetsk, Ukraine
 * @author Dan A. "devastator" Haman <dan.haman at yahoo.co.uk>
 * @version 1.0
 */
public class GroupsModel extends AbstractListModel implements MutableComboBoxModel {

    private ArrayList<AdrGroup> jlData = new ArrayList<AdrGroup>();
    /**
     * current selection index
     */
    private AdrGroup sSelectedObject = null;

    public GroupsModel() {
    }

    /**
     * getting number of elements present in the list
     * @since 1.0
     * @return list size
     */
    public int getSize() {
        return jlData.size();
    }

    /**
     * getting element specified by index
     * @since 1.0
     * @param iIndex number of element to get
     * @return selected element value
     */
    public Object getElementAt(int iIndex) {
        return jlData.get(iIndex);
    }

    /**
     * adding new element to list
     * @since 1.0
     * @param oObj object to add
     */
    public void addElement(Object oObj) {
        jlData.add((AdrGroup) oObj);
        fireIntervalAdded(this, getSize(), getSize());
    }

    /**
     * removing element by value
     * @since 1.0
     * @param oObj object to remove from list
     */
    public void removeElement(Object oObj) {
        int iIndex = jlData.indexOf(oObj);
        jlData.remove(iIndex);
        fireIntervalRemoved(this, iIndex, iIndex);
    }

    /**
     * add object to specified position
     * @since 1.0
     * @param oObj object to insert
     * @param iIndex position to insert to index
     */
    public void insertElementAt(Object oObj, int iIndex) {
        jlData.add(iIndex, (AdrGroup) oObj);
        fireIntervalAdded(this, iIndex, iIndex);
    }

    /**
     * remove element specified by index
     * @since 1.0
     * @param iIndex index of element to remove
     */
    public void removeElementAt(int iIndex) {
        jlData.remove(iIndex);
        fireIntervalRemoved(this, iIndex, iIndex);
    }

    /**
     * setting selected element
     * @since 1.0
     * @param oObj object to select
     */
    public void setSelectedItem(Object oObj) {
        this.sSelectedObject = (AdrGroup) oObj;
    }

    /**
     * getting current selection
     * @since 1.0
     * @return currently selected value
     */
    public Object getSelectedItem() {
        return sSelectedObject;
    }

    /**
     * removinf all elements from list
     * @since 1.0
     */
    public void clearAll() {
        int iMaxIndex = jlData.size();
        jlData.clear();
        fireIntervalRemoved(this, 0, iMaxIndex);
    }
}
