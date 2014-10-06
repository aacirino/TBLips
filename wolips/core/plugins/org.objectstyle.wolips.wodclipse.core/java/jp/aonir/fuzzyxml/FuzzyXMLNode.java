package jp.aonir.fuzzyxml;

import jp.aonir.fuzzyxml.internal.RenderContext;

public interface FuzzyXMLNode {

  //	public String getNamespaceURI();
  //	
  //	public String getPrefix();

  public FuzzyXMLNode getParentNode();

  public int getOffset();

  public int getLength();

  public String toDebugString();
  
  public void toDebugString(StringBuffer buffer, int indent);

  public String toXMLString(RenderContext renderContext);

  public void toXMLString(RenderContext renderContext, StringBuffer xmlBuffer);
  
  public boolean isSynthetic();
}
