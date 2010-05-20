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

package ua.edu.donnu.massmurderer.agent;

/**
 * Executor class, performs shutdown and restart operations with specified commands.
 * @author Ajax
 */
public abstract class Executor {
    private String shutdownApp;
    private String shutdownKey;
    private String restartKey;
    protected boolean success = false;
    /**
     * Initialization of commands.
     * @param shutdownApp path to shutdown application
     * @param shutdownKey shutdown key for specified application
     * @param restartKey restart key for specified application
     */
    public Executor(String shutdownApp, String shutdownKey, String restartKey) {
        this.shutdownApp = shutdownApp;
        this.shutdownKey = shutdownKey;
        this.restartKey = restartKey;
    }
    /**
     * Performs shutdown operation.
     */
    public void shutdown() {
        try{
            Runtime.getRuntime().exec(shutdownApp + " " + shutdownKey);
            success = true;
        } catch (Exception ex){
            success = false;
            executorError(ex);
        }
    }
    /**
     * * Performs restart operation.
     */
    public void restart(){
        try{
            Runtime.getRuntime().exec(shutdownApp + " " + restartKey);
            success = true;
        } catch (Exception ex){
            success = false;
            executorError(ex);
        }
    }
    /**
     * Separated exit call (for testing)
     */
    public void systemExit(){
        Main.exit();
    }
    /**
     * Returns whether last operation was successful.
     * @return true if last operation was succesfull, false if not.
     */
    public boolean isSuccess() {
        return success;
    }
    /**
     * Implementation of error handling mechanism.
     * @param ex Exception thrown.
     */
    public abstract void executorError(Exception ex);
}
