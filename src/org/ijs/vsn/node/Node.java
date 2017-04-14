/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.ijs.vsn.node;

import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author Matevz
 */
public class Node {

    private Map<String, Integer> node;
    private Map<String, Integer> sensors;
    private Map<String, String> measuredPhenonemon;
    private Double latitude;
    private Double longitude;

    public Node() {
        node = new HashMap<String, Integer>();
        sensors = new HashMap<String, Integer>();
        measuredPhenonemon = new HashMap<String, String>();
    }

    public void addNode(String xbeeId, int dbNodeId) {
        node.put(xbeeId, dbNodeId);
    }

    public void addSensors(String codeId, int dbSensorId) {
        sensors.put(codeId, dbSensorId);
    }

    public Map<String, Integer> getNode() {
        return node;
    }

    public Map<String, Integer> getSensors() {
        return sensors;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public Map<String, String> getMeasuredPhenonemon() {
        return measuredPhenonemon;
    }

    public void setMeasuredPhenonemon(String codeId, String measuredPhenonemon) {
        this.measuredPhenonemon.put(codeId, measuredPhenonemon);
    }
}
