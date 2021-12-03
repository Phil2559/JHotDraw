/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jhotdraw.draw.action.newpackage;

import com.tngtech.jgiven.annotation.BeforeStage;
import com.tngtech.jgiven.annotation.ExpectedScenarioState;
import com.tngtech.jgiven.annotation.ProvidedScenarioState;
import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;
import java.util.HashMap;
import org.jhotdraw.draw.AttributeKey;
import org.jhotdraw.draw.DefaultDrawingEditor;
import org.jhotdraw.draw.DrawingEditor;
import org.jhotdraw.samples.svg.PathTool;
import org.jhotdraw.samples.svg.figures.SVGBezierFigure;
import org.jhotdraw.samples.svg.figures.SVGPathFigure;
import static org.junit.Assert.assertTrue;
import org.mockito.Mockito;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.when;

/**
 *
 * @author phili
 */
public class ThenLine {
    
    @ExpectedScenarioState
    private DefaultDrawingEditor editor;
    
    ThenLine lineIsOnDrawing(){
        assertTrue(editor.getActiveView().getDrawing().getChild(0).getClass() == SVGPathFigure.class);
        return this;
    }
    
    
    ThenLine lineIsVisible(){
        assertTrue(editor.getActiveView().getDrawing().getChild(0).isVisible());
        return this;
    }
    
    ThenLine lineIsDrawnAsExpected(){
        assertTrue(editor.getActiveView().getDrawing().getChild(0).getDecomposition().size()==2);
        return this;
        
    }
    
    
    
    
    
    
}
