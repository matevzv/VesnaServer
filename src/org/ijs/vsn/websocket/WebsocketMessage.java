/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.ijs.vsn.websocket;

import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import org.apache.log4j.Logger;
import org.eclipse.jetty.websocket.WebSocket;
import org.eclipse.jetty.websocket.WebSocket.Connection;
import org.eclipse.jetty.websocket.WebSocketClient;
import org.eclipse.jetty.websocket.WebSocketClientFactory;
import org.ijs.vsn.listener.ValidatorListener;
import org.ijs.vsn.server.ServerConfig;

/**
 *
 * @author Matevz
 */
public class WebsocketMessage implements ValidatorListener {

    private static final Logger logger = Logger.getLogger(WebsocketMessage.class);
    private WebSocketClient client;
    private int errorCounter;

    public WebsocketMessage() {
        errorCounter = 0;
        try {
            WebSocketClientFactory factory = new WebSocketClientFactory();
            factory.start();

            client = factory.newWebSocketClient();
            // Configure the client

        } catch (Exception ex) {
            logger.info(ex);
            return;
        }
    }

    @Override
    public void validData(ArrayList<Object> validData) {
        Map<String, String> mapNodeData = getQueryMap((String) validData.get(0));
        Set<String> keys = mapNodeData.keySet();
        if (errorCounter < 10) {
            if (mapNodeData.containsKey("p04")) {
                String temperature = mapNodeData.get("p04").toString();

                if (Float.parseFloat(temperature) > -100 && Float.parseFloat(temperature) < 100) {
                    try {
                        WebSocket.Connection connection = client.open(new URI(ServerConfig.getWsAddress()), new WebSocket.OnTextMessage() {
                            public void onOpen(Connection connection) {
                                // open notification
                            }

                            public void onClose(int closeCode, String message) {
                                // close notification
                            }

                            public void onMessage(String data) {
                                // handle incoming message
                            }
                        }).get(5, TimeUnit.SECONDS);

                        try {
                            connection.sendMessage(temperature);
                        } catch (Exception ex) {
                            logger.info("Error while sending message: " + temperature);
                        } finally {
                            connection.disconnect();
                        }
                        errorCounter = 0;
                        //logger.info(temperature);
                    } catch (Exception ex) {
                        errorCounter++;
                        logger.info(ex);
                    }
                } else {
                    logger.info("Temperature out of bounds: " + temperature);
                }
            } else {
                logger.info("No temperature data: " + validData.get(0));
            }
        } else {
            logger.info("Error counter suggests that websocket connection has problems!");
        }
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
