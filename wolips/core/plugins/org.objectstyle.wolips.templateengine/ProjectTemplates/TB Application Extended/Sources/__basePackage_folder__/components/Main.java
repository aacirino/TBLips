package ${basePackage}.components;

import org.treasureboat.webcore.annotations.TBPageAccess;
import org.treasureboat.webcore.components.TBComponent;

import com.webobjects.appserver.WOContext;

@TBPageAccess (
    navigationState = "Welcome"
    )
public class Main extends TBComponent {

	private static final long serialVersionUID = 1L;

	//********************************************************************
	//  Constructor : コンストラクタ
	//********************************************************************

	public Main(WOContext context) {
		super(context);
	}

}
