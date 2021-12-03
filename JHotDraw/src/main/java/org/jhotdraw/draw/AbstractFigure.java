/*
 * @(#)AbstractFigure.java   6.0  2000-02-13
 *
 * Copyright (c) 1996-2008 by the original authors of JHotDraw
 * and all its contributors.
 * All rights reserved.
 *
 * The copyright of this software is owned by the authors and  
 * contributors of the JHotDraw project ("the copyright holders").  
 * You may not use, copy or modify this software, except in  
 * accordance with the license agreement you entered into with  
 * the copyright holders. For details see accompanying license terms. 
 */
package org.jhotdraw.draw;

import org.jhotdraw.beans.AbstractBean;
import org.jhotdraw.util.*;
import javax.swing.event.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.font.*;
import java.awt.geom.*;
import java.util.*;
import javax.swing.*;
import javax.swing.undo.*;
import java.io.*;
import org.jhotdraw.geom.*;

/**
 * AbstractFigure provides the functionality for managing listeners
 * for a Figure.
 *
 *
 * @author Werner Randelshofer
 * @version 7.0 2008-02-13 Huw Jones: Added methods to support
 * Figure.isTransformable().
 * <br>5.1 2007-12-19 Method invalidate only fires an areInvalidated
 * event, when the Figure is part of a Drawing. 
 * <br>5.0 2007-07-17 Extends from AbstractBean.
 * <br>4.0 2007-05-18 Removed addUndoableEditListener and
 * removeUndoableEditListener, isConnectorsVisible, setConnectorsVisible
 * methods due to changes in Figure interface.
 * <br>3.4 2007-02-09 Method fireFigureHandlesChanged added.
 * <br>3.3 Reworked.
 * <br>3.2 2006-01-05 Added method getChangingDepth().
 * <br>3.0 2006-01-20 Reworked for J2SE 1.5.
 * <br>1.0 2003-12-01 Derived from JHotDraw 5.4b1.
 */
public abstract class AbstractFigure
        extends AbstractNoConnectFigure {

    /** Creates a new instance. */
    public AbstractFigure() {
    }
    
    
    /**
     * Checks if this figure can be connected. By default
     * AbstractFigures can be connected.
     */
    public boolean canConnect() {
        return true;
    }
    /**
     * Returns the Figures connector for the specified location.
     * By default a ChopBoxConnector is returned.
     *
     *
     * @see ChopRectangleConnector
     */
    public Connector findConnector(Point2D.Double p, ConnectionFigure prototype) {
        return new ChopRectangleConnector(this);
    }


    public Connector findCompatibleConnector(Connector c, boolean isStart) {
        return new ChopRectangleConnector(this);
    }
    
    public Collection<Connector> getConnectors(ConnectionFigure prototype) {
        LinkedList<Connector> connectors = new LinkedList<Connector>();
        connectors.add(new ChopRectangleConnector(this));
        return connectors;
    }
    
    
    
}
