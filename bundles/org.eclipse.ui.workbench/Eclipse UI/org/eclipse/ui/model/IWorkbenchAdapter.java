/*******************************************************************************
 * Copyright (c) 2000, 2003 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.ui.model;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.graphics.RGB;

/**
 * This adapter interface provides visual presentation and hierarchical structure
 * for workbench elements, allowing them to be displayed in the UI
 * without having to know the concrete type of the element.
 * <p>
 * There is an associate label provider and content provider for showing
 * elements with a registered workbench adapter in JFace structured viewers.
 * </p>
 * @see WorkbenchLabelProvider
 * @see BaseWorkbenchContentProvider
 */
public interface IWorkbenchAdapter {
/**
 * Returns the children of this object.  When this object
 * is displayed in a tree, the returned objects will be this
 * element's children.  Returns an empty array if this
 * object has no children.
 *
 * @param object The object to get the children for.
 */
public Object[] getChildren(Object o);
/**
 * Returns an image descriptor to be used for displaying an object in the workbench.
 * Returns <code>null</code> if there is no appropriate image.
 *
 * @param object The object to get an image descriptor for.
 */
public ImageDescriptor getImageDescriptor(Object object);
/**
 * Returns the label text for this element.  This is typically
 * used to assign a label to this object when displayed
 * in the UI.  Returns an empty string if there is no appropriate
 * label text for this object.
 *
 * @param object The object to get a label for.
 */
public String getLabel(Object o);
/**
 * Returns the logical parent of the given object in its tree.
 * Returns <code>null</code> if there is no parent, or if this object doesn't
 * belong to a tree.
 *
 * @param object The object to get the parent for.
 */
public Object getParent(Object o);

/**
 * Provides a foreground color for the given element.
 * 
 * @param element the element
 * @return	the foreground color for the element, or <code>null</code> 
 *   to use the default foreground color
 * @since 3.0
 */
public RGB getForeground(Object element);

/**
 * Provides a background color for the given element.
 * 
 * @param element the element
 * @return	the background color for the element, or <code>null</code> 
 *   to use the default background color
 * @since 3.0
 */
public RGB getBackground(Object element);

}
