/*
 * ====================================================================
 * 
 * The ObjectStyle Group Software License, Version 1.0
 * 
 * Copyright (c) 2006 The ObjectStyle Group and individual authors of the
 * software. All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * 
 * 1. Redistributions of source code must retain the above copyright notice,
 * this list of conditions and the following disclaimer.
 * 
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 * 
 * 3. The end-user documentation included with the redistribution, if any, must
 * include the following acknowlegement: "This product includes software
 * developed by the ObjectStyle Group (http://objectstyle.org/)." Alternately,
 * this acknowlegement may appear in the software itself, if and wherever such
 * third-party acknowlegements normally appear.
 * 
 * 4. The names "ObjectStyle Group" and "Cayenne" must not be used to endorse or
 * promote products derived from this software without prior written permission.
 * For written permission, please contact andrus@objectstyle.org.
 * 
 * 5. Products derived from this software may not be called "ObjectStyle" nor
 * may "ObjectStyle" appear in their names without prior written permission of
 * the ObjectStyle Group.
 * 
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED WARRANTIES,
 * INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND
 * FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE
 * OBJECTSTYLE GROUP OR ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT,
 * INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA,
 * OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE,
 * EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 * ====================================================================
 * 
 * This software consists of voluntary contributions made by many individuals on
 * behalf of the ObjectStyle Group. For more information on the ObjectStyle
 * Group, please see <http://objectstyle.org/>.
 *  
 */
package org.objectstyle.wolips.eomodeler.editors.attribute;

import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IWorkbenchPart;
import org.objectstyle.wolips.baseforplugins.util.ComparisonUtils;
import org.objectstyle.wolips.eomodeler.Messages;
import org.objectstyle.wolips.eomodeler.core.model.AbstractEOArgument;
import org.objectstyle.wolips.eomodeler.core.model.EOArgument;
import org.objectstyle.wolips.eomodeler.core.model.EOArgumentDirection;
import org.objectstyle.wolips.eomodeler.utils.ComboViewerBinding;

public class EOArgumentBasicEditorSection extends AbstractEOArgumentBasicEditorSection {
	private ComboViewer myDirectionComboViewer;

	private ComboViewerBinding myDirectionBinding;

	@Override
	protected void _addSettings(Composite settings) {
		// DO NOTHING
	}
	
	protected void _addComponents(Composite _parent) {
		getWidgetFactory().createCLabel(_parent, Messages.getString("EOArgument." + EOArgument.DIRECTION), SWT.NONE);
		Combo prototypeCombo = new Combo(_parent, SWT.BORDER | SWT.FLAT | SWT.READ_ONLY);
		myDirectionComboViewer = new ComboViewer(prototypeCombo);
		myDirectionComboViewer.setLabelProvider(new EOArgumentLabelProvider());
		myDirectionComboViewer.setContentProvider(new EOArgumentContentProvider());
		myDirectionComboViewer.setInput(EOArgumentDirection.ARGUMENT_DIRECTIONS);
		GridData prototypeComboLayoutData = new GridData(GridData.FILL_HORIZONTAL);
		prototypeCombo.setLayoutData(prototypeComboLayoutData);
	}

	protected void _argumentChanged(AbstractEOArgument _argument) {
		EOArgument argument = (EOArgument) _argument;
		if (argument != null) {
			myDirectionBinding = new ComboViewerBinding(myDirectionComboViewer, argument, EOArgument.DIRECTION, null, null, null);
		}
	}

	protected void disposeBindings() {
		if (myDirectionBinding != null) {
			myDirectionBinding.dispose();
		}
		super.disposeBindings();
	}

	public void setInput(IWorkbenchPart part, ISelection selection) {
		if (ComparisonUtils.equals(selection, getSelection())) {
			return;
		}
		
		super.setInput(part, selection);
		EOArgument attribute = null;
		Object selectedObject = ((IStructuredSelection) selection).getFirstElement();
		if (selectedObject instanceof EOArgument) {
			attribute = (EOArgument) selectedObject;
		}
		setArgument(attribute);
	}
}
