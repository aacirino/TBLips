package org.objectstyle.wolips.eomodeler.outline;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.widgets.Display;
import org.objectstyle.wolips.baseforuiplugins.utils.WOTextCellEditor;
import org.objectstyle.wolips.eomodeler.core.model.DuplicateNameException;
import org.objectstyle.wolips.eomodeler.core.model.ISortableEOModelObject;

public class EOModelOutlineEditingSupport extends EditingSupport {
	private TextCellEditor _nameEditor;

	public EOModelOutlineEditingSupport(TreeViewer viewer) {
		super(viewer);
		_nameEditor = new WOTextCellEditor(viewer.getTree());
	}

	@Override
	protected boolean canEdit(Object element) {
		return (element instanceof ISortableEOModelObject);
	}

	@Override
	protected CellEditor getCellEditor(Object element) {
		return _nameEditor;
	}

	@Override
	protected Object getValue(Object element) {
		String text;
		if (element instanceof ISortableEOModelObject) {
			text = ((ISortableEOModelObject) element).getName();
			// } else if (element instanceof EORelationshipPath) {
			// EORelationshipPath relationshipPath = (EORelationshipPath)
			// element;
			// text = relationshipPath.getChildRelationship().getName();
			// } else if (element instanceof EOAttributePath) {
			// EOAttributePath attributePath = (EOAttributePath) element;
			// text = attributePath.getChildAttribute().getName();
		} else {
			text = null;
		}
		if (text == null) {
			text = "";
		}
		return text;
	}

	@Override
	protected void setValue(Object element, Object value) {
		if (element instanceof ISortableEOModelObject) {
			String newName = (String) value;
			try {
				((ISortableEOModelObject) element).setName(newName);
			} catch (DuplicateNameException e) {
				MessageDialog.openError(Display.getCurrent().getActiveShell(), "Duplicate Name", "The name '" + newName + "' is already taken.");
			}
			// } else if (element instanceof EORelationshipPath) {
			// EORelationshipPath relationshipPath = (EORelationshipPath)
			// element;
			// text = relationshipPath.getChildRelationship().getName();
			// } else if (element instanceof EOAttributePath) {
			// EOAttributePath attributePath = (EOAttributePath) element;
			// text = attributePath.getChildAttribute().getName();
		}
	}

}
