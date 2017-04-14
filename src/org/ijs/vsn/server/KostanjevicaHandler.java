/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.ijs.vsn.server;

import com.sun.net.httpserver.HttpExchange;
import java.net.InetSocketAddress;
import java.net.URI;
import java.util.ArrayList;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.log4j.Logger;

/**
 *
 * @author Matevz
 */
public class KostanjevicaHandler extends HttpServlet {

    private static final Logger logger = Logger.getLogger(KostanjevicaHandler.class);
    private NewDataEvent nde;

    public KostanjevicaHandler(NewDataEvent nde) {
        this.nde = nde;
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) {
        String query = "";
        try {
            query = request.getQueryString();
            if (kostanjevicaCheck(query)) {
                InetSocketAddress ipPort = new InetSocketAddress(request.getRemoteAddr(), request.getRemotePort());
                ArrayList<Object> newData = new ArrayList<Object>();
                newData.add(query);
                newData.add(TimeHandler.getTime("yyyy-MM-dd HH:mm:ss"));
                newData.add(ipPort);

                nde.newDataNotification(newData);
            }
        } catch (Exception ex) {
            logger.error("Validator listeners didn't get the coordinators message: " + query);
            logger.error(ex);
        }
    }

    private Boolean kostanjevicaCheck(String reqStr) {
        boolean kostanjevicaFlag = false;
        if (reqStr.contains("405D7FA8") || reqStr.contains("405D82B3")
                || reqStr.contains("405D8330")
                || reqStr.contains("405D8327")
                || reqStr.contains("405D82AF")) {
            kostanjevicaFlag = true;
        }
        return kostanjevicaFlag;
    }
}
