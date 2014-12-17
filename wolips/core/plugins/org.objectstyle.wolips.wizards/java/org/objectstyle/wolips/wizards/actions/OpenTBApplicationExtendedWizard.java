package org.objectstyle.wolips.wizards.actions;

import org.eclipse.ui.INewWizard;
import org.objectstyle.wolips.wizards.TBApplicationExtendedWizard;

public class OpenTBApplicationExtendedWizard extends AbstractOpenWizardAction {

	protected INewWizard createWizard() {
		return new TBApplicationExtendedWizard();
	}
}