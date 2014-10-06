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
package org.objectstyle.wolips.eomodeler.editors.model;

import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.views.properties.tabbed.AbstractPropertySection;
import org.eclipse.ui.views.properties.tabbed.TabbedPropertySheetPage;
import org.objectstyle.wolips.baseforplugins.util.ComparisonUtils;
import org.objectstyle.wolips.eomodeler.Messages;
import org.objectstyle.wolips.eomodeler.core.model.EOModel;
import org.objectstyle.wolips.eomodeler.core.utils.NamingConvention;
import org.objectstyle.wolips.eomodeler.utils.FormUtils;
import org.objectstyle.wolips.eomodeler.utils.StringLabelProvider;
import org.objectstyle.wolips.eomodeler.utils.UglyFocusHackWorkaroundListener;

public class EOModelAdvancedEditorSection extends AbstractPropertySection implements ModifyListener, ISelectionChangedListener, SelectionListener {
	private EOModel _model;

	private ComboViewer _entityCase;

	private ComboViewer _entitySeparator;

	private Text _entityPrefix;

	private Text _entitySuffix;

	private ComboViewer _attributeCase;

	private ComboViewer _attributeSeparator;

	private Text _attributePrefix;

	private Text _attributeSuffix;
	
	private Button _reverseEngineered;

	public EOModelAdvancedEditorSection() {
		// DO NOTHING
	}

	public void createControls(Composite parent, TabbedPropertySheetPage tabbedPropertySheetPage) {
		super.createControls(parent, tabbedPropertySheetPage);
		Composite form = getWidgetFactory().createFlatFormComposite(parent);
		FormLayout formLayout = new FormLayout();
		form.setLayout(formLayout);

		Composite topForm = FormUtils.createForm(getWidgetFactory(), form);

		getWidgetFactory().createCLabel(topForm, Messages.getString("EOModel." + EOModel.ATTRIBUTE_NAMING_CONVENTION + ".prefix"), SWT.NONE);
		_attributePrefix = new Text(topForm, SWT.BORDER);
		_attributePrefix.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		UglyFocusHackWorkaroundListener.addListener(_attributePrefix);

		getWidgetFactory().createCLabel(topForm, Messages.getString("EOModel." + EOModel.ATTRIBUTE_NAMING_CONVENTION + ".suffix"), SWT.NONE);
		_attributeSuffix = new Text(topForm, SWT.BORDER);
		_attributeSuffix.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		UglyFocusHackWorkaroundListener.addListener(_attributeSuffix);

		getWidgetFactory().createCLabel(topForm, Messages.getString("EOModel." + EOModel.ATTRIBUTE_NAMING_CONVENTION + ".case"), SWT.NONE);
		Combo attributeCaseCombo = new Combo(topForm, SWT.BORDER | SWT.FLAT | SWT.READ_ONLY);
		_attributeCase = new ComboViewer(attributeCaseCombo);
		_attributeCase.setLabelProvider(new StringLabelProvider());
		_attributeCase.setContentProvider(new ArrayContentProvider());
		_attributeCase.setInput(NamingConvention.Case.values());
		attributeCaseCombo.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		getWidgetFactory().createCLabel(topForm, Messages.getString("EOModel." + EOModel.ATTRIBUTE_NAMING_CONVENTION + ".separator"), SWT.NONE);
		Combo attributeSeparatorCombo = new Combo(topForm, SWT.BORDER | SWT.FLAT | SWT.READ_ONLY);
		_attributeSeparator = new ComboViewer(attributeSeparatorCombo);
		_attributeSeparator.setLabelProvider(new StringLabelProvider());
		_attributeSeparator.setContentProvider(new ArrayContentProvider());
		_attributeSeparator.setInput(NamingConvention.Separator.values());
		attributeSeparatorCombo.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		getWidgetFactory().createCLabel(topForm, Messages.getString("EOModel." + EOModel.ENTITY_NAMING_CONVENTION + ".prefix"), SWT.NONE);
		_entityPrefix = new Text(topForm, SWT.BORDER);
		_entityPrefix.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		UglyFocusHackWorkaroundListener.addListener(_entityPrefix);

		getWidgetFactory().createCLabel(topForm, Messages.getString("EOModel." + EOModel.ENTITY_NAMING_CONVENTION + ".suffix"), SWT.NONE);
		_entitySuffix = new Text(topForm, SWT.BORDER);
		_entitySuffix.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		UglyFocusHackWorkaroundListener.addListener(_entitySuffix);

		getWidgetFactory().createCLabel(topForm, Messages.getString("EOModel." + EOModel.ENTITY_NAMING_CONVENTION + ".case"), SWT.NONE);
		Combo entityCaseCombo = new Combo(topForm, SWT.BORDER | SWT.FLAT | SWT.READ_ONLY);
		_entityCase = new ComboViewer(entityCaseCombo);
		_entityCase.setLabelProvider(new StringLabelProvider());
		_entityCase.setContentProvider(new ArrayContentProvider());
		_entityCase.setInput(NamingConvention.Case.values());
		entityCaseCombo.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		getWidgetFactory().createCLabel(topForm, Messages.getString("EOModel." + EOModel.ENTITY_NAMING_CONVENTION + ".separator"), SWT.NONE);
		Combo entitySeparatorCombo = new Combo(topForm, SWT.BORDER | SWT.FLAT | SWT.READ_ONLY);
		_entitySeparator = new ComboViewer(entitySeparatorCombo);
		_entitySeparator.setLabelProvider(new StringLabelProvider());
		_entitySeparator.setContentProvider(new ArrayContentProvider());
		_entitySeparator.setInput(NamingConvention.Separator.values());
		entitySeparatorCombo.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		
		getWidgetFactory().createCLabel(topForm, "", SWT.NONE);
		_reverseEngineered = getWidgetFactory().createButton(topForm, "Reverse Engineered", SWT.CHECK);
		_reverseEngineered.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
	}

