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
public class PictureHandler extends HttpServlet {

    private static final Logger logger = Logger.getLogger(PictureHandler.class);

    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response) {
        String fileName = "";
        FileOutputStream imageFile = null;
        try {
            fileName = System.getProperty("user.dir") + System.getProperty("file.separator")
                    + "Pictures" + System.getProperty("file.separator")
                    + "image" + TimeHandler.getTime("yyyyMMddHHmm") + ".jpg.tmp";
            imageFile = new FileOutputStream(fileName);

            int c;
            long start = System.currentTimeMillis();
            while ((c = request.getInputStream().read()) != -1) {
                imageFile.write(c);
                long stop = System.currentTimeMillis();
                if ((stop - start) > 60000) {
                    imageFile.close();
                    File file = new File(fileName);
                    if (file.delete()) {
                        logger.error("Picture " + fileName + " has been deleted"
                                + " because it was not downloaded within the"
                                + " one minute time limit!");
                        return;
                    }
                }
            }
            imageFile.close();

            renameFile(fileName, fileName.substring(0, fileName.length() - 4));

        } catch (Exception ex) {
            logger.error(ex);
            try {
                imageFile.close();
            } catch (IOException ex1) {
                logger.error(ex1);
            }
            File file = new File(fileName);
            if (file.delete()) {
                logger.error("Picture " + fileName + " has been deleted because"
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
