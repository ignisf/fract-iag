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
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.Collection;
import javax.swing.JPanel;
import math.geom2d.Point2D;
import math.geom2d.conic.Circle2D;
import math.geom2d.conic.Conic2D;
import math.geom2d.conic.Ellipse2D;
import math.geom2d.conic.Hyperbola2D;
import math.geom2d.curve.AbstractSmoothCurve2D;
import math.geom2d.line.Line2D;
import math.geom2d.line.LineSegment2D;
import math.geom2d.line.Ray2D;
import math.geom2d.line.StraightLine2D;
import org.ignisf.iag.IAGView;
import org.ignisf.iag.descartes.ApollonianSet;
import org.ignisf.iag.descartes.SoddyCircle;

/**
 * Custom JPanel displaying the gasket.
 * @author ignisf
 */
public class GasketJPanel extends JPanel {
    
    /**
     * The center of the first generator.
     */
    protected Point2D center1 = null;
    
    /**
     * The radius of the first generator.
     */
    protected double radius1 = Double.NaN;
    
    /**
     * The center of the second generator.
     */
    protected Point2D center2 = null;
    
    /**
     * The radius of the second generator.
     */
    protected double radius2 = Double.NaN;

    /**
     * The center of the third generator.
     */
    protected Point2D center3 = null;
    
    /**
     * The radius of the third generator.
     */
    protected double radius3 = Double.NaN;
    
    /**
     * The locus of the center of the third generator.
     */
    protected AbstractSmoothCurve2D center3Locus;
    
    /**
     * The three generators.
     */
    SoddyCircle2D[] generators = new SoddyCircle2D[3];
    
    /**
     * The coordinate system grid of the panel.
     */
    protected CoordinateSystem grid;
    
    public int level = 5;
    
    /**
     * The current mouse position.
     */
    protected Point2D mousePosition = new Point2D();
    
    /**
     * Indicator if all the data for drawing a gasket has been collected.
     */
    protected boolean needRepaint;
    public static final String PROP_NEEDREPAINT = "needRepaint";

    /**
     * Get the value of needRepaint
     *
     * @return the value of needRepaint
     */
    public boolean isNeedRepaint() {
        return needRepaint;
    }

    /**
     * Set the value of needRepaint
     *
     * @param needRepaint new value of needRepaint
     */
    public void setNeedRepaint(boolean needRepaint) {
        boolean oldNeedRepaint = this.needRepaint;
        this.needRepaint = needRepaint;
        propertyChangeSupport.firePropertyChange(PROP_NEEDREPAINT, oldNeedRepaint, needRepaint);
    }
    private PropertyChangeSupport propertyChangeSupport = new PropertyChangeSupport(this);

    /**
     * Add PropertyChangeListener.
     *
     * @param listener
     */
    @Override
    public void addPropertyChangeListener(PropertyChangeListener listener) {
        propertyChangeSupport.addPropertyChangeListener(listener);
    }

    /**
     * Remove PropertyChangeListener.
     *
     * @param listener
     */
    @Override
    public void removePropertyChangeListener(PropertyChangeListener listener) {
        propertyChangeSupport.removePropertyChangeListener(listener);
    }

    
    IAGView v;
    
    /**
     * Set the coordinete system grid of the panel.
     * @param g The grid.
     */
    public void setGrid(CoordinateSystem g){
        this.grid = g;
        //TODO call repaint.
    }
    
    public void shift(double x, double y){
        if(center3 != null) {
            center1.setLocation(center1.x + x, center1.y + y);
            if(generators[0]!=null) generators[0] = new SoddyCircle2D(center1, generators[0].curvature, generators[0].level);
            center2.setLocation(center2.x + x, center2.y + y);
            if(generators[1]!=null) generators[1] = new SoddyCircle2D(center2, generators[1].curvature, generators[1].level);
            center3.setLocation(center3.x + x, center3.y + y);
            if(generators[2]!=null) generators[2] = new SoddyCircle2D(center3, generators[2].curvature, generators[2].level);
        }
    }
    
    public void zoom(double by){
        double d1 = this.getWidth()/2d;
        double d2 = this.getHeight()/2d;
        this.center1.x = (this.center1.x-d1)*by+d1;
        this.center1.y = (this.center1.y-d2)*by+d2;
        this.center2.x = (this.center2.x-d1)*by+d1;
        this.center2.y = (this.center2.y-d2)*by+d2;
        this.center3.x = (this.center3.x-d1)*by+d1;
        this.center3.y = (this.center3.y-d2)*by+d2;
        radius1=radius1*by;
        radius2=radius2*by;
        radius3=radius3*by;
        if(generators[0]!=null) generators[0] = new SoddyCircle2D(center1, generators[0].curvature/by, generators[0].level);
        if(generators[1]!=null) generators[1] = new SoddyCircle2D(center2, generators[1].curvature/by, generators[1].level);
        if(generators[2]!=null) generators[2] = new SoddyCircle2D(center3, generators[2].curvature/by, generators[2].level);
    }
    
