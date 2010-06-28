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

import java.awt.Component;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ListCellRenderer;

/**
 *
 * 12:50:50 25.06.2010 - INTSOL LLC, Donetsk, Ukraine
 * @author Dan A. "devastator" Haman <dan.haman at yahoo.co.uk>
 * @version 1.0
 */
public class GroupRenderer implements ListCellRenderer {

    private JLabel lRenderer = new JLabel("");

    public Component getListCellRendererComponent(JList liList, Object oObject, int iIndex, boolean bIsSelected, boolean bCellHasFocus) {
        if (oObject != null) {
            lRenderer.setText(((AdrGroup) oObject).getName());
        }
        return lRenderer;
    }
}
