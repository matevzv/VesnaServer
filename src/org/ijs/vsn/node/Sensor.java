/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.ijs.vsn.node;

/**
 *
 * @author Matevz
 */
public class Sensor {

    private String dataCode;
    private String sensorType;
    private String MeasuredPhenomenon;
    private String measurementUnit = "";

    public String getDataCode() {
        return dataCode;
    }

    public void setDataCode(String dataCode) {
        this.dataCode = dataCode;
    }

    public String getSensorType() {
        return sensorType;
    }

    public void setSensorType(String sensorType) {
        this.sensorType = sensorType;
    }

    public String getMeasuredPhenomenon() {
        return MeasuredPhenomenon;
    }

    public void setMeasuredPhenomenon(String MeasuredPmenomenon) {
        this.MeasuredPhenomenon = MeasuredPmenomenon;
    }

    public String getMeasurementUnit() {
        return measurementUnit;
    }

    public void setMeasurementUnit(String measurementUnit) {
        if (measurementUnit.equals("percent")) {
            measurementUnit = "%";
        }
        if (measurementUnit.equals("degC")) {
            measurementUnit = "°C";
        }
        this.measurementUnit = measurementUnit;
    }
}
