/**
 * This file contains the panel in which the gasket is being generated.
 */
package org.ignisf.iag.frontend;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.util.Collection;
import javax.swing.JPanel;
import math.geom2d.*;
import math.geom2d.conic.*;
import math.geom2d.line.*;
import org.ignisf.iag.descartes.ApollonianSet;
import org.ignisf.iag.descartes.SoddyCircle;
import org.ignisf.iag.primitives.SoddyCircle2D;


/**
 * Custom JPanel displaying the gasket.
 * @author ignisf
 */
public class GasketJPanel extends JPanel implements MouseListener,
        MouseMotionListener, MouseWheelListener {
    
    /**
     * The centers of the three generators.
     */
    Point2D[] generatorCenters = new Point2D[3];
    
    /**
     * The radii of the three generators.
     */
    double[] generatorRadii = new double[3];
    
    /**
     * Number of chosen centers.
     */
    short chosenPoints = 0;
    
    /**
     * Radius 1 indicator.
     */
    boolean radius1Chosen = false;
    
    /**
     * Current mouse position.
     */
    Point2D mousePosition = new Point2D();
    
    /**
     * The position of the center (on the locus);
     */
    Point2D centerPosition = new Point2D();
    
    /**
     * The circular marker of the centers.
     */
    Circle2D centerMarker;
    
    /**
     * Clock locker to avoid multiclicking during repaint.
     */
    boolean clicklock = false;

    /**
     * The locus of possible points to choose.
     */
    HyperbolaBranch2D locus;

    public GasketJPanel(){
        for (int i = 0; i < this.generatorCenters.length; i++){
            this.generatorCenters[i] = new Point2D(0, 0);
            this.generatorRadii[i] = 0;
        }
    }
    
    
    @Override
    public void paintComponent(java.awt.Graphics g) {
        
        /*
         * Adding mouse listeners.
         */
        this.addMouseListener(this);
        this.addMouseMotionListener(this);
        this.addMouseWheelListener(this);
        /**
         * Clearing our pane before we start.
         */
        this.clear(g);
        
        /**
         * Casting the Graphics context.
         */
        Graphics2D g2d = (Graphics2D) g;
        // for antialising geometric shapes
        g2d.addRenderingHints( new RenderingHints( RenderingHints.KEY_ANTIALIASING,
                                                 RenderingHints.VALUE_ANTIALIAS_ON ));
        // for antialiasing text
        g2d.setRenderingHint( RenderingHints.KEY_TEXT_ANTIALIASING,
                            RenderingHints.VALUE_TEXT_ANTIALIAS_ON );
        
        /**
         * The generator circles themselves.
         */
        SoddyCircle2D[] generators = new SoddyCircle2D[chosenPoints];
        
        
        /**
         * The three generator centers
         */
        for (int i = 0; i < this.chosenPoints; i++){
            g2d.setColor(Color.RED);
            new Circle2D(this.generatorCenters[i], 3).draw(g2d);
            if(this.radius1Chosen){
                generators[i] = new SoddyCircle2D(this.generatorCenters[i], 1d/this.generatorRadii[i], 0);
                g2d.setColor(Color.BLACK);
                generators[i].draw(g2d);
            }
        }
        
        // Drawing the center marker.
        if (chosenPoints < 3){
            /**
             * The current mouse position marker.
             */
            Circle2D current = new Circle2D(this.generatorCenters[chosenPoints], 3);
            g2d.setColor(Color.ORANGE);
            current.fill(g2d);
        }
        
        float [] Dashes = {0,8F};
        
        g2d.setColor(new Color(255, 0, 0, 99));
        g2d.setStroke(new BasicStroke(3f, BasicStroke.CAP_ROUND,  BasicStroke.JOIN_ROUND, 1f, Dashes, 50f));
        
        switch (chosenPoints){
            case 1:
                if (!this.radius1Chosen){
                    new Line2D(this.generatorCenters[0], this.mousePosition).draw(g2d);
                    new Circle2D(this.generatorCenters[0], this.generatorRadii[0]).draw(g2d);
                } else {
                    Collection<Point2D> is = generators[0].getIntersections(new StraightLine2D(this.mousePosition, this.generatorCenters[0]));
                    new Line2D(is.iterator().next(), this.mousePosition).draw(g2d);
                    new Circle2D(this.generatorCenters[1], this.generatorRadii[1]).draw(g2d);
                }
                break;
            case 2:
                /**
                 * Segment limited by the two centers.
                 */
                LineSegment2D l = new LineSegment2D(this.generatorCenters[0], this.generatorCenters[1]);

                /**
                 * Center of the hyperbola and ellipse - midpoint of the segment limited by the two centers.
                 */
                Point2D hc = l.getIntersection(l.getMedian());

                /**
                 * A point of the hyperbola or ellipse (in this case, tangent point of the first two circles.
                 */
                Point2D p = generators[0].getIntersections(new StraightLine2D(this.generatorCenters[1], this.generatorCenters[0])).iterator().next();

                //Calculating parabola parameters
                double a = hc.distance(p);
                double c = hc.distance(generatorCenters[0]);
                
                
                if (generatorCenters[0].distance(generatorCenters[1]) >= generatorRadii[0]){
                    
                    /**
                     * Calculating the second parameter.
                     */
                    double b = Math.sqrt(c*c - a*a);
                    /**
                     * The hyperbola itself.
                     */
                    Hyperbola2D hyper = new Hyperbola2D(hc, a, b, l.getHorizontalAngle(), false);

                    /*
                     * Choosing the relevant side of the hyperbola.
                     */
                    if (generatorRadii[0] >= generatorRadii[1]){
                        this.locus = hyper.getPositiveBranch();
                    } else {
                        this.locus = hyper.getNegativeBranch();
                    }

                    // Calculating the closest point of the hyperbola to the current mouse position.
                    Point2D closestPoint = new Point2D(Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY);



                    Collection<Point2D> intersectionsCollection = locus.getIntersections(new Line2D(0,0,this.getWidth(),0));
                    intersectionsCollection.addAll(locus.getIntersections(new Line2D(this.getWidth(),0,this.getWidth(),this.getHeight())));
                    intersectionsCollection.addAll(locus.getIntersections(new Line2D(0,this.getHeight(),this.getWidth(),this.getHeight())));
                    intersectionsCollection.addAll(locus.getIntersections(new Line2D(0,0,0,this.getHeight())));
                    intersectionsCollection.addAll(locus.getIntersections(new Line2D(this.mousePosition.x, 0, this.mousePosition.x, this.getWidth())));
                    intersectionsCollection.addAll(locus.getIntersections(new Line2D(0, this.mousePosition.y, this.getHeight(), this.mousePosition.y)));    

                    java.util.Iterator<Point2D> intersections = intersectionsCollection.iterator();
                    while (intersections.hasNext()){
                        Point2D pt = intersections.next();
                        //new Circle2D(pt, 3).draw(g2d);
                        if (mousePosition.distance(pt) < mousePosition.distance(closestPoint)){
                            closestPoint = pt;
                        }
                    }

                    generatorCenters[2]=closestPoint;
                    generatorRadii[2]=Math.abs(closestPoint.distance(generatorCenters[0]) - generatorRadii[0]); 
                    new Circle2D(this.generatorCenters[2], this.generatorRadii[2]).draw(g2d);
                    
                    // Drawing the locus.
                    g2d.setColor(new Color(255,204,65,200));
                    g2d.setStroke(new BasicStroke(2));
                    
                    new Line2D(this.generatorCenters[2], this.generatorCenters[1]).draw(g2d);
                    new Line2D(this.generatorCenters[2], this.generatorCenters[0]).draw(g2d);
                    
                    double angle = Math.abs(l.getHorizontalAngle());
                    if ( angle < Math.PI/4d || angle > 3d*Math.PI/4d){
                        int points = this.getHeight();
                        for (int i = 0; i < points; i = i + 10){
                            Line2D l1 = new Line2D(0, i, this.getWidth(), i);
                            java.util.Iterator<Point2D> i1 = locus.getIntersections(l1).iterator();

                            Line2D l2 = new Line2D(0, i+5, this.getWidth(), i+5);
                            java.util.Iterator<Point2D> i2 = locus.getIntersections(l2).iterator();

                            for (int j = 0; j < 2; j++){
                                if (i1.hasNext() && i2.hasNext()){
                                    new Line2D(i1.next(), i2.next()).draw(g2d);
                                }
                            }
                        }
                    } else {
                        int points = this.getWidth();
                        for (int i = 0; i < points; i = i + 10){
                            Line2D l1 = new Line2D(i, 0, i, this.getHeight());
                            java.util.Iterator<Point2D> i1 = locus.getIntersections(l1).iterator();

                            Line2D l2 = new Line2D(i+5, 0, i+5, this.getHeight());
                            java.util.Iterator<Point2D> i2 = locus.getIntersections(l2).iterator();

                            for (int j = 0; j < 2; j++){
                                if (i1.hasNext() && i2.hasNext()){
                                    new Line2D(i1.next(), i2.next()).draw(g2d);
                                }
                            }
                        }
                    }
                } else {
                    /**
                     * Calculating the second parameter.
                     */
                    double b = Math.sqrt(a*a - c*c);
                    Ellipse2D ellipse = new Ellipse2D(hc, a, b, l.getHorizontalAngle());
                    
                    StraightLine2D intersector = new StraightLine2D(mousePosition, hc);
                    generatorCenters[2]=ellipse.getIntersections(intersector).iterator().next();
                    
                    generatorRadii[2]=Math.abs(generatorCenters[2].distance(generatorCenters[0]) - generatorRadii[0]); 
                    new Circle2D(this.generatorCenters[2], this.generatorRadii[2]).draw(g2d);

                    
                    g2d.setColor(new Color(255, 204, 65, 200));
                    g2d.setStroke(new BasicStroke(3f, BasicStroke.CAP_ROUND,  BasicStroke.JOIN_ROUND, 1f, Dashes, 50f));
                    new Line2D(this.generatorCenters[2], this.generatorCenters[1]).draw(g2d);
                    new Line2D(this.generatorCenters[2], this.generatorCenters[0]).draw(g2d);
                    ellipse.draw(g2d);
                }
                break;
            case 3 :
                g2d.setColor(new Color(0, 0, 0, 255));
                g2d.setStroke(new BasicStroke(0.3f));
                ApollonianSet aps = new ApollonianSet(
                        new SoddyCircle(generators[0].getCenter().x, generators[0].getCenter().y,generators[0].getCurvature(), 0),
                        new SoddyCircle(generators[1].getCenter().x, generators[1].getCenter().y,generators[1].getCurvature(), 0),
                        new SoddyCircle(generators[2].getCenter().x, generators[2].getCenter().y,generators[2].getCurvature(), 0), 10);
                //Collection<SoddyCircle2D> soddyCircles = DescartesTheorem.getKissingCircles(generators[0], generators[1], generators[2], 5);
                java.util.Iterator<SoddyCircle> sc = aps.getSet().iterator();
                while(sc.hasNext()){
                    new SoddyCircle2D(sc.next()).draw(g2d);
                }
                
                break;
                
        }
    }
    
    public void clear(java.awt.Graphics g){
        super.paintComponent(g);
    }

    public void mouseClicked(MouseEvent e) {
    }

    public void mousePressed(MouseEvent e) {
        //System.out.println("Mouse pressed");
    }

    public void mouseReleased(MouseEvent e) {
        if (e.getButton() == 1) {
            if (!this.clicklock && this.chosenPoints < 3){
                if(this.chosenPoints == 1 && !this.radius1Chosen){
                    this.radius1Chosen = true;
                } else {
                    this.chosenPoints++;
                }
                this.clicklock = true;
                this.repaint();
            }
        } else {
            this.chosenPoints = 0;
            this.radius1Chosen = false;
            for (int i = 0; i < this.generatorCenters.length; i++){
                this.generatorCenters[i] = new Point2D(0, 0);
                this.generatorRadii[i] = 0;
            }
            this.repaint();
            this.clicklock = true;
        }
    }

    public void mouseEntered(MouseEvent e) {
    }

    public void mouseExited(MouseEvent e) {
    }

    public void mouseWheelMoved(MouseWheelEvent e) {
        System.out.println("Wheel moved");
    }

    public void mouseDragged(MouseEvent e) {
        mouseMoved(e);
    }

    public void mouseMoved(MouseEvent e) {
        if (this.chosenPoints < 3) {
            
            int x = e.getX(), y = e.getY();
            
            this.mousePosition.setLocation(x, y);
            
            switch(this.chosenPoints){
                case 0:
                    this.generatorCenters[0] = new Point2D(x, y);
                    break;
                case 1:
                    if (!this.radius1Chosen) {
                        this.generatorRadii[0] = generatorCenters[0].distance(x, y);
                    } else {
                        this.generatorRadii[1] = Math.abs(generatorCenters[0].distance(x, y) - this.generatorRadii[0]);
                        this.generatorCenters[1] = new Point2D(x, y);
                    }
                    break;
                case 2:
                    this.generatorRadii[2] = generatorCenters[0].distance(x, y) - this.generatorRadii[0];
                    break;
            }
            
            this.repaint();
            this.clicklock = false;
        }
    }
}
