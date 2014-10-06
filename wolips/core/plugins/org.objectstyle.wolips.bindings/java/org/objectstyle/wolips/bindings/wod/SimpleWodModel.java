package org.objectstyle.wolips.bindings.wod;


public class SimpleWodModel extends AbstractWodModel {
	private String _name;
	
	public SimpleWodModel(String name) {
		_name = name;
	}

	public String getName() {
		return _name;
	}
	
	public IWodElement getWodElementAtIndex(int index) {
	  return null;
	}

	public IWodUnit getWodUnitAtIndex(int _index) {
		return null;
	}

	public int getEndOffset() {
		return 0;
	}

	public int getStartOffset() {
		return 0;
	}
}
