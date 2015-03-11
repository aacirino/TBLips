package ${basePackage}.override;

#if ($pro || $base)
import org.treasureboat.basemodel.override.TBBM_LoginCheck;
#else
import org.treasureboat.webcore.security.password.TBWLoginBaseCheck;
#end

#if ($pro || $base)
	public class LoginCheck extends TBBM_LoginCheck {
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
