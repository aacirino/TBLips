package ${basePackage}.override;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
#if ($pro || $base)
	import org.treasureboat.basemodel.override.TBBM_FirstDataCreator;
#else
	import org.treasureboat.webcore.override.TBWInitializerOfFirstDataCreator;
#end
import org.treasureboat.webcore.security.grant.TBWGrantAccess;

#if ($pro || $base)
public class FirstDataCreator extends TBBM_FirstDataCreator {
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
		log.info("createFirstDataset");
		
		TBWGrantAccess.setGrantAsRestrict();
	}
}
