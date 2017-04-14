/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.ijs.vsn.server;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.log4j.Logger;

/**
 *
 * @author Matevz
 */
public class MtableHandler extends HttpServlet {

    private static final Logger logger = Logger.getLogger(MtableHandler.class);

    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response) {
        String fileName = "";

        FileOutputStream tableFile = null;
        try {
            fileName = System.getProperty("user.dir") + System.getProperty("file.separator")
                    + "CoordinatorTables" + System.getProperty("file.separator")
                    + "table" + TimeHandler.getTime("yyyyMMddHHmmss") + ".html.tmp";
            tableFile = new FileOutputStream(fileName);

            int c;
            long start = System.currentTimeMillis();
            while ((c = request.getInputStream().read()) != -1) {
                tableFile.write((char) c);
                long stop = System.currentTimeMillis();
                if ((stop - start) > 60000) {
                    tableFile.close();
                    File file = new File(fileName);
                    if (file.delete()) {
                        logger.error("Coordinator table file " + fileName + " has been deleted"
                                + " because it was not downloaded within the"
                                + " one minute time limit!");
                        return;
                    }
                }
            }
            tableFile.close();
            renameFile(fileName, fileName.substring(0, fileName.length() - 4));
        } catch (Exception ex) {
            logger.error(ex);
            try {
                tableFile.close();
            } catch (IOException ex1) {
                logger.error(ex1);
            }
            File file = new File(fileName);
            if (file.delete()) {
                logger.error("Coordinator table file " + fileName + " has been deleted because"
                        + " of error: " + ex.getMessage() + "!");
                return;
            }
        }
    }

    public void renameFile(String file, String toFile) {
        File toBeRenamed = new File(file);
        if (!toBeRenamed.exists() || toBeRenamed.isDirectory()) {
            logger.error("File does not exist: " + file);
            return;
        }
        File newFile = new File(toFile);

        //Rename
        if (toBeRenamed.renameTo(newFile)) {
        } else {
            logger.error("Error renmaing file");
            toBeRenamed.delete();
        }
    }
}
