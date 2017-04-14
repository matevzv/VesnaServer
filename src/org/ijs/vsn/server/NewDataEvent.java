/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.ijs.vsn.server;

import java.util.ArrayList;

/**
 *
 * @author Matevz
 */
public interface NewDataEvent {

    public void newDataNotification(ArrayList<Object> newData);
}
