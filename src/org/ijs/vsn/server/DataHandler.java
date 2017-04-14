/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.ijs.vsn.server;

import java.net.InetSocketAddress;
import java.util.ArrayList;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.log4j.Logger;

/**
 *
 * @author Matevz
 */
public class DataHandler extends HttpServlet {

    private static final Logger logger = Logger.getLogger(DataHandler.class);
    private NewDataEvent nde;

    public DataHandler(NewDataEvent nde) {
        this.nde = nde;
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) {
        String query = "";
        try {
            InetSocketAddress ipPort = new InetSocketAddress(request.getRemoteAddr(), request.getRemotePort());
            query = request.getQueryString();
            ArrayList<Object> newData = new ArrayList<Object>();
            newData.add(query);
            newData.add(TimeHandler.getTime("yyyy-MM-dd HH:mm:ss"));
            newData.add(ipPort);

            nde.newDataNotification(newData);
        } catch (Exception ex) {
            logger.error("Validator listeners didn't get the coordinators message: " + query);
            logger.error(ex);
        }
    }
}