    public void reset(){
        generators = new SoddyCircle2D[3];
        center1 = center2 = center3 = null;
        radius1=radius2=radius3 = Double.NaN;
        addListeners();
    }
    
    /**
     * Get the grid of the panel.
     * @return The grid.
     */
    public CoordinateSystem getGrid(){
        return this.grid;
    }
    
    public GasketJPanel(IAGView v){
        this.addListeners();
        this.v=v;
    }
    
    /**
     * Paint method of the panel.
     * @param g Graphics context.
     */
    @Override
    public void paintComponent(java.awt.Graphics g) {
        /**
         * Clearing our pane before we start.
         */
        this.clear(g);
        this.setBackground(Color.white);
        
        /**
         * Casting the Graphics context.
         */
        Graphics2D g2d = (Graphics2D) g;
        
        
        // for antialising geometric shapes
        g2d.addRenderingHints(new
                RenderingHints(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON ));
        
        // for antialiasing text
        g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
                            RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        
        if (center3 == null) new Circle2D(this.mousePosition, 5).draw(g2d);
        
        /**
         * Drawing the generators.
         */
        if (center1 != null) {
            new GeneratorCenter(center1, g2d).draw();
            if (Double.isNaN(radius1)){
                new GeneratorMockup(center1,
                        center1.distance(this.mousePosition), g2d).draw();
            } else {
                new Generator(center1, radius1, g2d).draw();
                if (center2 != null) {
                    new Generator(center2, radius2, g2d).draw();
                    new GeneratorCenter(center2, g2d).draw();
                    if (center3 != null){
                        new Generator(center3, radius3, g2d).draw();
                        new GeneratorCenter(center3, g2d).draw();
                        
                        g2d.setColor(new Color(0, 0, 0, 255));
                        g2d.setStroke(new BasicStroke(0.3f));
                        ApollonianSet aps = new ApollonianSet(
                                generators[0].getSoddyCircle(),
                                generators[1].getSoddyCircle(),
                                generators[2].getSoddyCircle(), this.level);
                        java.util.Iterator<SoddyCircle> sc = aps.getSet().iterator();
                        while(sc.hasNext()){
                            new SoddyCircle2D(sc.next()).draw(g2d);
                        }
                        
                    } else {
                        new GeneratorMockup(mousePosition,
                                Math.abs(mousePosition.distance(center2)
                                - radius2), g2d).draw();
                    }
                } else {
                    new GeneratorMockup(mousePosition,
                            Math.abs(mousePosition.distance(center1)
                            - radius1), g2d).draw();
                }
            }
        }
    }
    
    public void clear(java.awt.Graphics g){
        super.paintComponent(g);
    }

    /**
     * Adding the listeners.
     */
    private void addListeners(){
        /*
         * Cleaning up.
         */
        MouseListener[] mls = this.getMouseListeners();
        for (MouseListener ml : mls){
            this.removeMouseListener(ml);
        }
        MouseMotionListener[] mmls = this.getMouseMotionListeners();
        for (MouseMotionListener mml : mmls){
            this.removeMouseMotionListener(mml);
        }
        
        /*
         * Adding the listeners. 
         */
        MouseMotionListener mml = new MousePositionListener();
        this.addMouseMotionListener(mml);
        
        if (center1 == null) {
            this.addMouseListener(new Generator1CenterListener());
        } else {
            if (Double.isNaN(radius1)){
                this.addMouseListener(new Generator1RadiusListener());
            } else {
                if (center2 == null && Double.isNaN(radius2)) {
                    this.addMouseListener(new Generator2Listener());
                } else {
                    if (center3 == null && Double.isNaN(radius3)) {
                        Generator3Listener gl = new Generator3Listener();
                        this.addMouseListener(gl);
                        this.removeMouseMotionListener(mml);
                        this.addMouseMotionListener(new Generator3Listener());
                    } else {
                        this.addMouseListener(new InteractionListener());
                        this.addMouseMotionListener(new InteractionListener());
                    }
                }
            }
        }
    }
    
    public Conic2D getC3Locus(Point2D c1, double r1, Point2D c2, double r2){
        return null;
    }
    
