package org.objectstyle.wolips.wizards.actions;

import org.eclipse.ui.INewWizard;
import org.objectstyle.wolips.wizards.TBApplicationWizard;

public class OpenTBApplicationWizard extends AbstractOpenWizardAction {

	protected INewWizard createWizard() {
		return new TBApplicationWizard();
	}
}