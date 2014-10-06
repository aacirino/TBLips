package org.objectstyle.wolips.componenteditor.inspector;

import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.ISaveablePart;
import org.eclipse.ui.IViewSite;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.editors.text.TextEditor;
import org.eclipse.ui.part.IPage;
import org.eclipse.ui.part.PageBook;
import org.eclipse.ui.part.PageBookView;
import org.objectstyle.wolips.componenteditor.part.ComponentEditor;
import org.objectstyle.wolips.templateeditor.TemplateEditor;
import org.objectstyle.wolips.wodclipse.core.util.ICursorPositionListener;
import org.objectstyle.wolips.wodclipse.editor.WodEditor;

public class BindingsInspectorPageBookView extends PageBookView implements ICursorPositionListener {
	private TextEditor _lastEditor;

	private Point _lastSelectionRange;

	public BindingsInspectorPageBookView() {
		super();
	}

	protected IPage createDefaultPage(PageBook book) {
		BindingsInspectorPage page = new BindingsInspectorPage(null);
		initPage(page);
		page.createControl(book);
		return page;
	}

	public void createPartControl(Composite parent) {
		super.createPartControl(parent);
		// getSite().getPage().getWorkbenchWindow().getWorkbench().getHelpSystem().setHelp(getPageBook(),
		// IPropertiesHelpContextIds.PROPERTY_SHEET_VIEW);
	}

	public void dispose() {
		super.dispose();
		// getSite().getPage().removeSelectionListener(this);
	}

	protected PageRec doCreatePage(IWorkbenchPart part) {
		PageRec pageRec = null;
		if (part instanceof ComponentEditor) {
			ComponentEditor componentEditor = (ComponentEditor) part;
			BindingsInspectorPage page = new BindingsInspectorPage(componentEditor);
			initPage(page);
			page.createControl(getPageBook());
			pageRec = new PageRec(part, page);
		}
		return pageRec;
	}

	protected void doDestroyPage(IWorkbenchPart part, PageRec rec) {
		BindingsInspectorPage page = (BindingsInspectorPage) rec.page;
		page.dispose();
		rec.dispose();
	}

	protected IWorkbenchPart getBootstrapPart() {
		IWorkbenchPage page = getSite().getPage();
		if (page != null) {
			return page.getActivePart();
		}
		return null;
	}

	public void init(IViewSite site) throws PartInitException {
		// site.getPage().addSelectionListener(this);
		super.init(site);
	}

	protected boolean isImportant(IWorkbenchPart part) {
		return part instanceof ComponentEditor;
	}

	@Override
	public void partBroughtToTop(IWorkbenchPart part) {
		super.partBroughtToTop(part);
	}

	@Override
	public void partDeactivated(IWorkbenchPart part) {
		super.partDeactivated(part);
		if (part instanceof ComponentEditor) {
			ComponentEditor componentEditor = (ComponentEditor) part;
			TemplateEditor templateEditor = componentEditor.getTemplateEditor();
			if (templateEditor != null) {
				templateEditor.getSourceEditor().removeCursorPositionListener(this);
			}
			WodEditor wodEditor = componentEditor.getWodEditor();
			if (wodEditor != null) {
				wodEditor.removeCursorPositionListener(this);
			}
		}
	}

	public void partActivated(IWorkbenchPart part) {
		super.partActivated(part);
		if (part instanceof ComponentEditor) {
			ComponentEditor componentEditor = (ComponentEditor) part;
			TemplateEditor templateEditor = componentEditor.getTemplateEditor();
			if (templateEditor != null) {
				templateEditor.getSourceEditor().addCursorPositionListener(this);
			}
			WodEditor wodEditor = componentEditor.getWodEditor();
			if (wodEditor != null) {
				wodEditor.addCursorPositionListener(this);
			}
		} else if (part instanceof BindingsInspectorPageBookView) {
			if (_lastEditor != null && _lastSelectionRange != null) {
				cursorPositionChanged(_lastEditor, _lastSelectionRange);
			}
		}
	}

	public void cursorPositionChanged(TextEditor editor, Point selectionRange) {
		if (getViewSite().getPage().isPartVisible(this)) {
			// pass the selection to the page
			BindingsInspectorPage page = (BindingsInspectorPage) getCurrentPage();
			if (page != null) {
				page.cursorPositionChanged(editor, selectionRange);
			}
		}
		_lastEditor = editor;
		_lastSelectionRange = selectionRange;
	}

	protected Object getViewAdapter(Class key) {
		if (ISaveablePart.class.equals(key)) {
			return getSaveablePart();
		}
		return super.getViewAdapter(key);
	}

	protected ISaveablePart getSaveablePart() {
		IWorkbenchPart part = getCurrentContributingPart();
		if (part instanceof ISaveablePart) {
			return (ISaveablePart) part;
		}
		return null;
	}
}
