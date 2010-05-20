/*
 * Copyright 2010 Sergey <Ajax> Tyshlek (serhi.hsp@gmail.com)
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

package ua.edu.donnu.massmurderer.common;

import java.io.Serializable;

/**
 *
 * @author Ajax
 */
public class Message
        implements Serializable {
    public static enum MessageType{
        /** Shutdown message from admin. Only type needed */
        SHUTDOWN,
        /** Restart message from admin. Only type needed */
        RESTART,
        /** Success message from agents to admin in case of successful shutdown/restart execution. Only type is needed. Only over TCP. */
        SUCCESS,
        /** Fail message from agents to admin in case of unsuccessful shutdown/restart execution. Only type is needed. Only over TCP. */
        FAIL,
        /** Echo message from admin, initiates TCP connection. Type & body needed. Body: (int) server TCP listening port. In client's response only type needed.*/
        ECHO,
        /** Server sends md5 sum of proper client application. Type & body needed. Body: byte array with md5 checksum of newest client application. <u>Must be sended</u> to client right after Echo answer. Agent checks the sum and sends GET_UPDATE if the new version is needed. */
        VERSION_DIGEST,
        /** Update request from agents. Type only. <u>Must be handled</u> on admin-side for compatibility. */
        GET_UPDATE,
        /** Update message from admin with new package. Type & body needed. Body: FileMessage object. <u>Must be sended</u> after GET_UPDATE request for compatibility.*/
        UPDATE,
        /** Forced connection closing. Just type */
        CLOSE
    }
    private MessageType type;
    private Serializable body = null;

    public Message(MessageType type, Serializable body) {
        this.type = type;
        this.body = body;
    }

    public Message(MessageType type) {
        this.type = type;
    }

    public MessageType getType() {
        return type;
    }

    public Serializable getBody() {
        return body;
    }

}
