/*******************************************************************************
 * Copyright (c) 2006, 2015 IBM Corporation and others.
 *
 * This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/

package org.eclipse.jface.databinding.viewers;

import java.util.Iterator;

import org.eclipse.core.databinding.observable.set.IObservableSet;
import org.eclipse.core.databinding.observable.set.ISetChangeListener;
import org.eclipse.core.databinding.observable.set.SetChangeEvent;
import org.eclipse.jface.internal.databinding.provisional.viewers.ViewerLabelProvider;

/**
 * @since 1.1
 *
 */
public abstract class ListeningLabelProvider extends ViewerLabelProvider {

	private ISetChangeListener listener = new ISetChangeListener() {
		@Override
		public void handleSetChange(SetChangeEvent event) {
			for (Iterator it = event.diff.getAdditions().iterator(); it.hasNext();) {
				addListenerTo(it.next());
			}
			for (Iterator it = event.diff.getRemovals().iterator(); it.hasNext();) {
				removeListenerFrom(it.next());
			}
		}
	};

	private IObservableSet items;

	/**
	 * @param itemsThatNeedLabels
	 */
	public ListeningLabelProvider(IObservableSet itemsThatNeedLabels) {
		this.items = itemsThatNeedLabels;
		items.addSetChangeListener(listener);
		for (Iterator it = items.iterator(); it.hasNext();) {
			addListenerTo(it.next());
		}
	}

	/**
	 * @param next
	 */
	protected abstract void removeListenerFrom(Object next);

	/**
	 * @param next
	 */
	protected abstract void addListenerTo(Object next);

	@Override
	public void dispose() {
		for (Iterator iter = items.iterator(); iter.hasNext();) {
			removeListenerFrom(iter.next());
		}
		items.removeSetChangeListener(listener);
		super.dispose();
	}
}
