/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jhotdraw.draw.action.newpackage;

import com.tngtech.jgiven.annotation.BeforeStage;
import com.tngtech.jgiven.annotation.ProvidedScenarioState;
import org.jhotdraw.draw.DefaultDrawing;
import org.jhotdraw.draw.DefaultDrawingEditor;
import org.jhotdraw.draw.DefaultDrawingView;
import org.jhotdraw.draw.DrawingEditor;
import org.jhotdraw.draw.DrawingView;
import org.jhotdraw.draw.QuadTreeDrawing;
import org.jhotdraw.samples.svg.PathTool;
import org.jhotdraw.samples.svg.figures.SVGBezierFigure;
import org.jhotdraw.samples.svg.figures.SVGPathFigure;
import static org.junit.Assert.assertTrue;

/**
 *
 * @author phili
 */
public class GivenLine {
    @ProvidedScenarioState
    private DefaultDrawingEditor editor;
    
    @ProvidedScenarioState
    private PathTool pathTool;

    @BeforeStage
    private void before() {
        editor = new DefaultDrawingEditor();
        DefaultDrawingView view = new DefaultDrawingView();
        view.setDrawing(new DefaultDrawing());
        editor.add(view);
        editor.setActiveView(view);
    }
    
    GivenLine newDrawingEditor(){
        assertTrue(editor.getActiveView().getDrawing().getChildCount()== 0);
        return this;
    }
}
