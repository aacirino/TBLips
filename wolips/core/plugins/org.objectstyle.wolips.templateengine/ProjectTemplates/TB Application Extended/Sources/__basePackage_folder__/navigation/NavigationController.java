package ${basePackage}.navigation;

import org.treasureboat.webcore.appserver.navbar.TBWNavigationBaseController;

import ${basePackage}.Session;
import ${basePackage}.components.Main;
import com.webobjects.appserver.WOActionResults;

public class NavigationController extends TBWNavigationBaseController {

	//********************************************************************
	//  Constructor : コンストラクタ
	//********************************************************************

	public NavigationController(Session s) {
		super(s);
	}

	//********************************************************************
	//  Shared Actions : 共通アクション
	//********************************************************************

	public WOActionResults defaultAction() {
		return pageWithName(Main.class);
	}

	//********************************************************************
	//  Actions : アクション
	//********************************************************************

	// ...
	
}
