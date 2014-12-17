package ${basePackage};

import ${basePackage}.components.Main;

import com.webobjects.appserver.WOActionResults;
import com.webobjects.appserver.WORequest;

import er.extensions.appserver.ERXDirectAction;

public class DirectAction extends ERXDirectAction {

	//********************************************************************
	//  Constructor : コンストラクタ
	//********************************************************************

	public DirectAction(WORequest request) {
		super(request);
	}

	//********************************************************************
	//  Login Starter ログイン入り口
	//********************************************************************

	/**
	 * Main Page
	 */
	public WOActionResults s634Action() {
		Main nextPage = pageWithName(Main.class);
		return nextPage;
	}
}
