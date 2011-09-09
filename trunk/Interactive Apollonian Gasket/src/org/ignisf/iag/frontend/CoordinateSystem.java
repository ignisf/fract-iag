/*
 * $Id$
 */
package org.ignisf.iag.frontend;

import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.geom.Point2D;
import math.geom2d.conic.Circle2D;
import math.geom2d.line.Line2D;
import math.geom2d.line.StraightLine2D;

/**
 * Coordinate system object.
 * @author ignisf
 */
public class CoordinateSystem {

    /**
     * Origin of the coordinate system.
     */
    protected Point2D origin;

    /**
     * Axes of the coordinate system.
     */
    protected StraightLine2D xAxis, yAxis;
    
    /**
     * The unit length of the coordinate system.
     */
    protected double unit;

    /**
     * Get the value of unit
     *
     * @return the value of unit
     */
    public double getUnit() {
        return unit;
    }

    /**
     * Get the origin of the coordinate system.
     *
     * @return the value of origin
     */
    public Point2D getOrigin() {
        return origin;
    }

    /**
     * Set the origin of the coordinate system.
     *
     * @param origin new value of origin
     */
    public final void setOrigin(Point2D origin) {
        this.origin = origin;
        this.xAxis = new StraightLine2D(origin, 0);
        this.yAxis = new StraightLine2D(origin, Math.PI/2d);
    }

    /**
     * Create a new coordinate system by origin and unit.
     * 
     * @param origin The origin of the coordinate system
     * @param unit The unit of the coordinate system.
     */
    public CoordinateSystem(Point2D origin, double unit) {
        this.setOrigin(origin);
        this.unit = unit;
    }
    
    /**
     * Resize the coordinate system from the specified center.
     * @param center
     * @param level 
     * @TODO
     */
    public void resize(Point2D center, double level){
        double x, y;
        
        if(center.getX() > origin.getX()){
            x = origin.getX() - yAxis.getDistance(center)*level;
        } else {
            x = origin.getX() + yAxis.getDistance(center)*level;
        }

        if(center.getY() > origin.getY()){
            y = origin.getY() - xAxis.getDistance(center)*level;
        } else {
            y = origin.getY() + xAxis.getDistance(center)*level;
        }
        
        this.unit += this.unit*level;
        this.setOrigin(new Point2D.Double(x, y));
    }
    
    public void move(double dx, double dy){
        this.setOrigin(new Point2D.Double(this.origin.getX()+dx,
                this.origin.getY()+dy));
    }
    
    /**
     * Drawing the coordinate system in the specified bounds.
     * 
     * @param g2d Graphics context.
     * @param bounds Bounds.
     */
    public void draw(Graphics2D g2d, Rectangle bounds){
        Point2D p00 = bounds.getLocation();
        Point2D p11 = new Point2D.Double(bounds.width, bounds.height);
        
        StraightLine2D top = new StraightLine2D(p00, 0);
        StraightLine2D right = new StraightLine2D(p11, Math.PI/2d);
        StraightLine2D left = new StraightLine2D(p00, 3*Math.PI/2d);
        StraightLine2D bottom = new StraightLine2D(p11, Math.PI);
        
        math.geom2d.Point2D isect1, isect2;
        
        isect1 = xAxis.getIntersection(left);
        isect2 = xAxis.getIntersection(right);
        
        if (isect1 != null && isect2 != null)
            new Line2D(isect1, isect2).draw(g2d);
        
        isect1 = yAxis.getIntersection(top);
        isect2 = yAxis.getIntersection(bottom);
        
        if (isect1 != null && isect2 != null)
            new Line2D(isect1, isect2).draw(g2d);
    }
    
    /**
     * Drawing the coordinate system grid in the specified bounds.
     * 
     * @param g2d Graphics context.
     * @param bounds Bounds.
     */
    public void drawGrid(Graphics2D g2d, Rectangle bounds){
        Point2D p00 = bounds.getLocation();
        Point2D p11 = new Point2D.Double(bounds.width, bounds.height);
        
        StraightLine2D top = new StraightLine2D(p00, 0);
        StraightLine2D right = new StraightLine2D(p11, Math.PI/2d);
        StraightLine2D left = new StraightLine2D(p00, 3*Math.PI/2d);
        StraightLine2D bottom = new StraightLine2D(p11, Math.PI);
        
        new Line2D(xAxis.getIntersection(left), xAxis.getIntersection(right))
                .draw(g2d);
        new Line2D(yAxis.getIntersection(top), yAxis.getIntersection(bottom))
                .draw(g2d);
        
        double d = origin.getX();
        while (d < bounds.width){
            new Line2D(d, bounds.y, d, bounds.height).draw(g2d);
            d += unit;
        }
        
        d = origin.getX();
        
        while (d > 0){
            new Line2D(d, bounds.y, d, bounds.height).draw(g2d);
            d -= unit;
        }
        
        d = origin.getY();
        
        while (d < bounds.width){
            new Line2D(bounds.x, d, bounds.width, d).draw(g2d);
            d += unit;
        }
        d = origin.getY();
        
        while (d > 0){
            new Line2D(bounds.x, d, bounds.width, d).draw(g2d);
            d -= unit;
        }
    }
}
