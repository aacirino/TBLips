package org.objectstyle.wolips.wizards;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IProgressMonitor;

/**
 * @author swk
 */
public class TBApplicationExtendedWizard extends NewWOProjectWizard {
	public TBApplicationExtendedWizard() {
		super("TB Application Extended");
	}

	@Override
	protected WizardType getWizardType() {
		return WizardType.TB_APPLICATION_EX_WIZARD;
	}

	public String getWindowTitle() {
		return Messages.getString("TBApplicationExtendedCreationWizard.title");
	}

	@Override
	protected void postInstallTemplate(IProject project, IProgressMonitor progressMonitor) throws Exception {
		// DO NOTHING
	}
}