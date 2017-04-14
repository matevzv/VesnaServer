/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.ijs.vsn.validator;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import org.apache.log4j.Logger;
import org.ijs.vsn.listener.ServerListener;
import org.ijs.vsn.listener.ValidatorListener;
import org.ijs.vsn.node.Node;
import org.ijs.vsn.node.NodeList;
import org.ijs.vsn.server.ServerConfig;

/**
 *
 * @author Matevz
 */
public class DataValidator implements ServerListener {

    private static final Logger logger = Logger.getLogger(DataValidator.class);
    private ArrayList<ValidatorListener> registerValidatorListener = new ArrayList<ValidatorListener>();
    private ThreadPoolExecutor threadPool = null;
    private LinkedBlockingQueue<Runnable> queue;
    //int poolSize = 100;
    //int maxPoolSize = 1000;
    //long keepAliveTime = 0;

    public DataValidator() {
        //queue = new LinkedBlockingQueue<Runnable>();
        //threadPool = new ThreadPoolExecutor(poolSize, maxPoolSize,
        //keepAliveTime, TimeUnit.SECONDS, queue);
    }

    public void addValidatorListener(ValidatorListener listener) {
        registerValidatorListener.add(listener);
    }

    public void removeValidatorListener(ValidatorListener listener) {
        registerValidatorListener.remove(listener);
    }

    public synchronized void notifyValidatorListeners(ArrayList<Object> validData) {
        for (int i = 0; i < registerValidatorListener.size(); i++) {
            //threadPool.execute(new ThreadNotify(i, validData, registerValidatorListener));
            ((ValidatorListener) registerValidatorListener.get(i)).validData(validData);
        }
    }

    private boolean sendMetadataRequest(InetAddress ip, int port, String xbee) {
        Socket socket = null;
        Boolean connected = false;
        int count = 0;
        while (count < 10) {
            try {
                socket = new Socket(ip, port);
                boolean autoflush = true;
                PrintWriter out = new PrintWriter(socket.getOutputStream(), autoflush);

                // send request to the server
                out.print("NO=" + xbee + ",DA=ID\\r\\n");
                out.close();
                //socket.getOutputStream().flush();
                socket.close();
                connected = true;
                logger.info("Request for metadata of node with Xbee address: "
                        + xbee + " has been send to node in atempt: " + (count + 1) + "!");
                break;
            } catch (IOException ex) {
                logger.error(ex);
            } finally {
                count++;
            }
        }
        if (!connected) {
            logger.info("Request for metadata of node with Xbee address: "
                    + xbee + " has failed!");
        }
        return connected;
    }

    @Override
    public void newData(ArrayList<Object> newData) {
        try {
            if (newData.get(0) != null) {
                String tmp = (String) newData.get(0);
                int index1 = tmp.lastIndexOf('&');
                int index2 = tmp.lastIndexOf('=');
                if (index1 != -1 && index2 != -1 && (index1 < index2)) {
                    tmp = tmp.substring(index1 + 1, index2);
                    if (tmp.length() == 3) {
                        TreeMap<String, String> mapNodeData = getQueryMap((String) newData.get(0));
                        if (mapNodeData != null) {
                            String xbeeId = (String) mapNodeData.get("p01");
                            if (NodeList.nodeMap.containsKey(xbeeId)) {
                                if (validateDataString(mapNodeData)) {
                                    tmp = mapNodeData.toString();
                                    tmp = tmp.replace(" ", "");
                                    tmp = tmp.replace("{", "");
                                    tmp = tmp.replace("}", "");
                                    tmp = tmp.replace(",", "&");
                                    newData.remove(0);
                                    newData.add(0, tmp);
                                    //logger.info("Thread name: " + Thread.currentThread().getName());
                                    notifyValidatorListeners(newData);
                                } else {
                                    logger.info("Corrupted data: " + newData.get(0));
                                    return;
                                }
                            } else {
                                logger.info("Xbee address " + xbeeId + " is not in the database!");
                                //Metadata request sending
                                InetSocketAddress ipPort = (InetSocketAddress) newData.get(2);
                                if (ServerConfig.getMetadataFlag() && sendMetadataRequest(ipPort.getAddress(), ipPort.getPort(), xbeeId)) {
                                    logger.info("Metadata request for Xbee: " + xbeeId + " was sent successfully!");
                                }
                                return;
                            }
                        } else {
                            logger.info("Corrupted data: " + newData.get(0));
                            return;
                        }
                    } else {
                        logger.info("Corrupted data: " + newData.get(0));
                        return;
                    }
                } else {
                    logger.info("Corrupted data: " + newData.get(0));
                    return;
                }
            } else {
                logger.info("Corrupted data: " + newData.get(0));
                return;
            }
        } catch (Exception ex) {
            logger.error("Validation error: " + newData.get(0));
            logger.error(ex);
        }
        //logger.info("Working thread in Validator: " + Thread.currentThread().getName());
    }

    private boolean waitCheckNodeVector(ArrayList<String> data) {
        boolean nodeAdded = false;
        return nodeAdded;
    }

    public TreeMap<String, String> getQueryMap(String query) {
        TreeMap<String, String> map = null;
        try {
            if (query.charAt(query.length() - 1) != '='
                    && query.charAt(query.length() - 1) != '&' && query != null) {
                map = new TreeMap<String, String>();
                String[] params = query.split("&");
                for (String param : params) {
                    String name = "";
                    String value = "";
                    if (param.contains("p01")) {
                        name = param.split("=")[0];
                        value = param.split("=")[1];
                        if (value.length() == 0) {
                            map = null;
                            return map;
                        }
                    } else {
                        name = param.split("=")[0];
                        Double fValue = Double.valueOf(0);
                        try {
                            fValue = Double.parseDouble(param.split("=")[1]);
                        } catch (Exception e) {
                            logger.info("Missing data for sensor " + name
                                    + " on node " + map.get("p01"));
                        } finally {
                            value = fValue.toString();
                        }
                    }
                    map.put(name, value);
                }
            }
        } catch (Exception ex) {
            logger.error(ex);
        } finally {
            return map;
        }
    }

    private Boolean validateDataString(TreeMap<String, String> data) {
        Boolean valid = false;
        String xbeeId = (String) data.get("p01");
        Node node = NodeList.nodeMap.get(xbeeId);
        Map sensorsIds = node.getSensors();
        if (sensorsIds.size() >= data.size() - 1) {
            valid = true;
        }
        return valid;
    }

    private class ThreadNotify implements Runnable {

        int i;
        ArrayList<Object> newData;
        ArrayList<ValidatorListener> listener;

        public ThreadNotify(int i, ArrayList<Object> newData, ArrayList<ValidatorListener> listener) {
            this.i = i;
            this.newData = newData;
            this.listener = listener;
        }

        @Override
        public void run() {
            ((ValidatorListener) listener.get(i)).validData(newData);
        }
    }
}
