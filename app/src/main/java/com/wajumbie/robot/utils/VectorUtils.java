package com.wajumbie.robot.utils;

import com.aldebaran.qi.sdk.object.geometry.Transform;
import com.aldebaran.qi.sdk.object.geometry.Vector3;

/**
 * Created by Tyler on 1/23/2017.
 */

public class VectorUtils {


    public static double distance(Transform transform) {
        Vector3 t = transform.getT();
        double x = t.getX();
        double y = t.getY();
        return Math.sqrt(x * x + y * y);
    }
}
