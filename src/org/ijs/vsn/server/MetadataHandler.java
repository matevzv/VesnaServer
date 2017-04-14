/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.ijs.vsn.server;

import java.net.URLDecoder;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.log4j.Logger;
import org.apache.tomcat.jdbc.pool.DataSource;
import org.ijs.vsn.database.Database;
import org.ijs.vsn.node.*;

/**
 *
 * @author Matevz
 */
public class MetadataHandler extends HttpServlet {

    private static final Logger logger = Logger.getLogger(MetadataHandler.class);

    private String updateDatabase(Map<String, List<String>> parameterMap) {
        String sensorNodeId = "";
        try {
            Connection con = null;
            Statement st = null;
            ResultSet result = null;
            Node newNode = new Node();
            int nodeId = -1;
            int typeId = -1;
            int dataCode;
            int sensorId = -1;
            sensorNodeId = parameterMap.get("p01").get(0);
            try {
                con = Database.datasource.getConnection();
                if (!con.isValid(0)) {
                    Database.datasource = new DataSource();
                    Database.datasource.setPoolProperties(Database.pool);
                    con = Database.datasource.getConnection();
                }
                con.setAutoCommit(false);
                st = con.createStatement();

                result = st.executeQuery("SELECT sn_auto_id FROM sensor_node_table WHERE sn_uid = " + "'" + sensorNodeId + "';");
                if (result.next()) {
                    return sensorNodeId;
                } else {
                    if (result.last()) {
                        return sensorNodeId;
                    } else {
                        st.execute("INSERT INTO sensor_node_table (sn_uid) VALUES('" + sensorNodeId + "');", Statement.RETURN_GENERATED_KEYS);
                        ResultSet rs = st.getGeneratedKeys();
                        if (rs.next()) {
                            nodeId = rs.getInt(1);
                        }
                        rs.close();
                        newNode.addNode(sensorNodeId, nodeId);
                    }
                }

                for (Map.Entry<String, List<String>> entry : parameterMap.entrySet()) {

                    String sensorCodeId = "";
                    if (entry.getKey() != null) {
                        sensorCodeId = entry.getKey();
                        if (sensorCodeId.equals("p01")) {
                            continue;
                        }
                    }

                    String sensorType = "";
                    if (entry.getValue().size() >= 1) {
                        sensorType = entry.getValue().get(0);
                    }

                    String measured_phenomenon = "";
                    if (entry.getValue().size() >= 2) {
                        measured_phenomenon = entry.getValue().get(1);
                    }

                    String measurementUnit = "";
                    if (entry.getValue().size() >= 3) {
                        measurementUnit = entry.getValue().get(2);
                    }

                    dataCode = Integer.parseInt(sensorCodeId.replace("p", ""));
                    result = st.executeQuery("SELECT st_uid FROM sensor_type_table WHERE sensor_type = " + "'" + sensorType + "'"
                            + " AND measured_phenomenon = " + "'" + measured_phenomenon + "'"
                            + " AND unit_of_measurement = " + "'" + measurementUnit + "';");

                    if (result.next()) {
                        typeId = result.getInt("st_uid");
                    } else {
                        st.execute("INSERT INTO sensor_type_table "
                                + "(sensor_type,measured_phenomenon,unit_of_measurement) "
                                + "VALUES('" + sensorType + "','"
                                + measured_phenomenon + "','"
                                + measurementUnit + "');", Statement.RETURN_GENERATED_KEYS);
                        ResultSet rs = st.getGeneratedKeys();
                        if (rs.next()) {
                            typeId = rs.getInt(1);
                        }
                        rs.close();
                    }

                    st.execute("INSERT INTO sensor_table "
                            + "(sn_uid,st_uid,data_code) VALUES('" + nodeId
                            + "','" + typeId + "','" + dataCode + "');", Statement.RETURN_GENERATED_KEYS);
                    ResultSet rs = st.getGeneratedKeys();
                    if (rs.next()) {
                        sensorId = rs.getInt(1);
                    }
                    rs.close();
                    newNode.addSensors(sensorCodeId, sensorId);
                    newNode.setMeasuredPhenonemon(sensorCodeId, measured_phenomenon);
                }

                NodeList.nodeMap.put(sensorNodeId, newNode);

            } catch (Exception ex) {
                logger.error(ex);
            } finally {
                st.close();
                result.close();
                con.setAutoCommit(true);
                con.close();
            }
        } catch (SQLException ex) {
            logger.error(ex);
        }
        return sensorNodeId;
    }

    public Map<String, List<String>> getQueryParams(String url) {
        try {
            Map<String, List<String>> params = new HashMap<String, List<String>>();
            String query = url;
            for (String param : query.split("&")) {
                String[] pair = param.split("=");
                String key = pair[0];
                String value = "";
                if (pair.length > 1) {
                    value = pair[1];
                }

                List<String> values = params.get(key);
                if (values == null) {
                    values = new ArrayList<String>();
                    params.put(key, values);
                }
                values.add(value);
            }

            return params;
        } catch (Exception ex) {
            throw new AssertionError(ex);
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) {
        try {
            String queryString = URLDecoder.decode(request.getQueryString(), "UTF-8");
            Map<String, List<String>> parameterMap = getQueryParams(queryString);

            String nodeId = updateDatabase(parameterMap);

            response.getWriter().print(nodeId + " was added to the database!");

        } catch (Exception ex) {
            logger.error(ex);
        }
    }
}
