package $basePackage;

import ${basePackage}.navigation.NavigationController;

#if ($pro)
import me.webobjects.d2w.WOdkaD2WHandlerDelegate;

#end
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.treasureboat.webcore.appserver.TBSession;
import org.treasureboat.foundation.constants.TBFKnownLookNames;
import org.treasureboat.webcore.appserver.navbar.handler.TBNavigationHandler;

public class Session extends TBSession {

	private static final long serialVersionUID = 1L;
   
	/** 
	 * <a href="http://wiki.wocommunity.org/display/documentation/Wonder+Logging">new org.slf4j.Logger</a> 
	 */
	static final Logger log = LoggerFactory.getLogger(Session.class);
   
	//********************************************************************
	//  Constructor : コンストラクター
	//********************************************************************

	public Session() {
		super();

		// Set the Navigation Controller
		if(navController() == null) {
			setNavController(new NavigationController(this));
		}

		// Menu Handler メニュー・ハンドラー
#if ($pro)
		TBNavigationHandler.setDelegate(new WOdkaD2WHandlerDelegate());
#end
	
		// set the default Look
		setCurrentD2WLook(TBFKnownLookNames.GUMBY);
	}
}
