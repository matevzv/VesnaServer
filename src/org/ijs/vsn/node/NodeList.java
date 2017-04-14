/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.ijs.vsn.node;

import java.sql.DriverManager;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;
import org.ijs.vsn.server.ServerConfig;
import org.apache.log4j.Logger;

/**
 *
 * @author Matevz
 */
public class NodeList {

    private static final Logger logger = Logger.getLogger(NodeList.class);
    private Connection con = null;
    private int nodeNumber;
    private int dbSensorId;
    private int codeId;
    private int dbNodeId;
    private String xbeeId;
    private Double latitude;
    private Double longitude;
    private String measuredPhenomenon;
    public static Map<String, Node> nodeMap;
    private String strCode;

    // id of data in the data string in HTTP GET request
    public NodeList() {
    }

    public void downloadNodes() {
        nodeMap = new HashMap<String, Node>();
        Statement st = null;
        try {
            Class.forName("com.mysql.jdbc.Driver").newInstance();
            con = DriverManager.getConnection(ServerConfig.getDbAddress(), ServerConfig.getDbUsername(), ServerConfig.getDbPassword());

            // create the java statement
            st = con.createStatement();

            // execute the query, and get a java resultset
            ResultSet rs = st.executeQuery("SELECT sn_auto_id FROM sensor_node_table ORDER BY sn_auto_id DESC LIMIT 1");
            while (rs.next()) {
                nodeNumber = rs.getInt("sn_auto_id");
            }

            for (int i = 1; i <= nodeNumber; i++) {
                Node node = new Node();

                rs = st.executeQuery("SELECT sn_auto_id, sn_uid, gps_latitude, gps_longitude from sensor_node_table where sn_auto_id=" + i);
                while (rs.next()) {
                    dbNodeId = rs.getInt("sn_auto_id");
                    xbeeId = rs.getString("sn_uid");
                    if (rs.getString("gps_latitude") != null && rs.getString("gps_longitude") != null) {
                        latitude = Double.parseDouble(rs.getString("gps_latitude"));
                        longitude = Double.parseDouble(rs.getString("gps_longitude"));
                        node.setLatitude(latitude);
                        node.setLongitude(longitude);
                    }
                    node.addNode(xbeeId, dbNodeId);
                }

                rs = st.executeQuery("SELECT sensor_uid, data_code, measured_phenomenon FROM sensor_table, "
                        + "sensor_type_table WHERE sn_uid=" + i + " and "
                        + "sensor_table.st_uid = sensor_type_table.st_uid ORDER BY sensor_uid");
                while (rs.next()) {
                    dbSensorId = rs.getInt("sensor_uid");
                    codeId = rs.getInt("data_code");
                    measuredPhenomenon = rs.getString("measured_phenomenon");
                    if (codeId < 10) {
                        strCode = "p0" + codeId;
                    } else {
                        strCode = "p" + codeId;
                    }
                    node.addSensors(strCode, dbSensorId);
                    node.setMeasuredPhenonemon(strCode, measuredPhenomenon);
                    //System.out.println(dbSensorId + " " + codeId);
                }
                nodeMap.put(xbeeId, node);
            }
        } catch (Exception ex) {
            logger.error(ex);
            //System.out.println("Exception: " + ex.getMessage());
        } finally {
            try {
                st.close();
                con.close();
                logger.info("Successfully downloaded the nodes from MySQL database.");
            } catch (SQLException ex) {
                logger.error(ex);
            }
        }
    }
}
