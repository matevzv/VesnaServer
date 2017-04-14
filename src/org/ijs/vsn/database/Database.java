package org.ijs.vsn.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import org.apache.log4j.Logger;
import org.apache.tomcat.jdbc.pool.DataSource;
import org.apache.tomcat.jdbc.pool.PoolProperties;
import org.ijs.vsn.listener.ValidatorListener;
import org.ijs.vsn.node.Node;
import org.ijs.vsn.node.NodeList;
import org.ijs.vsn.server.ServerConfig;

public class Database implements ValidatorListener {

    private static final Logger logger = Logger.getLogger(Database.class);
    String recivedNodeData;
    Map sensorsIds;
    Map smp;
    public static PoolProperties pool;
    public static DataSource datasource;
    private Connection con;
    private PreparedStatement ps;

    public Database() {
        pool = new PoolProperties();
        pool.setUrl(ServerConfig.getDbAddress());
        pool.setDriverClassName("com.mysql.jdbc.Driver");
        pool.setUsername(ServerConfig.getDbUsername());
        pool.setPassword(ServerConfig.getDbPassword());
        pool.setRemoveAbandoned(true);
        pool.setJdbcInterceptors("org.apache.tomcat.jdbc.pool.interceptor.ConnectionState;"
                + "org.apache.tomcat.jdbc.pool.interceptor.StatementFinalizer");
        datasource = new DataSource();
        datasource.setPoolProperties(pool);
        con = null;
        ps = null;
    }

    public synchronized void processData(ArrayList<Object> newData) {
        //long start = System.currentTimeMillis();

        // Data from the coordinator        
        Map<String, String> mapNodeData = getQueryMap((String) newData.get(0));
        Set<String> keys = mapNodeData.keySet();
        String nodeId = mapNodeData.get("p01").toString();

        // Node in the database
        Node node = NodeList.nodeMap.get(nodeId);
        sensorsIds = node.getSensors();
        smp = node.getMeasuredPhenonemon();
        int snAutoId = node.getNode().get(nodeId);
        Double nodeLatitude = node.getLatitude();
        Double nodeLongitude = node.getLongitude();
        keys.remove("p01");

        try {
            con = datasource.getConnection();
            if (!con.isValid(0)) {
                datasource = new DataSource();
                datasource.setPoolProperties(pool);
                con = datasource.getConnection();
            }
            con.setAutoCommit(false);
            ps = con.prepareStatement("INSERT INTO "
                    + "sensor_measurement_table(sensor_uid,timestamp,value) "
                    + "VALUES(?,?,?)");
            for (String key : keys) {
                String measuredPhenomenon = smp.get(key).toString();
                Double measurement = Double.parseDouble(mapNodeData.get(key));
                ps.setString(1, sensorsIds.get(key).toString());
                ps.setString(2, newData.get(1).toString());
                ps.setString(3, measurement.toString());
                ps.addBatch();
                if (measuredPhenomenon.equals("latitude") && !measurement.equals(nodeLatitude)) {
                    node.setLatitude(measurement);
                    nodeLatitude = measurement;
                    ps.addBatch("UPDATE sensor_node_table SET gps_latitude=" + measurement + " WHERE sn_auto_id=" + snAutoId + ";");
                } else if (measuredPhenomenon.equals("longitude") && !measurement.equals(nodeLongitude)) {
                    node.setLongitude(measurement);
                    nodeLongitude = measurement;
                    ps.addBatch("UPDATE sensor_node_table SET gps_longitude=" + measurement + " WHERE sn_auto_id=" + snAutoId + ";");
                }
            }
            ps.executeBatch();
            con.commit();
        } catch (Exception ex) {
            logger.error(ex);
        } finally {
            if (ps != null) {
                try {
                    ps.close();
                } catch (SQLException ignore) {
                }
            }
            if (con != null) {
                try {
                    con.close();
                } catch (SQLException ignore) {
                }
            }
        }
        //long stop = System.currentTimeMillis();
        //logger.info("Time to insert one request in the DB: " + (stop - start));
    }

    @Override
    public void validData(ArrayList<Object> newData) {
        try {
            // TODO Auto-generated method stub
            processData(newData);
        } catch (Exception ex) {
            logger.error("Failed to process data: " + newData.get(0));
        }
        //logger.info("Working thread in Database: " + Thread.currentThread().getName());
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
                    Double fValue = Double.parseDouble(param.split("=")[1]);
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
