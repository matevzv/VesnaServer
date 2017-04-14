/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.ijs.vsn.gsn;

import java.text.Normalizer;
import java.util.ArrayList;
import java.util.List;
import org.apache.log4j.Logger;
import org.geonames.Toponym;
import org.geonames.WebService;

/**
 *
 * @author Matevz
 */
public class GeoNames {

    private static final Logger logger = Logger.getLogger(GeoNames.class);

    public static ArrayList<String> getGeoNames(double gpsLatitude, double gpsLongitude) {
        ArrayList<String> geoNames = new ArrayList<String>();
        try {
            WebService.setUserName("matevz");
            List<Toponym> toponyms = WebService.findNearbyPlaceName(gpsLatitude, gpsLongitude);
            for (Toponym toponym : toponyms) {
                geoNames.add(getAsciiString(toponym.getName()));
                geoNames.add(getAsciiString(toponym.getCountryName()));
            }
        } catch (Exception ex) {
            logger.error(ex);
        }
        return geoNames;
    }

    public static String getAsciiString(String st) {
        String input = st;
        input = Normalizer.normalize(input, Normalizer.Form.NFD).replaceAll("[^\\p{ASCII}]", "");
        return input;
    }
}
