/**
 * $Id$
 */

package org.ignisf.iag.descartes;

import java.util.ArrayList;
import java.util.Collection;
import org.apache.commons.math.complex.Complex;

/**
 * Descartes theorem helper class.
 * http://en.wikipedia.org/wiki/Descartes'_theorem
 * Bo Söderberg, Apollonian Tiling, the Lorentz Group and Regular Trees
 * (http://citeseerx.ist.psu.edu/viewdoc/summary?doi=10.1.1.33.1756)
 * http://en.wikibooks.org/wiki/Fractals/Apollonian_fractals
 * @author Petko Bordjukov
 */
public class DescartesTheorem {
    /**
     * Get the curvatures of the two children of three mutually tangent circles.
     * http://en.wikipedia.org/wiki/Descartes'_theorem#Descartes.27_theorem
     * @param a1 First ancestor.
     * @param a2 Second ancestor.
     * @param a3 Third ancestor.
     * @return The two curvatures.
     */
    public static double[] descartes(SoddyCircle a1, SoddyCircle a2,
            SoddyCircle a3){
        
        /**
         * The first part of the equation.
         */
        double p = a1.k + a2.k + a3.k;
        
        /**
         * The second part of the eqation.
         */
        double q = Math.sqrt(a1.k*a2.k + a2.k*a3.k + a3.k*a1.k)*2d;
        
        return new double[] {p+q, p-q};
    }
    
    /**
     * Supplement three kissing circles to a Descartes' configuration with their
     * two children.
     * http://en.wikipedia.org/wiki/Descartes'_theorem#Complex_Descartes_theorem
     * @param a1 First ancestor.
     * @param a2 Second ancestor.
     * @param a3 Third ancestor.
     * @param k4 Curvature of the first child.
     * @param k5 Curvature of the second child.
     * @return The two children.
     */
    public static Collection<SoddyCircle> complexDescartes(SoddyCircle a1,
            SoddyCircle a2, SoddyCircle a3, double k4, double k5){
        
        /**
         * First part of the equation.
         */
        Complex p =  a1.z().multiply(a1.k)
                .add(a2.z().multiply(a2.k))
                .add(a3.z().multiply(a3.k));
        
        /**
         * Second part of the eqation.
         */
        Complex q =  a1.z().multiply(a2.z()).multiply(a1.k*a2.k)
                .add(a2.z().multiply(a3.z()).multiply(a2.k*a3.k))
                .add(a3.z().multiply(a1.z()).multiply(a3.k*a1.k))
                .sqrt().multiply(2);
        
        /**
         * Center of the first child.
         */
        Complex z4 = p.add(q).divide(new Complex(k4, 0d));
        
        /**
         * Center of the second child.
         */
        Complex z5 = p.subtract(q).divide(new Complex(k5, 0d));
        
        /**
         * The two child circles.
         */
        ArrayList<SoddyCircle> children = new ArrayList<SoddyCircle>(2);
        
        children.add(new SoddyCircle(z4, k4, 0));
        children.add(new SoddyCircle(z5, k5, 0));
        
        return children;
    }
    
    /**
     * Supplement three kissing circles to a Descartes' configuration with their
     * two children.
     * @param a1 First ancestor.
     * @param a2 Second ancestor.
     * @param a3 Third ancestor.
     * @return The two children.
     */
    public static Collection<SoddyCircle> complexDescartes(SoddyCircle a1,
            SoddyCircle a2, SoddyCircle a3){
        
        /**
         * The curvatures of the two children.
         */
        double[] curvatures = DescartesTheorem.descartes(a1, a2, a3);
        
        return DescartesTheorem.complexDescartes(a1, a2, a3,
                curvatures[0], curvatures[1]);
    }
    
    /**
     * Get the curvature of the daughter by mother and three ancestors.
     * Bo Söderberg, Apollonian Tiling, the Lorentz Group and Regular Trees,
     * Formula 17
     * @param m Mother circle.
     * @param a1 First ancestor.
     * @param a2 Second ancestor.
     * @param a3 Third ancestor.
     * @return The curvature of the daughter.
     */
    public static double getDaughterCurvature(SoddyCircle m, SoddyCircle a1,
            SoddyCircle a2, SoddyCircle a3){
        return 2 * (m.k + a2.k + a3.k) - a1.k;
    }
    
    /**
     * Get the daughter by mother, three ancestors.
     * Complex variant of the equation used in getDaughterCurvature.
     * @param m The mother
     * @param a1 First ancestor.
     * @param a2 Second acnestor.
     * @param a3 Third ancestor.
     * @return 
     */
    public static SoddyCircle getDaughter(SoddyCircle m, SoddyCircle a1,
            SoddyCircle a2, SoddyCircle a3){
        return DescartesTheorem.getDaughter(m, a1, a2, a3,
                DescartesTheorem.getDaughterCurvature(m, a1, a2, a3));
    }
    
    /**
     * Get the daughter by mother, three ancestors and curvature of the daugter.
     * Complex variant of the equation used in getDaughterCurvature.
     * @param m Mother.
     * @param a1 First ancestor.
     * @param a2 Second acnestor.
     * @param a3 Third ancestor.
     * @param k Curvature of the daughter.
     * @return The daughter.
     */
    public static SoddyCircle getDaughter(SoddyCircle m, SoddyCircle a1,
            SoddyCircle a2, SoddyCircle a3, double k){
        
        /**
         * The center of the daughter.
         */
        Complex z =  m.z().multiply(m.k)
                .add(a2.z().multiply(a2.k))
                .add(a3.z().multiply(a3.k))
                .multiply(2)
                .subtract(a1.z().multiply(a1.k))
                .divide(new Complex(k, 0));
        
        return new SoddyCircle(z, k, m.l+1);
    }
}
