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
public class MirrorHandler extends HttpServlet {

    private static final Logger logger = Logger.getLogger(TimeHandler.class);

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) {
        try {
            response.setContentType("text/plain");
            response.getWriter().println(request.getQueryString());
            response.setStatus(HttpServletResponse.SC_OK);
        } catch (Exception ex) {
            logger.error(ex);
        }
    }
}
