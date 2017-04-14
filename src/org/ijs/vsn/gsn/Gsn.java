/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.ijs.vsn.gsn;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.log4j.Logger;
import org.ijs.vsn.listener.ValidatorListener;
import org.ijs.vsn.node.Node;
import org.ijs.vsn.node.NodeList;
import org.ijs.vsn.database.Database;
import org.ijs.vsn.server.ServerConfig;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;

/**
 *
 * @author Administrator
 */
public class Gsn implements ValidatorListener {

    private static final Logger logger = Logger.getLogger(Gsn.class);

    public Gsn() {
    }

    public synchronized void createCsv(String strData) {
        String fileName = System.getProperty("user.dir")
                + System.getProperty("file.separator") + "DataCSV"
                + System.getProperty("file.separator") + getFileName(strData);

        try {
            FileWriter writer = new FileWriter(fileName);
            try {
                writer.append(strData);
                writer.flush();
            } finally {
                writer.close();
            }
        } catch (Exception ex) {
            logger.error(ex);
        }
    }

    public String getFileName(String strData) {
        String fileName = "node";
        String xbeeId = strData.substring(strData.indexOf(",") + 1, strData.length());
        xbeeId = xbeeId.substring(0, xbeeId.indexOf(","));

        Node node = NodeList.nodeMap.get(xbeeId);
        int dbId = node.getNode().get(xbeeId);
        fileName += dbId + ".csv";

        return fileName;
    }

    private boolean fileCheck(String fileName) {
        File file = new File(fileName);

        // File whether exists or not
        boolean exists = false;
        exists = file.exists();
        return exists;
    }

    private void createGsnFile(String data, String csvFile) {
        String latitude = "";
        String longitude = "";

        Map<String, String> mapNodeData = getQueryMap(data);
        String xbeeId = mapNodeData.get("p01").toString();

        try {
            Connection con = Database.datasource.getConnection();
            con.setAutoCommit(false);

            // create the java statement
            Statement st = con.createStatement();

            ResultSet result = st.executeQuery("SELECT gps_latitude, "
                    + "gps_longitude FROM sensor_node_table "
                    + "WHERE sn_uid = '" + xbeeId + "';");

            result.next();
            latitude = result.getString("gps_latitude");
            longitude = result.getString("gps_longitude");

            con.close();
        } catch (Exception ex) {
            logger.error(ex);
        }

        ArrayList<String> geoNames = GeoNames.getGeoNames(
                Double.parseDouble(latitude), Double.parseDouble(longitude));

        try {
            SAXBuilder builder = new SAXBuilder();
            File xmlFile = new File(ServerConfig.getGsnLocation()
                    + System.getProperty("file.separator") + "virtual-sensors"
                    + System.getProperty("file.separator") + "templates"
                    + System.getProperty("file.separator") + "template.xml");

            Document doc = (Document) builder.build(xmlFile);
            Element rootNode = doc.getRootElement();

            // update GPS location		
            Element addressing = rootNode.getChild("addressing");
            List predicates = addressing.getChildren("predicate");
            for (int i = 0; i < predicates.size(); i++) {
                Element column = (Element) predicates.get(i);
                String key = column.getAttribute("key").getValue();

                if (key.equals("geographical")) {
                    column.setText(geoNames.get(0) + ", "
                            + geoNames.get(1));
                } else if (key.equals("LATITUDE")) {
                    column.setText(latitude);
                } else if (key.equals("LONGITUDE")) {
                    column.setText(longitude);
                }
            }

            // set virtual-sensor name
            rootNode.getAttribute("name").setValue(geoNames.get(0) + "_" + xbeeId);

            // set CSV data file name		
            Element streams = rootNode.getChild("streams");
            Element stream = streams.getChild("stream");
            Element source = stream.getChild("source");
            Element address = source.getChild("address");
            List predicates2 = address.getChildren("predicate");
            for (int i = 0; i < predicates2.size(); i++) {
                Element column = (Element) predicates2.get(i);
                String key = column.getAttribute("key").getValue();

                if (key.equals("file")) {
                    column.setText(csvFile);
                }
            }

            // xml file output
            XMLOutputter xmlOutput = new XMLOutputter();
            xmlOutput.setFormat(Format.getPrettyFormat());
            xmlOutput.output(doc, new FileWriter(ServerConfig.getGsnLocation()
                    + System.getProperty("file.separator") + "virtual-sensors"
                    + System.getProperty("file.separator") + xbeeId + ".xml"));

            logger.info("New GSN Virtual Sensor created for the Sensor Node: "
                    + xbeeId);
        } catch (IOException io) {
            logger.error(io);
        } catch (JDOMException e) {
            logger.error(e);
        }
    }

    @Override
    public void validData(ArrayList<Object> validData) {
        //long start = System.currentTimeMillis();

        String nodeData = validData.get(0).toString();
        String[] params = nodeData.split("&");

        String strData = "";
        for (String param : params) {
            String name = param.split("=")[0];
            String value = param.split("=")[1];
            strData += (value + ",");
        }

        strData = strData + validData.get(1);
        String fileName = System.getProperty("user.dir")
                + System.getProperty("file.separator") + "DataCSV"
                + System.getProperty("file.separator") + getFileName(strData);

        boolean fileExist = fileCheck(fileName);
        if (fileExist) {
            createCsv(strData);
        } else {
            createCsv(strData);
            createGsnFile(nodeData, fileName);
        }

        //long stop = System.currentTimeMillis();
        //logger.info("Time to insert the CSV file: " + (stop - start));
        //logger.info("Working thread in Csv: " + Thread.currentThread().getName());
    }

    public Map<String, String> getQueryMap(String query) {
        Map<String, String> map = new HashMap<String, String>();
        try {
            String[] params = query.split("&");
            for (String param : params) {
                if (param.contains("p01")) {
                    String name = param.split("=")[0];
                    String value = param.split("=")[1];
                    map.put(name, value);
                } else {
                    String name = param.split("=")[0];
                    Float fValue = Float.parseFloat(param.split("=")[1]);
                    String value = fValue.toString();
                    map.put(name, value);
                }
            }
        } catch (Exception ex) {
            logger.error(ex);
        } finally {
            return map;
        }
    }
}
