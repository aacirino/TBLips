package org.objectstyle.wolips.wizards.actions;

import org.eclipse.ui.INewWizard;
import org.objectstyle.wolips.wizards.TBFrameworkWizard;

/**
 * @author ishimoto
 */
public class OpenTBFrameworkWizard extends AbstractOpenWizardAction {

	@Override
	protected INewWizard createWizard() {
		return new TBFrameworkWizard();
	}

}