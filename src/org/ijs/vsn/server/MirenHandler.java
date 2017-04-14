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
public class MirenHandler extends HttpServlet {

    private static final Logger logger = Logger.getLogger(MirenHandler.class);
    //private static int reqCounter = 0;
    //private static int sumTimings = 0;
    private NewDataEvent nde;

    public MirenHandler(NewDataEvent nde) {
        this.nde = nde;
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) {
        //reqCounter++;
        //logger.info("Request counter: " + reqCounter);

        //long start = System.currentTimeMillis();
        String query = "";
        try {
            InetSocketAddress ipPort = new InetSocketAddress(request.getRemoteAddr(), request.getRemotePort());
            query = request.getQueryString();
            ArrayList<Object> newData = new ArrayList<Object>();
            newData.add(query);
            newData.add(TimeHandler.getTime("yyyy-MM-dd HH:mm:ss"));
            newData.add(ipPort);

            nde.newDataNotification(newData);
            //long stop = System.currentTimeMillis();
            //sumTimings += (stop - start);
            //logger.info("Sum of requests timings: " + sumTimings);
            //logger.info("Everage duration of all requests: " + sumTimings/reqCounter + "ms");
            //logger.info("Duration of single request: " + (stop - start) + "ms");            
        } catch (Exception ex) {
            logger.error("Validator listeners didn't get the coordinators message: " + query);
            logger.error(ex);
        }
    }
}
