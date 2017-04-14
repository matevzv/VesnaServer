/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.ijs.vsn.server;

import java.io.UnsupportedEncodingException;
import java.net.InetSocketAddress;
import java.net.URLDecoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.log4j.Logger;
import org.ijs.vsn.node.NodeList;

/**
 *
 * @author Matevz
 */
public class TimestampedDataHandler extends HttpServlet {

    private static final Logger logger = Logger.getLogger(DataHandler.class);
    private NewDataEvent nde;

    public TimestampedDataHandler(NewDataEvent nde) {
        this.nde = nde;
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
        String query = "";
        String formatedTime = TimeHandler.getTime("yyyy-MM-dd HH:mm:ss");

        String sensorNodeId = "";

        try {
            String queryString = URLDecoder.decode(request.getQueryString(), "UTF-8");
            Map<String, List<String>> params = getQueryParams(queryString);

            InetSocketAddress ipPort = new InetSocketAddress(request.getRemoteAddr(), request.getRemotePort());

            for (Map.Entry<String, List<String>> param : params.entrySet()) {
                String key = param.getKey();
                List<String> values = param.getValue();
                if (values.size() == 2) {
                    Long unixTimestamp = Long.parseLong(values.get(0));
                    Long currentDate = new Date().getTime();
                    Date dt;
                    if (String.valueOf(unixTimestamp).length() == String.valueOf(currentDate).length()) {
                        // in milliseconds
                        dt = new Date(unixTimestamp);
                    } else {
                        // in seconds
                        dt = new Date(unixTimestamp * 1000);
                    }
                    SimpleDateFormat dtf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    formatedTime = dtf.format(dt);
                    query += key + "=" + values.get(1) + "&";
                } else if (key.equals("p01")) {
                    sensorNodeId = values.get(0);
                    query += key + "=" + sensorNodeId + "&";
                } else {
                    query += key + "=" + values.get(0) + "&";
                }
            }

            if (query.charAt(query.length() - 1) == '&') {
                query = query.substring(0, query.length() - 1);
            }

            ArrayList<Object> newData = new ArrayList<Object>();
            newData.add(query);
            newData.add(formatedTime);
            newData.add(ipPort);
            if (NodeList.nodeMap.containsKey(sensorNodeId)) {
                response.getWriter().print("we know this node :)");
                nde.newDataNotification(newData);
            } else {
                response.getWriter().print("we do not know this node please send metadata :)");
                logger.info("Request for metadata of the node with the address: "
                        + sensorNodeId + " has been send to " + ipPort.getAddress().getHostAddress() + "!");
            }
        } catch (Exception ex) {
            logger.error("Validator listeners didn't get the coordinators message: " + query);
            logger.error(ex);
        }
    }
}
