/**
 * $Id$
 */

package org.ignisf.iag.descartes;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

/**
 * Apollonian set abstract data type.
 * @author Petko Bordjukov
 */
public class ApollonianSet {
    
    /**
     * The set itself.
     */
    ArrayList<SoddyCircle> set = new ArrayList<SoddyCircle>(3);
    
    /**
     * The maximum level of the set.
     */
    int n;
    
    /**
     * Create an apollonian set by three ancestors.
     * @param a1 First ancestor.
     * @param a2 Second ancestor.
     * @param a3 Third acnestor.
     * @param n The maximum level
     */
    public ApollonianSet(SoddyCircle a1, SoddyCircle a2, SoddyCircle a3,
            int n){
        
        this.n = n;
        
        /* Adding the three ancestors to the set. */
        set.add(a1);
        set.add(a2);
        set.add(a3);
        
        /* Setting up the initial descartes configuration */
        Collection<SoddyCircle> a45 = 
                DescartesTheorem.complexDescartes(a1, a2, a3);
        set.addAll(a45);
        
        /* Fill in the set */
        Iterator<SoddyCircle> i = a45.iterator();
        
        while (i.hasNext())
            this.set.addAll(this.getHeirs(i.next(), a1, a2, a3, n)); 
    }
    
    /**
     * A recursive method to fill the Apollonian set to the n-th level
     * @param m Mother
     * @param a1 First ancestor.
     * @param a2 Second ancestor.
     * @param a3 Third ancestor.
     * @param n Maximum level.
     */
    private Collection<SoddyCircle> getHeirs(SoddyCircle m,
            SoddyCircle a1, SoddyCircle a2, SoddyCircle a3, int n){
        
        /**
         * The heirs of the 4 ancestors up to the n-th level.
         */
        ArrayList<SoddyCircle> heirs = new ArrayList<SoddyCircle>(3);
        
        if (n>=1){
            /**
             * The daughters.
             */
            SoddyCircle[] d = new SoddyCircle[3];
            d[0] = DescartesTheorem.getDaughter(m, a1, a2, a3);
            d[1] = DescartesTheorem.getDaughter(m, a3, a1, a2);
            d[2] = DescartesTheorem.getDaughter(m, a2, a3, a1);

            /* Adding the daughters to the set */
            heirs.add(d[0]);
            heirs.add(d[1]);
            heirs.add(d[2]);

            /* Recuring if we are not at the maximum level */
            if(m.l+1 < n){
                heirs.addAll(this.getHeirs(d[0], m, a2, a3, n));
                heirs.addAll(this.getHeirs(d[1], m, a1, a2, n));
                heirs.addAll(this.getHeirs(d[2], m, a3, a1, n));
            }
        }
        return heirs;
    }
    
    /**
     * Get the complete set.
     * @return The set.
     */
    public Collection<SoddyCircle> getSet(){
        return set;
    }
}
