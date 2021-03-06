/*
 * @(#)SVGPathFigure.java  1.2  2009-04-17
 *
 * Copyright (c) 1996-2009 by the original authors of JHotDraw
 * and all its contributors.
 * All rights reserved.
 *
 * The copyright of this software is owned by the authors and  
 * contributors of the JHotDraw project ("the copyright holders").  
 * You may not use, copy or modify this software, except in  
 * accordance with the license agreement you entered into with  
 * the copyright holders. For details see accompanying license terms. 
 */
package org.jhotdraw.samples.svg.figures;

import dk.sdu.mmmi.featuretracer.lib.FeatureEntryPoint;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.*;
import javax.swing.*;
import javax.swing.undo.*;
import org.jhotdraw.app.JHotDrawFeatures;
import org.jhotdraw.draw.*;
import org.jhotdraw.geom.*;
import org.jhotdraw.samples.svg.*;
import org.jhotdraw.util.*;
import org.jhotdraw.xml.*;
import static org.jhotdraw.samples.svg.SVGAttributeKeys.*;

/**
 * SVGPath is a composite Figure which contains one or more
 * SVGBezierFigures as its children.
 *
 * @author Werner Randelshofer
 * @version 2.1 2009-04-17 Method contains() takes now into account
 * whether the figure is filled.
 * <br>1.1.1 2008-03-20 Attributes must be set on child figures in order
 * to ensure that the drawing area of the child figures is computed properly. 
 * <br>1.1 2007-12-21 Only close/open last path. 
 * <br>1.0 July 8, 2006 Created.
 */
public class SVGPathFigure extends AbstractAttributedCompositeFigure implements SVGFigure {

    /**
     * This cachedPath is used for drawing.
     */
    private transient GeneralPath cachedPath;
   // private transient Rectangle2D.Double cachedDrawingArea;
    /**
     * This is used to perform faster hit testing.
     */
    private transient Shape cachedHitShape;
    private final static boolean DEBUG = false;

    /** Creates a new instance. */
    @FeatureEntryPoint(JHotDrawFeatures.LINE_TOOL)
    public SVGPathFigure() {
        System.out.println("new svg path figure");
        add(new SVGBezierFigure());
        SVGAttributeKeys.setDefaults(this);
    }
    @FeatureEntryPoint(JHotDrawFeatures.LINE_TOOL)
    public SVGPathFigure(boolean isEmpty) {
        System.out.println("new svg path figure");
        if (! isEmpty) { add(new SVGBezierFigure()); }
        SVGAttributeKeys.setDefaults(this);
    }
    
    public double getOpacity(){
        return Math.min(Math.max(0d, OPACITY.get(this)), 1d);
    }

    public boolean canDrawWithoutSetup(){
        if (getOpacity() == 0.0d || getOpacity()== 1.0d) {
            return true; 
        } else {
            return false;
        }
        
    }
    
    public Rectangle2D.Double limitAreaByGraphicClipbounds(Graphics2D g, Rectangle2D.Double drawingArea){
        Rectangle2D clipBounds = g.getClipBounds();
        if (clipBounds != null) {
            Rectangle2D.intersect(drawingArea, clipBounds, drawingArea);
        }
        return drawingArea;
    }
    
    private Graphics2D configureGraphics(Graphics2D g, BufferedImage buf, Rectangle2D.Double drawingArea  ){
            Graphics2D gr = buf.createGraphics();
            gr.scale(g.getTransform().getScaleX(), g.getTransform().getScaleY());
            gr.translate((int) -drawingArea.x, (int) -drawingArea.y);
            gr.setRenderingHints(g.getRenderingHints());
            return gr;
    }
    
