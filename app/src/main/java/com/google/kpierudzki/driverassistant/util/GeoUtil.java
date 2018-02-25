package com.google.kpierudzki.driverassistant.util;

import com.google.kpierudzki.driverassistant.common.model.Coordinate;

/**
 * https://stackoverflow.com/questions/3694380/calculating-distance-between-two-points-using-latitude-longitude-what-am-i-doi
 * Created by Kamil on 26.06.2017.
 */

public class GeoUtil {

    /**
     * @return Distance in meters.
     */
    public static double distance(Coordinate coordinate1, Coordinate coordinate2) {
        double a = (coordinate1.latitude - coordinate2.latitude) * GeoUtil.distPerLat(coordinate1.latitude);
        double b = (coordinate1.longitude - coordinate2.longitude) * GeoUtil.distPerLng(coordinate1.latitude);
        return Math.sqrt(a * a + b * b);
    }

    private static double distPerLng(double lat) {
        return 0.0003121092 * Math.pow(lat, 4)
                + 0.0101182384 * Math.pow(lat, 3)
                - 17.2385140059 * lat * lat
                + 5.5485277537 * lat + 111301.967182595;
    }

    private static double distPerLat(double lat) {
        return -0.000000487305676 * Math.pow(lat, 4)
                - 0.0033668574 * Math.pow(lat, 3)
                + 0.4601181791 * lat * lat
                - 1.4558127346 * lat + 110579.25662316;
    }
}
