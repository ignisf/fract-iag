/**
 * $Id$
 */

package org.ignisf.iag.descartes;

import org.apache.commons.math.complex.Complex;

/**
 * SoddyCircle abstract data type.
 * @author Petko Bordjukov
 */
public class SoddyCircle {
    
    /**
     * The cooridiantes of the center of the circle.
     */
    public double x, y;
    
    /**
     * The curvature of the circle.
     */
    public double k;
    
    /**
     * The level of the circle.
     */
    public int l;
    
    /**
     * The main constructor of the SoddyCircle class. Construc a new Soddy
     * Circle by coordinates of the center, curvature and level.
     * @param x The x coordinate of the center.
     * @param y The y coordinate of the center.
     * @param k The curvature of the circle.
     * @param l The level of the circle.
     */
    public SoddyCircle(double x, double y, double k, int l){
        this.x = x;
        this.y = y;
        this.k = k;
        this.l = l;
    }
    
    /**
     * The main constructor of the SoddyCircle class. Construc a new Soddy
     * Circle by the center as a complex number, curvature and level.
     * @param z The center as a complex number (x + iy).
     * @param k The curvature of the circle.
     * @param l The level of the circle.
     */
    public SoddyCircle(Complex z, double k, int l){
        this(z.getReal(), z.getImaginary(), k, l);
    }
    
    /**
     * Get the center of the circle as a complex number.
     * @return The center of the circle - x + iy.
     */
    public Complex z(){
        return new Complex(x, y);
    }
    
}
