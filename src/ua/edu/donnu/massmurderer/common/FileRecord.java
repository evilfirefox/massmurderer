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

import java.io.File;
import java.io.FileInputStream;

/**
 * Class for convenient work with file. Reads file data and calculates MD5 sum.
 * @author Ajax
 */
public class FileRecord {
    private File file;
    private byte[] data;
    private byte[] md5Sum;
    /**
     * Creates FileRecord class of specified File.
     * @param f target File
     * @throws Exception when the file is invalid.
     */
    public FileRecord(File f) throws Exception {
        this.file = f;
        FileInputStream fis = new FileInputStream(file);
        data = new byte[(int)file.length()];
        fis.read(data);
        fis.close();
        md5Sum = MD5.get(data);
    }
    /**
     * Return target File
     * @return target File
     */
    public File getFile() {
        return file;
    }
    /**
     * Returns contents of the target file
     * @return contents of the target file
     */
    public byte[] getData() {
        return data;
    }
    /**
     * Returns calculated MD5 sum
     * @return calculated MD5 sum
     */
    public byte[] getMd5Sum() {
        return md5Sum;
    }
    /**
     * Returns convenient structure for transmission
     * @return FileMessage object, which can be sended
     */
    public FileMessage getFileMessage(){
        return new FileMessage(file.getName(), data);
    }
    /**
     * Clears fields so the gc can erase unused data
     */
    public void clear(){
        file = null;
        data = null;
        md5Sum = null;
    }
}
