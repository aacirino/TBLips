package ${basePackage}.override;

#if ($pro)
import me.webobjects.override.WOdkaBaseModelLoginCheck;
#else
import org.treasureboat.webcore.security.password.TBWLoginBaseCheck;
#end

#if ($pro)
	public class LoginCheck extends WOdkaBaseModelLoginCheck {
#else
	public class LoginCheck extends TBWLoginBaseCheck {
#end
	
	//********************************************************************
	//  implements ITBWLoginProcess
	//********************************************************************

	// override your needs ...

	//********************************************************************
	//  implements ITBWPasswordChange
	//********************************************************************

	// override your needs ...

	//********************************************************************
	//  implements ITBWPasswordReset
	//********************************************************************

	// override your needs ...

}
