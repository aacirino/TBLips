package org.objectstyle.wolips.wizards.template;

import java.util.List;

import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ListViewer;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.objectstyle.wolips.templateengine.ProjectTemplate;

public class SelectTemplateWizardPage extends WizardPage implements ISelectionChangedListener, IDoubleClickListener {
	private List<ProjectTemplate> _templates;

	private ListViewer _templateViewer;

	private ProjectTemplate _selectedProjectTemplate;

	protected SelectTemplateWizardPage() {
		super("Select Template");
		setPageComplete(false);
		_templates = ProjectTemplate.loadProjectTemplates(ProjectTemplate.PROJECT_TEMPLATES);
	}

	public List<ProjectTemplate> getProjectTemplates() {
		return _templates;
	}

	public ProjectTemplate getSelectedProjectTemplate() {
		return _selectedProjectTemplate;
	}

	public void createControl(Composite parent) {
		Composite composite = new Composite(parent, SWT.NONE);
		composite.setFont(parent.getFont());
		composite.setLayout(new GridLayout());
		composite.setLayoutData(new GridData(GridData.FILL_BOTH));

		initializeDialogUnits(parent);

		if (_templates.size() == 0) {
			setTitle("No Templates Found");
			Label noTemplatesLabel = new Label(composite, SWT.NONE);
			noTemplatesLabel.setText("There are no templates available.");
		} else {
			setTitle("Select a Template");
			_templateViewer = new ListViewer(composite, SWT.SINGLE | SWT.V_SCROLL | SWT.BORDER);
			_templateViewer.setLabelProvider(new TemplateLabelProvider());
			_templateViewer.setContentProvider(new TemplateContentProvider());
			_templateViewer.setInput(_templates);
			_templateViewer.getList().setLayoutData(new GridData(GridData.FILL_BOTH));
			_templateViewer.addSelectionChangedListener(this);
			_templateViewer.addDoubleClickListener(this);
		}

		setControl(composite);
	}

	public void selectionChanged(SelectionChangedEvent event) {
		IStructuredSelection templateSelection = (IStructuredSelection) event.getSelection();
		_selectedProjectTemplate = (ProjectTemplate) templateSelection.getFirstElement();
		setPageComplete(_selectedProjectTemplate != null);
	}

	public void doubleClick(DoubleClickEvent event) {
		if (isPageComplete()) {
			getWizard().getContainer().showPage(getNextPage());
		}
	}
}