package org.objectstyle.wolips.componenteditor.actions;

public class InsertComponentTBRadioButtonAction extends InsertComponentAction {
	
	@Override
	public String getComponentInstanceNameSuffix() { 
		return "RadioButton"; 
	}

	@Override
	public String getComponentName() { 
		return "TBRadioButton"; 
	}
}
