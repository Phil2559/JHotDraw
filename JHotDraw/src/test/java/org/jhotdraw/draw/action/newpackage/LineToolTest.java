/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jhotdraw.draw.action.newpackage;
import com.tngtech.jgiven.junit.ScenarioTest;
import java.awt.geom.Point2D;
import org.junit.Test;

/**
 *
 * @author phili
 */
public class LineToolTest extends ScenarioTest<GivenLine,WhenLine,ThenLine> {
    
    @Test
    public void testDrawLine(){
        given()
                .newDrawingEditor();
        when()
                .selectPathTool()
                .clickAndHoldMouse(10, 10)
                .moveMouseTo(30, 30)
                .releaseMouse(30, 30);
        then()
                .lineIsOnDrawing()
                .lineIsVisible()
                .lineIsDrawnAsExpected();
    }
}
