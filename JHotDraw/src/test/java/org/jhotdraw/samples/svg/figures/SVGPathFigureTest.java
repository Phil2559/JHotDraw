package org.jhotdraw.samples.svg.figures;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.geom.Rectangle2D;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author phili
 */
public class SVGPathFigureTest {
    @BeforeClass
    public static void setUpClass() {
    }
    
    @AfterClass
    public static void tearDownClass() {
    }
    
    @Before
    public void setUp() {
        
    }
    
    @After
    public void tearDown() {
    }

    @Test
    public void testCanDrawWithoutSetupGivenOpacityZero() {
        SVGPathFigure mockFigure = mock(SVGPathFigure.class);
         when(mockFigure.getOpacity()).thenReturn(0d);
         when(mockFigure.canDrawWithoutSetup()).thenCallRealMethod();
         assertTrue(mockFigure.canDrawWithoutSetup());
    }
    
    @Test
    public void testCanDrawWithoutSetupGivenOpacityOne() {
        SVGPathFigure mockFigure = mock(SVGPathFigure.class);
         when(mockFigure.getOpacity()).thenReturn(1d);
         when(mockFigure.canDrawWithoutSetup()).thenCallRealMethod();
         assertTrue(mockFigure.canDrawWithoutSetup());
    }
    
    @Test
    public void testCanDrawWithoutSetupGivenOpacityBetweenZeroAndOne() {
        SVGPathFigure mockFigure = mock(SVGPathFigure.class);
         when(mockFigure.getOpacity()).thenReturn(0.5d);
         when(mockFigure.canDrawWithoutSetup()).thenCallRealMethod();
         assertFalse(mockFigure.canDrawWithoutSetup());
    }
    
    @Test
    public void testLimitAreaByGraphicClipboundsGivenOverlap(){
        Graphics2D g = mock(Graphics2D.class);
        when(g.getClipBounds()).thenReturn(new Rectangle(0, 0, 10, 10));
        Rectangle2D.Double drawingArea = new Rectangle.Double(5,5, 10, 10);
        SVGPathFigure figure = new SVGPathFigure();
        Rectangle2D.Double expectedRectangle = new Rectangle.Double(5,5,5,5);
        assertEquals(figure.limitAreaByGraphicClipbounds(g, drawingArea), expectedRectangle);
    }
    @Test
    public void testLimitAreaByGraphicClipboundsGivenPointOverlap(){
        Graphics2D g = mock(Graphics2D.class);
        when(g.getClipBounds()).thenReturn(new Rectangle(0, 0, 5, 5));
        Rectangle2D.Double drawingArea = new Rectangle.Double(5,5, 5,5);
        SVGPathFigure figure = new SVGPathFigure();
        Rectangle2D.Double expectedRectangle = new Rectangle.Double(5,5,0,0);
        assertEquals(figure.limitAreaByGraphicClipbounds(g, drawingArea), expectedRectangle);
    }
    @Test
    public void testLimitAreaByGraphicClipboundsGivenNoClipbounds(){
        Graphics2D g = mock(Graphics2D.class);
        when(g.getClipBounds()).thenReturn(null);
        Rectangle2D.Double drawingArea = new Rectangle.Double(5,5, 5,5);
        SVGPathFigure figure = new SVGPathFigure();
        Rectangle2D.Double expectedRectangle = new Rectangle.Double(5,5,5,5);
        assertEquals(figure.limitAreaByGraphicClipbounds(g, drawingArea), expectedRectangle);
    }
//    @Test
//    public void testLimitAreaByGraphicClipboundsGivenNoOverlap(){
//        Graphics2D g = mock(Graphics2D.class);
//        when(g.getClipBounds()).thenReturn(new Rectangle(5,5, 5,5));
//        Rectangle2D.Double drawingArea = new Rectangle.Double(20,20, 5,5);
//        SVGPathFigure figure = new SVGPathFigure();
//        Rectangle2D.Double expectedDrawingArea = new Rectangle.Double(20,20, 5,5);
//        assertEquals(figure.limitAreaByGraphicClipbounds(g, drawingArea), expectedDrawingArea);
//    }
    
    
    
}
