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
import org.jhotdraw.draw.CreationTool;
import org.jhotdraw.draw.DefaultDrawingEditor;
import org.jhotdraw.draw.DrawingEditor;
import org.jhotdraw.samples.svg.PathTool;
import org.jhotdraw.samples.svg.figures.SVGBezierFigure;
import org.jhotdraw.samples.svg.figures.SVGPathFigure;
import static org.junit.Assert.*;
import org.mockito.Mockito;
import static org.mockito.Mockito.when;

/**
 *
 * @author phili
 */
public class WhenLine {
    
    @ExpectedScenarioState
    @ProvidedScenarioState
    private DefaultDrawingEditor editor;

    @ExpectedScenarioState
    private PathTool pathTool;
    
    WhenLine selectPathTool(){
         editor.setTool(new CreationTool(new SVGPathFigure()));//, attributes);
        return this;
    }
    
    WhenLine clickAndHoldMouse(int x, int y) {
        MouseEvent startingMousePosition = Mockito.mock(MouseEvent.class);
        when(startingMousePosition.getSource()).thenReturn(editor.getActiveView());
        when(startingMousePosition.getX()).thenReturn(x);
        when(startingMousePosition.getY()).thenReturn(y);
        //System.out.println("bob: "+editor.getTool().toString());
        
        ((CreationTool)editor.getTool()).mousePressed(startingMousePosition);
        return this;
    }

    WhenLine moveMouseTo(int x, int y) {
        MouseEvent newMousePosition = Mockito.mock(MouseEvent.class);
        when(newMousePosition.getSource()).thenReturn(editor.getActiveView());
        when(newMousePosition.getX()).thenReturn(x);
        when(newMousePosition.getY()).thenReturn(y);
        editor.getTool().mouseDragged(newMousePosition);
        return this;
    }
    
    WhenLine releaseMouse(int x, int y){
        MouseEvent finalPosition = Mockito.mock(MouseEvent.class);
        when(finalPosition.getSource()).thenReturn(editor.getActiveView());
        when(finalPosition.getX()).thenReturn(x);
        when(finalPosition.getY()).thenReturn(y);
        editor.getTool().mouseReleased(finalPosition);
        return this;
    }

    Object and() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
}