    /**
     * This listener constantly updates the mouse position.
     */
    public class MousePositionListener implements MouseMotionListener {

        public void mouseDragged(MouseEvent e) {}

        public void mouseMoved(MouseEvent e) {
            if (center3 == null && Double.isNaN(radius3)){
                mousePosition = new Point2D(e.getPoint());
                repaint();
            }
        }
        
    }
    
    public class GeneratorListener implements MouseListener {

        public void mouseClicked(MouseEvent e) {
        }

        public void mousePressed(MouseEvent e) {
        }

        public void mouseReleased(MouseEvent e) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        public void mouseEntered(MouseEvent e) {
            mousePosition = new Point2D(e.getPoint());
            repaint();
        }

        public void mouseExited(MouseEvent e) {
            mousePosition = new Point2D(e.getPoint());
            repaint();
        }
    }
    
    /**
     * Listener class used for the input of the first generator.
     */
    public class Generator1CenterListener extends GeneratorListener {
        @Override
        public void mouseReleased(MouseEvent e) {
            if (e.getButton() == MouseEvent.BUTTON1){
                center1 = new Point2D(e.getPoint());
                System.out.println("Set Center");
            }
            addListeners();
            repaint();
        }
    }
    
    private class Generator1RadiusListener extends GeneratorListener {
        @Override
        public void mouseReleased(MouseEvent e){
            if (e.getButton() == MouseEvent.BUTTON3){
                center1 = null;
                radius1 = Double.NaN;
                generators[0] = null;
                System.out.println("Reset Center");
            } else {
                radius1 = center1.distance(new Point2D(e.getPoint()));
                System.out.println("SetRadius");
            }
            addListeners();
            repaint();
        }
    }
    
    private class Generator2Listener extends GeneratorListener {
        @Override
        public void mouseReleased(MouseEvent e){
            if (e.getButton() == MouseEvent.BUTTON3){
                radius1 = Double.NaN;
                generators[0] = null;
                generators[1] = null;
            } else {
                center2 = new Point2D(e.getPoint());
                double rad = center2.distance(center1) - radius1;
                radius2 = Math.abs(rad);
                
                if (rad >= 0){
                    generators[0] = new SoddyCircle2D(center1, 1/radius1, -1);
                } else {
                    generators[0] = new SoddyCircle2D(center1, -1/radius1, -1);
                }

                /**
                 * Instantiating the second generator Soddy Circle
                 */
                generators[1] = new SoddyCircle2D(center2, 1/radius2, -1);

                /**
                 * Segment limited by the two centers.
                 */
                LineSegment2D l = new LineSegment2D(center1, center2);

                /**
                 * Center of the hyperbola and ellipse - midpoint of the
                 * segment limited by the two centers.
                 */
                Point2D lc = l.getIntersection(l.getMedian());

                /**
                 * A point of the hyperbola or ellipse (in this case,
                 * tangent point of the first two circles.
                 */
                Point2D p = generators[0].getIntersections(
                        new StraightLine2D(center2, center1)).iterator().next();
                
                //Calculating parabola/hyperbola parameters
                double a = lc.distance(p);
                double c = lc.distance(center1);

                if (center1.distance(center2) >= radius1){
                    /**
                     * Calculating the second parameter.
                     */
                    double b = Math.sqrt(c*c - a*a);
                    
                    /**
                     * The hyperbola itself.
                     */
                    Hyperbola2D hyper = new Hyperbola2D(lc, a, b,
                            l.getHorizontalAngle(), false);
                    
                    /*
                     * Choosing the relevant side of the hyperbola.
                     */
                    if (radius1 >= radius2){
                        center3Locus = hyper.getPositiveBranch();
                    } else {
                        center3Locus = hyper.getNegativeBranch();
                    }
                    
                } else {
                    /**
                     * Calculating the second parameter.
                     */
                    double b = Math.sqrt(a*a - c*c);
                    
                    /*
                     * Setting our elliptical locus.
                     */
                    center3Locus =
                            new Ellipse2D(lc, a, b, l.getHorizontalAngle());
                    
                }
            }
            addListeners();
            repaint();
        }
    }
    
