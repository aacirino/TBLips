package ${basePackage}.components;

import org.treasureboat.webcore.annotations.TBPageAccess;
#if ($pro)
import me.webobjects.components.WOdkaWrapperD2WComponent;
#else
import org.treasureboat.webcore.components.TBComponent;
#end

import com.webobjects.appserver.WOContext;

@TBPageAccess(navigationState = "Welcome", requireLogin = "true")
public class Main extends #if ($pro) WOdkaWrapperD2WComponent #else TBComponent #end {

	private static final long serialVersionUID = 1L;

	//********************************************************************
	//  Constructor : コンストラクタ
	//********************************************************************

	public Main(WOContext context) {
		super(context);
	}

}
