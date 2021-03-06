/*******************************************************************************
 * Copyright (c) 2000, 2014 IBM Corporation and others.
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
package org.eclipse.ui.internal;

import org.eclipse.ui.IWorkbenchPage;

/**
 * The selection service for a page.
 */
/* package */
class PageSelectionService extends AbstractSelectionService {

    private IWorkbenchPage page;

    /**
     * Creates a new selection service for a specific workbench page.
     */
    public PageSelectionService(IWorkbenchPage page) {
        setPage(page);
    }

    /**
     * Sets the page.
     */
    private void setPage(IWorkbenchPage page) {
        this.page = page;
    }

    /**
     * Returns the page.
     */
    protected IWorkbenchPage getPage() {
        return page;
    }

    /*
     * @see AbstractSelectionService#createPartTracker(String)
     */
    @Override
	protected AbstractPartSelectionTracker createPartTracker(String partId) {
        return new PagePartSelectionTracker(getPage(), partId);
    }

}
