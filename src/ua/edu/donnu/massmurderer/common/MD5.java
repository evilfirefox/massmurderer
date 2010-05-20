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

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 *
 * @author Ajax
 */
public class MD5 {
    private static MessageDigest algorithm;
    /**
     * Class for calculation MD5 sums
     */
    static {
        try {
             algorithm = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException ex) {
            ex.printStackTrace();
        }
    }
    /**
     * Calculates md5
     */
    public static byte[] get(byte input[]){
        algorithm.reset();
        return algorithm.digest(input);
    }
    /**
     * Converts md5 sum from byte array to string
     */
    public static String md5ToString(byte [] md5){
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < md5.length; i++) {
            int val = 0xFF & md5[i];
            sb.append(val<0xF ? "0" : "");
            sb.append(Integer.toHexString(val));
        }
        return sb.toString();
    }
}
