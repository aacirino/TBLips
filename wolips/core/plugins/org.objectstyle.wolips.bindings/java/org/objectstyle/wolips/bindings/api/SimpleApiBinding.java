package org.objectstyle.wolips.bindings.api;

import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;
import org.objectstyle.wolips.baseforplugins.util.ComparisonUtils;
import org.objectstyle.wolips.bindings.wod.TypeCache;

public class SimpleApiBinding implements IApiBinding {
	private String _name;
	private String _defaults;
	private boolean _required;
	private boolean _willSet;

	public SimpleApiBinding(String name) {
		_name = name;
	}
  
  public int compareTo(IApiBinding o) {
    return (o == null) ? -1 : getName() == null ? -1 : getName().compareTo(o.getName());
  }

  @Override
  public boolean equals(Object o) {
    return o instanceof SimpleApiBinding && ComparisonUtils.equals(((SimpleApiBinding) o).getName(), getName());
  }

  @Override
  public int hashCode() {
    String name = getName();
    return name == null ? 0 : name.hashCode();
  }

	public boolean isAction() {
	  return ApiUtils.isActionBinding(this);
	}
	
	public void setDefaults(String defaults) {
		_defaults = defaults;
	}
	
	public String getDefaults() {
		return _defaults;
	}
	
	public int getSelectedDefaults() {
		return ApiUtils.getSelectedDefaults(this);
	}

	public String getName() {
		return _name;
	}
	
	public void setName(String name) {
		_name = name;
	}

	public boolean isRequired() {
		return _required;
	}
	
	public void setRequired(boolean required) {
		_required = required;
	}

	public boolean isWillSet() {
		return _willSet;
	}
	
	public void setWillSet(boolean willSet) {
		_willSet = willSet;
	}

	public String[] getValidValues(String partialValue, IJavaProject javaProject, IType componentType, TypeCache typeCache) throws JavaModelException {
		return ApiUtils.getValidValues(this, partialValue, javaProject, componentType, typeCache);
	}
}
