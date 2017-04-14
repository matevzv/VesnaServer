/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.ijs.vsn.server;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.log4j.Logger;

/**
 *
 * @author Matevz
 */
public class GpsHandler extends HttpServlet {

    private static final Logger logger = Logger.getLogger(GpsHandler.class);

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) {
        String query = "";
        try {
            query = request.getQueryString();
            logger.info("GPS: " + query);
        } catch (Exception ex) {
            logger.error("Validator listeners didn't get the coordinators message: " + query);
            logger.error(ex);
        }
    }
}
