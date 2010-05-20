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
import javax.swing.JCheckBox;
import javax.swing.JList;
import javax.swing.ListCellRenderer;
import ua.edu.donnu.massmurderer.common.Connection;

/**
 * this class is a ua.edu.donnu.massmurderer.common.Connection list renderer
 * 15:35:52 13.05.2010
 * @author Dan A. "devastator" Haman <dan.haman at yahoo.co.uk>
 * @version 1.0
 */
public class ConnRenderer implements ListCellRenderer {

    /**
     * renderer checkbox
     */
    protected JCheckBox cbItem = new JCheckBox();

    /**
     * default constructor
     * @since 1.0
     */
    public ConnRenderer() {
    }

    public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
        cbItem.setText(((Connection) value).getRemoteAddress().getHostAddress());
        cbItem.setSelected(isSelected);
        return cbItem;
    }
}
