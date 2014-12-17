package ${basePackage}.override;

#if ($pro)
import me.webobjects.override.WOdkaBaseModelFirstDataCreator;
#else
import org.treasureboat.webcore.override.TBWInitializerOfFirstDataCreator;
#end

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.treasureboat.webcore.security.grant.TBWGrantAccess;

#if ($pro)
public class FirstDataCreator extends WOdkaBaseModelFirstDataCreator {
#else
public class FirstDataCreator extends TBWInitializerOfFirstDataCreator { 
#end

	/** 
	 * <a href="http://wiki.wocommunity.org/display/documentation/Wonder+Logging">new org.slf4j.Logger</a> 
	 */
	static final Logger log = LoggerFactory.getLogger(FirstDataCreator.class);

	//********************************************************************
	//  Methods : メソッド
	//********************************************************************

	@Override
	public void createFirstDataset() {
		super.createFirstDataset();

		// ... 

		TBWGrantAccess.setGrantAsRestrict();
	}
}
