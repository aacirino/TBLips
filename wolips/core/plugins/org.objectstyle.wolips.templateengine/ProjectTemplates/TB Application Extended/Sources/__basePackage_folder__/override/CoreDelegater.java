package ${basePackage}.override;

#if ($pro)
import me.webobjects.override.WOdkaBaseCoreDelegater;

public class CoreDelegater extends WOdkaBaseCoreDelegater {
  
}
#else
import org.treasureboat.webcore.override.core.TBWCoreDelegaterBase;
  
public class CoreDelegater extends TBWCoreDelegaterBase {
  
}
#end
