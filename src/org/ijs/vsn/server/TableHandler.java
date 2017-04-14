/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.ijs.vsn.server;

import java.io.FileWriter;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.log4j.Logger;

/**
 *
 * @author Matevz
 */
public class TableHandler extends HttpServlet {

    private static final Logger logger = Logger.getLogger(MtableHandler.class);

    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response) {
        String table = "";
        try {
            int c;
            long start = System.currentTimeMillis();
            while ((c = request.getInputStream().read()) != -1) {
                table += (char) c;
                long stop = System.currentTimeMillis();
                if ((stop - start) > 60000) {
                    logger.error("Coordinator tables download timeout!");
                    return;
                }
            }

            if (ServerConfig.getcTables().equals("on")) {
                String fileName = System.getProperty("user.dir") + System.getProperty("file.separator")
                        + "CoordinatorTables" + System.getProperty("file.separator") + TimeHandler.getTime("HH_mm_ss-dd-MM-yyyy") + ".html";
                FileWriter writer = new FileWriter(fileName);
                try {
                    writer.append(table);
                    writer.flush();
                } finally {
                    writer.close();
                }
            }
        } catch (Exception ex) {
            logger.error("Coordinator tables download error!: " + table);
            logger.error(ex);
        }
    }
}
