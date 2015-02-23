package org.objectstyle.wolips.wizards;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IProgressMonitor;

/**
 * @author ishimoto
 */
public class TBFrameworkWizard extends NewWOProjectWizard {

	public TBFrameworkWizard() {
		super("TB Framework");
	}

	@Override
	protected WizardType getWizardType() {
		return WizardType.TB_FRAMEWORK;
	}

	@Override
	public String getWindowTitle() {
		return Messages.getString("TBFrameworkCreationWizard.title");
	}

	@Override
	protected void postInstallTemplate(IProject project, IProgressMonitor progressMonitor) throws Exception {
		// DO NOTHING
	}
}