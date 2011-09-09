/**
 * $Id$
 */
package org.ignisf.iag.frontend;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Iterator;
import math.geom2d.line.Line2D;

/**
 *
 * @author ignisf
 */
public class Line2DGrid {
    /**
     * The distance between the lines of the grid.
     */
    protected double size;
    
    /**
     * The overall width of the grid.
     */
    protected double width;
    
    /**
     * The overall height of the grid.
     */
    protected double height;
    
    /**
     * Origin of the coordinate system.
     */
    protected Point2D origin;
    
    /**
     * The color of the grid.
     */
    protected Color color;

    /**
     * Get the value of color
     *
     * @return the value of color
     */
    public Color getColor() {
        return color;
    }

    /**
     * Set the value of color
     *
     * @param color new value of color
     */
    public void setColor(Color color) {
        this.color = color;
    }

    
    /**
     * The vertical lines.
     */
    protected ArrayList<Line2D> vlines;
    
    /**
     * The horizontal lines.
     */
    protected ArrayList<Line2D> hlines;
    
    /**
     * Construct a grid of Line2D objects by width, height and distance between
     * the lines.
     * @param w Overall width of the grid.
     * @param h Overall height of the grid.
     * @param s Distance between the lines.
     */
    public Line2DGrid(double w, double h, double s){
        this.width = w;
        this.height = h;
        this.size = s;
        
        this.origin = new Point2D.Double(w/2d, h/2d);
        double vstart = w/2d;
        double hstart = h/2d;
        
        this.vlines = new ArrayList<Line2D>(1);
        vlines.add(new Line2D(vstart, 0, vstart, height));
        
        double dist = s;
        while (dist <= width) {
            vlines.add(new Line2D(vstart + dist, 0, vstart + dist, height));
            vlines.add(new Line2D(vstart - dist, 0, vstart - dist, height));
            dist += s;
        }
        
        this.hlines = new ArrayList<Line2D>(1);
        vlines.add(new Line2D(0, hstart, width, hstart));
        
        dist = s;
        while (dist <= height) {
            vlines.add(new Line2D(0, hstart + dist, width, hstart + dist));
            vlines.add(new Line2D(0, hstart - dist, width, hstart - dist));
            dist += s;
        }
    }
    
    /**
     * Auxiliarry constructor - creates a grid with size 10.
     * @param w Width of the grid.
     * @param h Height of the grid.
     */
    public Line2DGrid(double w, double h){
        this(w, h, 10);
    }
    
    
    public void resize(double factor, Point2D center){
        
    }
    
    public void draw(Graphics2D g2d){
        Iterator<Line2D> i = vlines.iterator();
        while (i.hasNext()){
            i.next().draw(g2d);
        }
        
        i = hlines.iterator();
        while (i.hasNext()){
            i.next().draw(g2d);
        }
    }
}
