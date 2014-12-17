package org.objectstyle.wolips.wizards;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IProgressMonitor;

/**
 * @author swk
 */
public class TBApplicationWizard extends NewWOProjectWizard {
	public TBApplicationWizard() {
		super("TB Application");
	}

	@Override
	protected WizardType getWizardType() {
		return WizardType.TB_APPLICATION_WIZARD;
	}

	public String getWindowTitle() {
		return Messages.getString("TBApplicationCreationWizard.title");
	}

	@Override
	protected void postInstallTemplate(IProject project, IProgressMonitor progressMonitor) throws Exception {
		// DO NOTHING
	}
}