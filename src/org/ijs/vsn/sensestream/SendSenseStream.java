/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.ijs.vsn.sensestream;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import org.apache.log4j.Logger;
import org.ijs.vsn.listener.ValidatorListener;
import org.ijs.vsn.node.Node;
import org.ijs.vsn.node.NodeList;
import org.ijs.vsn.server.ServerConfig;

/**
 *
 * @author Matevz
 */
public class SendSenseStream implements ValidatorListener {

    private static final Logger logger = Logger.getLogger(SendSenseStream.class);

    public SendSenseStream() {
    }

    @Override
    public void validData(ArrayList<Object> newData) {
        Map<String, String> mapNodeData = getQueryMap((String) newData.get(0));
        Set<String> keys = (Set<String>) mapNodeData.keySet();
        String xbeeId = mapNodeData.get("p01").toString();
        Node node = NodeList.nodeMap.get(xbeeId);
        Map sensorsIds = node.getSensors();

        keys.remove("p01");
        String data = "p=";
        for (String key : keys) {
            data += sensorsIds.get(key) + ":" + mapNodeData.get(key) + ":";
        }
        data = data.substring(0, data.length() - 1);
        String time = ((String) newData.get(1)).replace(" ", "%20");
        sendGetRequest(ServerConfig.getSenseStreamAddress(), data + "&ts=" + time);
        //logger.info(ServerConfig.getSenseStreamAddress() + "?" + data + "&ts=" + newData.get(1));
        //logger.info("Working thread in SenseStream: " + Thread.currentThread().getName());
    }

    public synchronized String sendGetRequest(String endpoint, String requestParameters) {
        String response = null;
        if (endpoint.startsWith("http://")) {
            // Send a GET request to the servlet
            try {
                // Construct data
                StringBuilder data = new StringBuilder();

                // Send data
                String urlStr = endpoint;
                if (requestParameters != null && requestParameters.length() > 0) {
                    urlStr += "?" + requestParameters;
                }
                URL url = new URL(urlStr);
                URLConnection conn = url.openConnection();
                conn.setConnectTimeout(100);

                // Get the response
                BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                try {
                    StringBuilder sb = new StringBuilder();
                    String line;
                    while ((line = rd.readLine()) != null) {
                        sb.append(line);
                    }
                    response = sb.toString();
                } finally {
                    rd.close();
                }
            } catch (Exception ex) {
                logger.error(ex);
            }
        }
        return response;
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
