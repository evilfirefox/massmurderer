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

package ua.edu.donnu.massmurderer.loader;

import java.io.File;
import java.util.Properties;
import java.util.prefs.Preferences;

/**
 * Update loader main class.
 * Waits until MMAgent will be closed, then replaces it with the new version.
 * After that, executes new version.
 * @author Ajax
 */
public class Main {
    public static String backupPrefix;
    private static String oldVersionPath = null;
    private static String newVersionPath = null;
    private static String agentArgs;
    /**
     * Parses command line arguments
     */
    private static void parseCmdArgs(String[] args){
        for (int i=0; i<args.length; i++){
            if (args[i].equals("-o"))
                oldVersionPath = args[i+1];
            else if (args[i].equals("-n"))
                newVersionPath = args[i+1];
            else if (args[i].equals("-a"))
                agentArgs = args[i+1];
            else if (args[i].equals("-h")){
                oldVersionPath = newVersionPath = null;
                break;
            }
        }
    }
    /**
     * Loader startup.
     * @param args Command line arguments, such as path to both old and new version (necessary) and custom command line arguments for MMAgent.jar (optional).
     */
    public static void main(String[] args) {
        Properties properties = null;
        try{
            properties = new Properties();
            properties.load(properties.getClass().getResourceAsStream("/ua/edu/donnu/massmurderer/loader/mml.properties"));
            backupPrefix = properties.getProperty("backupPrefix","backup");
        } catch (Exception ex){
            ex.printStackTrace();
            System.exit(1);
        }
        parseCmdArgs(args);
        if (oldVersionPath!=null && newVersionPath!=null){
            int delay = Integer.parseInt(properties.getProperty("runningUpdateTime","3"));
            final String key = properties.getProperty("runningKeyName","mma.running");
            long diff = System.currentTimeMillis() - Preferences.userRoot().getLong(key,0);
            while (diff < delay * 2000){
                try{
                    Thread.sleep(delay/2 * 1000);
                } catch (Exception ex){
                }
                diff = System.currentTimeMillis() - Preferences.userRoot().getLong(key,0);
            }
            try{
                File oldVersion = new File(oldVersionPath);
                File newVersion = new File(newVersionPath);
                File dir = oldVersion.getParentFile();
                File backup = new File(dir,backupPrefix+oldVersion.getName());
                backup.delete();
                oldVersion.renameTo(backup);
                oldVersion = new File(oldVersionPath);
                oldVersion.delete();
                newVersion.renameTo(oldVersion);
                Runtime.getRuntime().exec("java -jar "
                    + oldVersion.getCanonicalPath() + (agentArgs!=null ? " " + agentArgs : ""));
                System.exit(0);
            } catch (Exception ex){
                ex.printStackTrace();
            }
        } else {
            try{
                String helpString = properties.getProperty("help."+java.util.Locale.getDefault().getLanguage(),properties.getProperty("help.en"));
                System.out.println(helpString);
            } catch (Exception ex){
            }
            System.exit(0);
        }
    }
}