    private void updateDrawingArea(Graphics2D g, Rectangle2D.Double drawingArea, BufferedImage buf){
        Composite savedComposite = g.getComposite();
            g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, (float) getOpacity()));
            g.drawImage(buf, (int) drawingArea.x, (int) drawingArea.y,
                    2 + (int) drawingArea.width, 2 + (int) drawingArea.height, null);
            g.setComposite(savedComposite);
    }
    
    @FeatureEntryPoint(JHotDrawFeatures.LINE_TOOL)
    public void draw(Graphics2D g) {
        if(canDrawWithoutSetup()){drawFigure(g); return;}
        Rectangle2D.Double drawingArea = limitAreaByGraphicClipbounds(g,getDrawingArea());
        
        if (!drawingArea.isEmpty()) { 
            BufferedImage buf = createBufferedImage(drawingArea, g);
            Graphics2D gr = configureGraphics(g, buf, drawingArea);
            drawFigure(gr);
            gr.dispose();
            updateDrawingArea(g, drawingArea, buf);   
        }
    }
    
    private BufferedImage createBufferedImage(Rectangle2D.Double drawingArea,Graphics2D g){
        return new BufferedImage(
                    Math.max(1, (int) ((2 + drawingArea.width) * g.getTransform().getScaleX())),
                    Math.max(1, (int) ((2 + drawingArea.height) * g.getTransform().getScaleY())),
                    BufferedImage.TYPE_INT_ARGB);
    }

    public void drawFigure(Graphics2D g) {
        AffineTransform savedTransform = null;
        if (TRANSFORM.get(this) != null) {
            savedTransform = g.getTransform();
            g.transform(TRANSFORM.get(this));
        }
        Paint paint = SVGAttributeKeys.getFillPaint(this);
        if (paint != null) {
            g.setPaint(paint);
            drawFill(g);
        }
        paint = SVGAttributeKeys.getStrokePaint(this);
        if (paint != null) {
            g.setPaint(paint);
            g.setStroke(SVGAttributeKeys.getStroke(this));
            drawStroke(g);
        }
        if (TRANSFORM.get(this) != null) {
            g.setTransform(savedTransform);
        }
    }

    protected void drawChildren(Graphics2D g) {
    // empty
    }

    public void drawFill(Graphics2D g) {
        g.fill(getPath());
    }

    public void drawStroke(Graphics2D g) {
        g.draw(getPath());
    }

    @Override protected void invalidate() {
        super.invalidate();
        cachedPath = null;
        cachedDrawingArea = null;
        cachedHitShape = null;
    }

    protected GeneralPath getPath() {
        if (cachedPath == null) {
            cachedPath = new GeneralPath();
            cachedPath.setWindingRule(WINDING_RULE.get(this) == WindingRule.EVEN_ODD ? GeneralPath.WIND_EVEN_ODD : GeneralPath.WIND_NON_ZERO);
            for (Figure child : getChildren()) {
                SVGBezierFigure b = (SVGBezierFigure) child;
                cachedPath.append(b.getBezierPath(), false);
            }
        }
        return cachedPath;
    }
    protected Shape getHitShape() {
        if (cachedHitShape == null) {
            cachedHitShape = getPath();
            if (FILL_COLOR.get(this) == null && FILL_GRADIENT.get(this) == null) {
                cachedHitShape = SVGAttributeKeys.getHitStroke(this).createStrokedShape(cachedHitShape);
                }

        }
        return cachedHitShape;
    }

    
    // int count;
    public Rectangle2D.Double getDrawingArea() {
        if (cachedDrawingArea == null) {
            double width = getRectangleWidth(AttributeKeys.getStrokeTotalWidth(this));
            GeneralPath gp = (GeneralPath) getPath();
            Rectangle2D strokeRect = new Rectangle2D.Double(0, 0, width, width);
            if (TRANSFORM.get(this) != null) {
                gp = (GeneralPath) gp.clone();
                gp.transform(TRANSFORM.get(this));
                strokeRect = TRANSFORM.get(this).createTransformedShape(strokeRect).getBounds2D();
            }
            Rectangle2D rx = gp.getBounds2D();
            Rectangle2D.Double r = (rx instanceof Rectangle2D.Double) ? (Rectangle2D.Double) rx : new Rectangle2D.Double(rx.getX(), rx.getY(), rx.getWidth(), rx.getHeight());
            Geom.grow(r, strokeRect.getWidth(), strokeRect.getHeight());
            cachedDrawingArea = r;
        }
        return (Rectangle2D.Double) cachedDrawingArea.clone();
    }
    
    private double getRectangleWidth(double strokeTotalWidth){
        double width = strokeTotalWidth / 2d;
        if (STROKE_JOIN.get(this) == BasicStroke.JOIN_MITER) {
                width *= STROKE_MITER_LIMIT.get(this);
            } else if (STROKE_CAP.get(this) != BasicStroke.CAP_BUTT) {
                width += strokeTotalWidth * 2;
            }
        return width; 
    }

    @Override
    final public void write(DOMOutput out) throws IOException {
        throw new UnsupportedOperationException("Use SVGStorableOutput to write this Figure.");
    }

    @Override
    final public void read(DOMInput in) throws IOException {
        throw new UnsupportedOperationException("Use SVGStorableInput to read this Figure.");
    }

    public boolean contains(Point2D.Double p) {
        getPath();
        if (TRANSFORM.get(this) != null) {
            try {
                p = (Point2D.Double) TRANSFORM.get(this).inverseTransform(p, new Point2D.Double());
            } catch (NoninvertibleTransformException ex) {
                ex.printStackTrace();
            }
        }
        boolean isClosed = CLOSED.get(getChild(0));
        double tolerance = Math.max(2f, AttributeKeys.getStrokeTotalWidth(this) / 2d);
        
        boolean check1 = isClosed || FILL_COLOR.get(this) != null || FILL_GRADIENT.get(this)!=null;
        boolean check2 = new GrowStroke((float) AttributeKeys.getPerpendicularHitGrowth(this),
                    (float) (AttributeKeys.getStrokeTotalWidth(this) *
                    STROKE_MITER_LIMIT.get(this))).createStrokedShape(getPath()).contains(p);
        boolean check3 = isClosed && FILL_COLOR.get(this) == null && FILL_GRADIENT.get(this)==null;
        boolean check4 = check1 && (check2 ||getPath().contains(p));
        boolean check5 = getHitShape().contains(p) && check3;
        boolean check6 = !isClosed && Shapes.outlineContains(getPath(), p, tolerance);
        return check5 && check4 && !isClosed && check6;
            
    }

    public void setBounds(Point2D.Double anchor, Point2D.Double lead) {
        if (getChildCount() == 1 && ((SVGBezierFigure) getChild(0)).getNodeCount() <= 2) {
            SVGBezierFigure b = (SVGBezierFigure) getChild(0);
            b.setBounds(anchor, lead);
            invalidate();
        } else {
            super.setBounds(anchor, lead);
        }
    }

    public void transform(AffineTransform tx) {
        boolean check1 = TRANSFORM.get(this) != null || (tx.getType() & (AffineTransform.TYPE_TRANSLATION)) != tx.getType();
        if (check1 && TRANSFORM.get(this) == null) {
                TRANSFORM.basicSetClone(this, tx);}
        if (check1 && TRANSFORM.get(this) != null){
                AffineTransform t = TRANSFORM.getClone(this);
                t.preConcatenate(tx);
                TRANSFORM.basicSet(this, t);
        }
        if(!check1){
            for (Figure f : getChildren()) {
                f.transform(tx);
            }
            if (FILL_GRADIENT.get(this) != null &&
                    !FILL_GRADIENT.get(this).isRelativeToFigureBounds()) {
                Gradient g = FILL_GRADIENT.getClone(this);
                g.transform(tx);
                FILL_GRADIENT.basicSet(this, g);
            }
            if (STROKE_GRADIENT.get(this) != null &&
                    !STROKE_GRADIENT.get(this).isRelativeToFigureBounds()) {
                Gradient g = STROKE_GRADIENT.getClone(this);
                g.transform(tx);
                STROKE_GRADIENT.basicSet(this, g);
            }
        }
        invalidate();
    }

    @SuppressWarnings("unchecked")
    @Override
    public void restoreTransformTo(Object geometry) {
        invalidate();
        Object[] restoreData = (Object[]) geometry;
        ArrayList<Object> paths = (ArrayList<Object>) restoreData[0];
        for (int i = 0,  n = getChildCount(); i < n; i++) {
            getChild(i).restoreTransformTo(paths.get(i));
        }
        TRANSFORM.basicSetClone(this, (AffineTransform) restoreData[1]);
        FILL_GRADIENT.basicSetClone(this, (Gradient) restoreData[2]);
        STROKE_GRADIENT.basicSetClone(this, (Gradient) restoreData[3]);
    }

    @Override
    public Object getTransformRestoreData() {
        ArrayList<Object> paths = new ArrayList<Object>(getChildCount());
        for (int i = 0,  n = getChildCount(); i < n; i++) {
            paths.add(getChild(i).getTransformRestoreData());
        }
        return new Object[] {
            paths,
            TRANSFORM.getClone(this),
            FILL_GRADIENT.getClone(this),
            STROKE_GRADIENT.getClone(this)   
        };   
    }

    @Override
    public <T> void setAttribute(AttributeKey<T> key, T newValue) {
        super.setAttribute(key, newValue);
        invalidate();
    }
    
    public boolean isEmpty() {
        for (Figure child : getChildren()) {
            SVGBezierFigure b = (SVGBezierFigure) child;
            if (b.getNodeCount() > 0) {
                return false;
            }
        }
        return true;
    }

    @Override
    public Collection<Handle> createHandles(int detailLevel) {
        LinkedList<Handle> handles = new LinkedList<Handle>();
        switch (detailLevel % 2) {
            case -1 : // Mouse hover handles
                handles.add(new SVGPathOutlineHandle(this, true));
                break;
            case 0:
                handles.add(new SVGPathOutlineHandle(this));
                for (Figure child : getChildren()) {
                    handles.addAll(((SVGBezierFigure) child).createHandles(this, detailLevel));
                }
                handles.add(new LinkHandle(this));
                break;
            case 1:
                TransformHandleKit.addTransformHandles(this, handles);
                break;
            default:
                break;
        }
        return handles;
    }

    @Override
    public Collection<Action> getActions(Point2D.Double p) {
        final ResourceBundleUtil labels = ResourceBundleUtil.getBundle("org.jhotdraw.samples.svg.Labels");
        LinkedList<Action> actions = new LinkedList<Action>();
        ActionFactory af = ActionFactory.getInstance();
        if (TRANSFORM.get(this) != null) {
            actions.add(af.createRemoveTransformAction(labels, this));
            actions.add(af.createFlattenTransformAction(labels,this));
        }
        if (CLOSED.get(getChild(getChildCount() - 1))) {
            actions.add(af.createOpenPathAction(labels,this));
        } else {
            actions.add(af.createClosePathAction(labels,this));
        }
        if (WINDING_RULE.get(this) != WindingRule.EVEN_ODD) {
            actions.add(af.createWindingRuleEvenOddAction(labels,this));
        } else {
            actions.add(af.createWindingRuleNonZeroAction(labels,this));
        }
        return actions;
    }

    /**
     * Handles a mouse click.
     */
    @Override
    @FeatureEntryPoint(JHotDrawFeatures.LINE_TOOL)
    public boolean handleMouseClick(Point2D.Double p, MouseEvent evt, DrawingView view) {
        if (evt.getClickCount() == 2 && view.getHandleDetailLevel() % 2 == 0) {
            for (Figure child : getChildren()) {
                SVGBezierFigure bf = (SVGBezierFigure) child;
                int index = bf.findSegment(p, (float) (5f / view.getScaleFactor()));
                if (index != -1) {
                    bf.handleMouseClick(p, evt, view);
                    evt.consume();
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public void add(final int index, final Figure figure) {
        super.add(index, (SVGBezierFigure) figure);
    }

    @Override
    public SVGBezierFigure getChild(int index) {
        return (SVGBezierFigure) super.getChild(index);
    }

    public SVGPathFigure clone() {
        SVGPathFigure that = (SVGPathFigure) super.clone();
        return that;
    }

    public void flattenTransform() {
        willChange();
        AffineTransform tx = TRANSFORM.get(this);
        if (tx != null) {
            for (Figure child : getChildren()) {
                //((SVGBezierFigure) child).transform(tx);
                ((SVGBezierFigure) child).flattenTransform();
            }
        }
        if (FILL_GRADIENT.get(this) != null) {
            FILL_GRADIENT.get(this).transform(tx);
        }
        if (STROKE_GRADIENT.get(this) != null) {
            STROKE_GRADIENT.get(this).transform(tx);
        }
        TRANSFORM.basicSet(this, null);
        changed();
    }
}
