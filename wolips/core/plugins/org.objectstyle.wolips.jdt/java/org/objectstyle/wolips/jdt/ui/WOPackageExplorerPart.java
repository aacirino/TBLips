/* ====================================================================
 * 
 * The ObjectStyle Group Software License, Version 1.0 
 *
 * Copyright (c) 2007 The ObjectStyle Group 
 * and individual authors of the software.  All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer. 
 *
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the
 *    distribution.
 *
 * 3. The end-user documentation included with the redistribution, if
 *    any, must include the following acknowlegement:  
 *       "This product includes software developed by the 
 *        ObjectStyle Group (http://objectstyle.org/)."
 *    Alternately, this acknowlegement may appear in the software itself,
 *    if and wherever such third-party acknowlegements normally appear.
 *
 * 4. The names "ObjectStyle Group" and "Cayenne" 
 *    must not be used to endorse or promote products derived
 *    from this software without prior written permission. For written 
 *    permission, please contact andrus@objectstyle.org.
 *
 * 5. Products derived from this software may not be called "ObjectStyle"
 *    nor may "ObjectStyle" appear in their names without prior written
 *    permission of the ObjectStyle Group.
 *
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED.  IN NO EVENT SHALL THE OBJECTSTYLE GROUP OR
 * ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF
 * USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 * ====================================================================
 *
 * This software consists of voluntary contributions made by many
 * individuals on behalf of the ObjectStyle Group.  For more
 * information on the ObjectStyle Group, please see
 * <http://objectstyle.org/>.
 *
 */
package org.objectstyle.wolips.jdt.ui;

import org.eclipse.jdt.internal.ui.packageview.PackageExplorerContentProvider;
import org.eclipse.jdt.internal.ui.packageview.PackageExplorerPart;
import org.eclipse.jdt.ui.PreferenceConstants;
import org.eclipse.jdt.ui.actions.JdtActionConstants;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.actions.ActionFactory;
import org.objectstyle.wolips.jdt.ui.refactoring.actions.RenameWOComponentAction;

public class WOPackageExplorerPart extends PackageExplorerPart {
	private static final int SHOW_PROJECTS= PackageExplorerPart.PROJECTS_AS_ROOTS;
	private static final int SHOW_WORKING_SETS= PackageExplorerPart.WORKING_SETS_AS_ROOTS;
	
	private RenameWOComponentAction renameAction;
	
	public WOPackageExplorerPart() {
		super();
	}

	@Override
	public PackageExplorerContentProvider createContentProvider() {
		IPreferenceStore store = PreferenceConstants.getPreferenceStore();
		boolean showCUChildren = store.getBoolean(PreferenceConstants.SHOW_CU_CHILDREN);
		if (getRootMode() == SHOW_PROJECTS) {
			return new WOPackageExplorerContentProvider(showCUChildren, this);
		}
		return new WOWorkingSetAwareContentProvider(showCUChildren, getWorkingSetModel(), this);
	}
	
	@Override
	public void createPartControl(Composite parent) {
		super.createPartControl(parent);
		switchToWOSorter();
		makeActions();
		getViewSite().getActionBars().setGlobalActionHandler(
				ActionFactory.RENAME.getId(), renameAction);
		getViewSite().getActionBars().setGlobalActionHandler(
				JdtActionConstants.RENAME, renameAction);
		this.getTreeViewer().addSelectionChangedListener(renameAction);
	}

	@Override
	public void rootModeChanged(int newMode) {
		super.rootModeChanged(newMode);
		switchToWOSorter();
	}

	@Override
	public void dispose() {
		this.getTreeViewer().removeSelectionChangedListener(renameAction);
		super.dispose();
	}
	
	protected void switchToWOSorter() {
		TreeViewer viewer = getTreeViewer();
		boolean showWorkingSets = (getRootMode() == SHOW_WORKING_SETS);
		if (showWorkingSets) {
			viewer.setComparator(new WOWorkingSetAwareJavaElementSorter());
		} else {
			viewer.setComparator(new WOJavaElementComparator());
		}
	}
	
	private void makeActions() {
		renameAction = new RenameWOComponentAction(this.getSite()); 
		renameAction.setText("Rename WOComponent..."); 
		renameAction.setToolTipText("Rename WOComponent"); 
		renameAction.setEnabled(false);
	}
	
}
