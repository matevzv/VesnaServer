/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.ijs.vsn.server;

import java.text.SimpleDateFormat;
import java.util.Date;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.log4j.Logger;

/**
 *
 * @author Matevz
 */
public class TimeHandler extends HttpServlet {

    private static final Logger logger = Logger.getLogger(TimeHandler.class);

    public String replaceCharAt(String s, int pos, char c) {
        StringBuilder buf = new StringBuilder(s);
        buf.setCharAt(pos, c);
        return buf.toString();
    }

    public static String getTime(String dateTimeFormat) {
        Date dt = new Date();
        SimpleDateFormat df = new SimpleDateFormat(dateTimeFormat);
        return df.format(dt);
    }

    private String getFormatedTime() {
        String gregorTime = getTime("HH:mm:ss dd-MM-yyyy");
        gregorTime = "h" + gregorTime;
        gregorTime = replaceCharAt(gregorTime, 3, 'm');
        gregorTime = replaceCharAt(gregorTime, 6, 's');
        gregorTime = replaceCharAt(gregorTime, 9, 'd');
        gregorTime = replaceCharAt(gregorTime, 12, 'e');
        gregorTime = replaceCharAt(gregorTime, 15, 'l');

        gregorTime = "DATUM: " + gregorTime;
        return gregorTime;
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) {
        try {
            response.setContentType("text/plain");
            response.getWriter().println(getFormatedTime());
            response.setStatus(HttpServletResponse.SC_OK);
        } catch (Exception ex) {
            logger.error("Failed to print time!");
        }
    }
}
