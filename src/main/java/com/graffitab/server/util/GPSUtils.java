package com.graffitab.server.util;

import org.javatuples.Pair;

public class GPSUtils {

	public static final double R = 6378137.0; // Earth’s radius, sphere.

	public static Pair<Double, Double> offsetCoordinates(Double latitude, Double longitude, int offsetInMeters) {
		// Coordinate offsets in radians.
		Double dLat = (double)offsetInMeters / R;
		Double dLon = offsetInMeters / (R*Math.cos(Math.PI*latitude/180));

		// OffsetPosition, decimal degrees.
		Double latO = latitude + dLat * 180/Math.PI;
		Double lonO = longitude + dLon * 180/Math.PI;

		return new Pair<Double, Double>(latO, lonO);
	}
}
