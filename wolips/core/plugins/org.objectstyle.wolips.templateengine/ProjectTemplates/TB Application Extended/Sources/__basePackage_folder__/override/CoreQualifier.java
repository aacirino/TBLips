package ${basePackage}.override;

#if ($pro)
import me.webobjects.coreQualifier.WOdkaBaseModelCoreQualifier;

public class CoreQualifier extends WOdkaBaseModelCoreQualifier {
  
}
#else
import org.treasureboat.webcore.override.core.TBWCoreQualifierBase;
  
public class CoreQualifier extends TBWCoreQualifierBase {
  
}
#end
