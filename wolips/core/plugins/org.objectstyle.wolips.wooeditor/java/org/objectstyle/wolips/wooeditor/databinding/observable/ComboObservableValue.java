/*******************************************************************************
 * Copyright (c) 2005, 2007 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     Brad Reynolds - bug 164653
 *     Ashley Cambrell - bug 198904
 *******************************************************************************/
package org.objectstyle.wolips.wooeditor.databinding.observable;

import org.eclipse.core.databinding.observable.Diffs;
import org.eclipse.core.databinding.observable.value.AbstractObservableValue;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Combo;

/**
 * @since 3.2
 * 
 */
public class ComboObservableValue extends AbstractObservableValue {

	private final Combo combo;
	private final String attribute;
	private boolean updating = false;
	private String currentValue;
	private ModifyListener modifyListener;
	private SelectionListener selectionListener;

	/**
	 * @param combo
	 * @param attribute
	 */
	public ComboObservableValue(Combo combo, String attribute) {
		this.combo = combo;
		this.attribute = attribute;

		if (attribute.equals(SWTProperties.SELECTION) || attribute.equals(SWTProperties.TEXT)) {
			this.currentValue = combo.getText();
			modifyListener = new ModifyListener() {
				public void modifyText(ModifyEvent e) {
					if (!updating) {
						String oldValue = currentValue;
						currentValue = ComboObservableValue.this.combo.getText();
						fireValueChange(Diffs.createValueDiff(oldValue, currentValue));
					} else {
						currentValue = ComboObservableValue.this.combo.getText();
					}
				}
			};
			
			selectionListener = new SelectionListener() {
				public void widgetDefaultSelected(SelectionEvent e) {
					if (!updating) {
						String oldValue = currentValue;
						currentValue = ComboObservableValue.this.combo.getText();
						fireValueChange(Diffs.createValueDiff(oldValue, currentValue));
					} else {
						currentValue = ComboObservableValue.this.combo.getText();
					}
				}

				public void widgetSelected(SelectionEvent e) {
					if (!updating) {
						String oldValue = currentValue;
						currentValue = ComboObservableValue.this.combo.getText();
						fireValueChange(Diffs.createValueDiff(oldValue, currentValue));
					} else {
						currentValue = ComboObservableValue.this.combo.getText();
					}
				}
			};
			combo.addModifyListener(modifyListener);
			combo.addSelectionListener(selectionListener);
		} else
			throw new IllegalArgumentException();
	}

	@Override
	public void doSetValue(final Object value) {
		String oldValue = combo.getText();
		try {
			updating = true;
			if (attribute.equals(SWTProperties.TEXT)) {
				String stringValue = value != null ? value.toString() : ""; //$NON-NLS-1$
				combo.setText(stringValue);
			} else if (attribute.equals(SWTProperties.SELECTION)) {
				String items[] = combo.getItems();
				int index = -1;
				if (items != null && value != null) {
					for (int i = 0; i < items.length; i++) {
						if (value.equals(items[i])) {
							index = i;
							break;
						}
					}
					if (index == -1) {
						combo.setText((String) value);
					} else {
						combo.select(index); // -1 will not "unselect"
					}
				}
			}
		} finally {
			updating = false;
		}
		fireValueChange(Diffs.createValueDiff(oldValue, combo.getText()));
	}

	@Override
	public Object doGetValue() {
		if (attribute.equals(SWTProperties.TEXT))
			return combo.getText();

		// The problem with a ccombo, is that it changes the text and
		// fires before it update its selection index
		return combo.getText();
	}

	public Object getValueType() {
		return String.class;
	}

	/**
	 * @return attribute being observed
	 */
	public String getAttribute() {
		return attribute;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.core.databinding.observable.value.AbstractObservableValue#dispose
	 * ()
	 */
	@Override
	public synchronized void dispose() {
		super.dispose();

		if (modifyListener != null && !combo.isDisposed()) {
			combo.removeModifyListener(modifyListener);
		}
	}
}
