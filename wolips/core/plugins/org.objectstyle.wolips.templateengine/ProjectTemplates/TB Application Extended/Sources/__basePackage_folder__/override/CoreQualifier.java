package ${basePackage}.override;

#if ($pro)
import org.treasureboat.basemodel.corequalifier.TBBM_CoreQualifier;

public class CoreQualifier extends TBBM_CoreQualifier {
  
}
#else
import org.treasureboat.webcore.override.core.TBWCoreQualifierBase;
  
public class CoreQualifier extends TBWCoreQualifierBase {
  
}
#end