    private class Generator3Listener extends
            GeneratorListener implements MouseMotionListener {
        @Override
        public void mouseReleased(MouseEvent e){
            if (e.getButton() == MouseEvent.BUTTON3){
                center2 = null;
                radius2 = Double.NaN;
                generators[1] = null;
            } else {
                center3 = mousePosition;
                radius3 = center3.distance(center2) - radius2;
                generators[2] = new SoddyCircle2D(center3, 1/radius3, -1);
            }
            addListeners();
            repaint();
        }

        public void mouseDragged(MouseEvent e) {
        }

        public void mouseMoved(MouseEvent e) {
            
            /* Check if we are working with a hyperbola or an ellipse */
            if(!center3Locus.isClosed()){
                /**
                 * The three vertices of the rectangular triangle and the heal
                 * of its height. Set by default to a point of our locus.
                 */
                Point2D A, B, C, H;
                A = B = C = H = center3Locus.getPoint(0d);

                StraightLine2D a =
                        new StraightLine2D(new Point2D(e.getPoint()), 0d);
                StraightLine2D b = 
                        new StraightLine2D(new Point2D(e.getPoint()),
                                Math.PI/2d);
                /**
                 * The intersection of the horizontal line passing below the
                 * pointer.
                 */
                Collection<Point2D> isecta = center3Locus.getIntersections(a);

                /**
                 * The intersection of the vertical line passing below the
                 * pointer.
                 */
                Collection<Point2D> isectb = center3Locus.getIntersections(b);

                /*
                 * Updating the three three vertices.
                 */
                if (isecta.size()>0) A = isecta.iterator().next();
                if (isectb.size()>0) B = isectb.iterator().next();
                C = a.getIntersection(b);

                /*
                 * Finding the hypotenusis.
                 */
                StraightLine2D c = new StraightLine2D(A, B);

                /*
                 * Finding the height.
                 */
                StraightLine2D h = c.getPerpendicular(C);

                /*
                 * Finding the intersection of the height with the locus.
                 */
                Collection<Point2D> isecth = center3Locus.getIntersections(h);
                if (isecth.size()>0) H = isecth.iterator().next();
                mousePosition = H;
            } else {
                /**
                 * The centralla of the first two generators.
                 */
                LineSegment2D centralla = new LineSegment2D(center1, center2);
                /**
                 * The midpoint of the centralla.
                 */
                Point2D a = centralla.getIntersection(centralla.getMedian());
                
                Ray2D r = new Ray2D(a, new Point2D(e.getPoint()));
                Collection<Point2D> isect = center3Locus.getIntersections(r);
                mousePosition = isect.iterator().next();
            }
            repaint();
        }
        @Override
        public void mouseEntered(MouseEvent e) {
        }

        @Override
        public void mouseExited(MouseEvent e) {
        }
    }
    
    private class InteractionListener extends
            GeneratorListener implements MouseMotionListener {
        Point2D start = new Point2D();
        Point2D end = new Point2D();
        int startLevel;
        
        public void mousePressed(MouseEvent e) {
            start = end = new Point2D(e.getPoint());
            startLevel=level;
            level=3;

        }

        public void mouseReleased(MouseEvent e) {
            //start = end = new Point2D();
            level = startLevel;
            repaint();
        }
        
        public void mouseDragged(MouseEvent e) {
            end = new Point2D(e.getPoint());
            GasketJPanel s= (GasketJPanel) e.getSource();
            double deltax = end.x - start.x;
            double deltay = end.y - start.y;
            
            shift(deltax, deltay);
            start = end;
            repaint();
        }

        public void mouseMoved(MouseEvent e) {
            start = end = new Point2D(e.getPoint());
        }
        
    }
    
    private class GeneratorCenter extends Circle2D {
        Graphics2D g2d;
        public GeneratorCenter(Point2D center, Graphics2D g2d){
            super(center, 3);
            this.g2d = g2d;
            g2d.setColor(new Color(255, 0, 0, 99));
            g2d.setStroke(new BasicStroke(1f));
        }
        
        public void draw(){
            super.fill(g2d);
            super.draw(g2d);
        }
    }
    
    private class GeneratorMockup extends Circle2D {
        Graphics2D g2d;
        public GeneratorMockup(Point2D c, double r, Graphics2D g2d){
            super(c, r);
            this.g2d = g2d;
            float [] Dashes = {0,8F};
            g2d.setColor(new Color(255, 0, 0, 99));
            g2d.setStroke(new BasicStroke(3f, BasicStroke.CAP_ROUND,
                    BasicStroke.JOIN_ROUND, 1f, Dashes, 50f));
        }
        
        public void draw(){
            super.draw(g2d);
        }
    }
    
    private class Generator extends Circle2D {
        Graphics2D g2d;
        public Generator(Point2D c, double r, Graphics2D g2d){
            super(c, r);
            this.g2d = g2d;
            g2d.setColor(new Color(0, 0, 0, 255));
            g2d.setStroke(new BasicStroke(1f));
        }
        
        public void draw(){
            super.draw(g2d);
        }
    }
}