	public void setInput(IWorkbenchPart part, ISelection selection) {
		if (ComparisonUtils.equals(selection, getSelection())) {
			return;
		}
		
		super.setInput(part, selection);
		disposeBindings();

		Object selectedObject = ((IStructuredSelection) selection).getFirstElement();
		_model = (EOModel) selectedObject;
		if (_model != null) {
			String attributePrefix = _model.getAttributeNamingConvention().getPrefix();
			String attributeSuffix = _model.getAttributeNamingConvention().getSuffix();
			_attributePrefix.setText(attributePrefix == null ? "" : attributePrefix);
			_attributeSuffix.setText(attributeSuffix == null ? "" : attributeSuffix);
			_attributeCase.setSelection(new StructuredSelection(_model.getAttributeNamingConvention().getCase()));
			_attributeSeparator.setSelection(new StructuredSelection(_model.getAttributeNamingConvention().getSeparator()));

			String entityPrefix = _model.getEntityNamingConvention().getPrefix();
			String entitySuffix = _model.getEntityNamingConvention().getSuffix();
			_entityPrefix.setText(entityPrefix == null ? "" : entityPrefix);
			_entitySuffix.setText(entitySuffix == null ? "" : entitySuffix);
			_entityCase.setSelection(new StructuredSelection(_model.getEntityNamingConvention().getCase()));
			_entitySeparator.setSelection(new StructuredSelection(_model.getEntityNamingConvention().getSeparator()));

			_reverseEngineered.setSelection(_model.isReverseEngineered());

			_attributePrefix.addModifyListener(this);
			_attributeSuffix.addModifyListener(this);
			_attributeCase.addSelectionChangedListener(this);
			_attributeSeparator.addSelectionChangedListener(this);
			_entityPrefix.addModifyListener(this);
			_entitySuffix.addModifyListener(this);
			_entityCase.addSelectionChangedListener(this);
			_entitySeparator.addSelectionChangedListener(this);
			_reverseEngineered.addSelectionListener(this);
		}
	}

	protected void disposeBindings() {
		if (_attributePrefix != null && !_attributePrefix.isDisposed()) {
			_attributePrefix.removeModifyListener(this);
		}
		if (_attributeSuffix != null && !_attributeSuffix.isDisposed()) {
			_attributeSuffix.removeModifyListener(this);
		}
		if (_attributeCase != null && !_attributeCase.getCombo().isDisposed()) {
			_attributeCase.removeSelectionChangedListener(this);
		}
		if (_attributeSeparator != null && !_attributeSeparator.getCombo().isDisposed()) {
			_attributeSeparator.removeSelectionChangedListener(this);
		}
		if (_entityPrefix != null && !_entityPrefix.isDisposed()) {
			_entityPrefix.removeModifyListener(this);
		}
		if (_entitySuffix != null && !_entitySuffix.isDisposed()) {
			_entitySuffix.removeModifyListener(this);
		}
		if (_entityCase != null && !_entityCase.getCombo().isDisposed()) {
			_entityCase.removeSelectionChangedListener(this);
		}
		if (_entitySeparator != null && !_entitySeparator.getCombo().isDisposed()) {
			_entitySeparator.removeSelectionChangedListener(this);
		}
		if (_reverseEngineered != null && !_reverseEngineered.isDisposed()) {
			_reverseEngineered.removeSelectionListener(this);
		}
	}

	public void dispose() {
		disposeBindings();
		super.dispose();
	}

	public void modifyText(ModifyEvent e) {
		syncToModel();
	}

	public void selectionChanged(SelectionChangedEvent event) {
		syncToModel();
	}

	public void syncToModel() {
		NamingConvention.Case attributeCase = (NamingConvention.Case) ((IStructuredSelection) _attributeCase.getSelection()).getFirstElement();
		NamingConvention.Separator attributeSeparator = (NamingConvention.Separator) ((IStructuredSelection) _attributeSeparator.getSelection()).getFirstElement();
		_model.setAttributeNamingConvention(new NamingConvention(attributeCase, attributeSeparator, _attributePrefix.getText(), _attributeSuffix.getText()));

		NamingConvention.Case entityCase = (NamingConvention.Case) ((IStructuredSelection) _entityCase.getSelection()).getFirstElement();
		NamingConvention.Separator entitySeparator = (NamingConvention.Separator) ((IStructuredSelection) _entitySeparator.getSelection()).getFirstElement();
		_model.setEntityNamingConvention(new NamingConvention(entityCase, entitySeparator, _entityPrefix.getText(), _entitySuffix.getText()));
		
		_model.setReverseEngineered(_reverseEngineered.getSelection());
	}

	public void widgetDefaultSelected(SelectionEvent e) {
		syncToModel();
	}

	public void widgetSelected(SelectionEvent e) {
		syncToModel();
	}
}
