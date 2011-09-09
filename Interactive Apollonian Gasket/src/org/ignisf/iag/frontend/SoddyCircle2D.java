/**
 * $Id$
 * @TODO fix this.
 */

package org.ignisf.iag.frontend;

import java.awt.geom.Point2D;
import math.geom2d.conic.Circle2D;
import org.ignisf.iag.descartes.SoddyCircle;

/**
 * Soddy circle primitive
 * @author ignisf
 */
public class SoddyCircle2D extends Circle2D {
    /**
     * The level at which the circle is located.
     */
    public int level = 0;
    
    /**
     * The curvature.
     */
    protected double curvature;
    
    /**
     * Empty constructor: center 0,0, infinite curvature and level 0.
     */
    public SoddyCircle2D() {
        this(0, 0, Double.MAX_VALUE, 0, true);
    }

    /**
     * Soddy circle by center and curvature
     * @param center The center of the circle
     * @param curvature the curvature of the circle
     * @param level the level of the circle.
     */
    public SoddyCircle2D(Point2D center, double curvature, int level) {
        this(center.getX(), center.getY(), curvature, level, true);
    }

    /** Create a new circle with specified center, radius and orientation */
    /**
     * Soddy circle by center, curvature and orientation.
     * @param center The center of the circle
     * @param curvature the curvature of the circle
     * @param level the level of the circle.
     * @param direct orientation
     */
    public SoddyCircle2D(Point2D center, double curvature, int level,
            boolean direct) {
        this(center.getX(), center.getY(), curvature, level, direct);
    }

    /**
     * Create a new soddy circle by center, curvature, level and orientation.
     * @param xcenter x coordiante of the center
     * @param ycenter y coordinate of the center
     * @param curvature curvature of the circle
     * @param level level of the circle
     */
    public SoddyCircle2D(double xcenter, double ycenter, double curvature,
            int level) {
        this(xcenter, ycenter, curvature, level, true);
    }

    /**
     * Create a SoddyCircle2D by a SoddyCircle.
     * @param c The circle.
     */
    public SoddyCircle2D(SoddyCircle c){
        this(c.x, c.y, c.k, c.l, true);
    }
    
    /**
     * Create a new soddy circle by center, curvature, level and orientation.
     * @param xcenter x coordiante of the center
     * @param ycenter y coordinate of the center
     * @param curvature curvature of the circle
     * @param level level of the circle
     * @param direct orientation
     */
    public SoddyCircle2D(double xcenter, double ycenter, double curvature,
            int level, boolean direct) {
        super(xcenter, ycenter, Math.abs(1d/curvature), direct);
        this.curvature = curvature;
        this.level = level;
    }
    
    /**
     * Curvature getter.
     * @return The curvature.
     */
    public double getCurvature(){
        return curvature;
    }
    
    /**
     * Get a SoddyCircle from this SoddyCircle2D
     * @return The SoddyCircle
     */
    public SoddyCircle getSoddyCircle(){
        return new SoddyCircle(this.xc, this.yc, this.curvature, this.level);
    }
}

