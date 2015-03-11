package ${basePackage}.override;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.treasureboat.webcore.appserver.TBSession;
import org.treasureboat.webcore.appserver.TBWApplication;
import org.treasureboat.webcore.security.user.TBWFirstResponder;

import com.webobjects.appserver.WOActionResults;

public class FirstResponder extends TBWFirstResponder {

	/** 
	 * <a href="http://wiki.wocommunity.org/display/documentation/Wonder+Logging">new org.slf4j.Logger</a> 
	 */
	static final Logger log = LoggerFactory.getLogger(FirstResponder.class);

	//********************************************************************
	//  Methods : メソッド
	//********************************************************************

	@Override
	public WOActionResults createFirstResponseAction(String loginUri) {
		WOActionResults result = super.createFirstResponseAction(loginUri);
		if (result != null) {
			// we got something from TB itself
			// ...
			// return result;
		}
		return TBWApplication.tbwApplication().pageWithName("Main", TBSession.session().context());
	}
}
