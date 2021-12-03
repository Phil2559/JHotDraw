/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jhotdraw.samples.svg.figures;

import dk.sdu.mmmi.featuretracer.lib.FeatureEntryPoint;
import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.undo.AbstractUndoableEdit;
import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;
import javax.swing.undo.UndoableEdit;
import org.jhotdraw.app.JHotDrawFeatures;
import org.jhotdraw.draw.AttributeKeys;
import static org.jhotdraw.draw.AttributeKeys.CLOSED;
import static org.jhotdraw.draw.AttributeKeys.TRANSFORM;
import static org.jhotdraw.draw.AttributeKeys.WINDING_RULE;
import org.jhotdraw.draw.Figure;
import org.jhotdraw.util.ResourceBundleUtil;

/**
 *
 * @author phili
 */
public class ActionFactory {
    
    private SVGPathFigure affected;
    private static ActionFactory instance;
    
    
    private ActionFactory(){
    }
    
    public static ActionFactory getInstance(){
        if(instance == null){
            instance = new ActionFactory();
        }
        return instance;
    }
    
    public Action createClosePathAction(ResourceBundleUtil labels, SVGPathFigure affected){
        return new AbstractAction(labels.getString("attribute.closePath.text")) {
                @FeatureEntryPoint(JHotDrawFeatures.LINE_TOOL)
                public void actionPerformed(ActionEvent evt) {
                    affected.willChange();
                    for (Figure child : affected.getChildren()) {
                        affected.getDrawing().fireUndoableEditHappened(
                                CLOSED.setUndoable(child, true));
                    }
                    affected.changed();
                }
            };
    }
    
    public Action createFlattenTransformAction(ResourceBundleUtil labels, SVGPathFigure affected){
        return new AbstractAction(labels.getString("edit.flattenTransform.text")) {
            public void actionPerformed(ActionEvent evt) {
                // CompositeEdit edit = new CompositeEdit(labels.getString("flattenTransform"));
                //TransformEdit edit = new TransformEdit(SVGPathFigure.this, )
                final Object restoreData = affected.getTransformRestoreData();
                UndoableEdit edit = new AbstractUndoableEdit() {

                    @Override
                    public String getPresentationName() {
                        return labels.getString("edit.flattenTransform.text");
                    }

                    @Override
                    public void undo() throws CannotUndoException {
                        super.undo();
                        affected.willChange();
                        affected.restoreTransformTo(restoreData);
                        affected.changed();
                    }

                    @Override
                    public void redo() throws CannotRedoException {
                        super.redo();
                        affected.willChange();
                        affected.restoreTransformTo(restoreData);
                        affected.flattenTransform();
                        affected.changed();
                    }
                };
                affected.willChange();
                affected.flattenTransform();
                affected.changed();
                affected.fireUndoableEditHappened(edit);
            }
        };
    }

    public Action createOpenPathAction(ResourceBundleUtil labels, SVGPathFigure affected){
        return new AbstractAction(labels.getString("attribute.openPath.text")) {

            public void actionPerformed(ActionEvent evt) {
                affected.willChange();
                for (Figure child : affected.getChildren()) {
                    affected.getDrawing().fireUndoableEditHappened(
                            CLOSED.setUndoable(child, false));
                }
                affected.changed();
            }
        };
    }
    
    public Action createWindingRuleEvenOddAction(ResourceBundleUtil labels, SVGPathFigure affected){
        return new AbstractAction(labels.getString("attribute.windingRule.evenOdd.text")) {
                @FeatureEntryPoint(JHotDrawFeatures.LINE_TOOL)
                public void actionPerformed(ActionEvent evt) {
                    affected.willChange();
                    affected.getDrawing().fireUndoableEditHappened(
                            WINDING_RULE.setUndoable(affected, AttributeKeys.WindingRule.EVEN_ODD));
                    affected.changed();
                }
            };
    }
        
    public Action createWindingRuleNonZeroAction(ResourceBundleUtil labels, SVGPathFigure affected){
        return new AbstractAction(labels.getString("attribute.windingRule.nonZero.text")) {
                public void actionPerformed(ActionEvent evt) {
                    WINDING_RULE.set(affected, AttributeKeys.WindingRule.NON_ZERO);
                    affected.getDrawing().fireUndoableEditHappened(
                            WINDING_RULE.setUndoable(affected, AttributeKeys.WindingRule.NON_ZERO));
                }
            };
    }
    
    public Action createRemoveTransformAction(ResourceBundleUtil labels,SVGPathFigure affected){
        return new AbstractAction(labels.getString("edit.removeTransform.text")) {

                public void actionPerformed(ActionEvent evt) {
                    ResourceBundleUtil labels = ResourceBundleUtil.getBundle("org.jhotdraw.samples.svg.Labels");
                    affected.willChange();
                    affected.fireUndoableEditHappened(
                            TRANSFORM.setUndoable(affected, null));
                    affected.changed();
                }
            };
    }
    
}
