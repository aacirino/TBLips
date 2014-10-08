package org.objectstyle.wolips.htmlpreview.editor.tags;

import java.util.Stack;

import jp.aonir.fuzzyxml.FuzzyXMLElement;
import jp.aonir.fuzzyxml.FuzzyXMLNode;
import jp.aonir.fuzzyxml.internal.RenderContext;

import org.objectstyle.wolips.bindings.wod.IWodBinding;
import org.objectstyle.wolips.bindings.wod.IWodElement;
import org.objectstyle.wolips.htmlpreview.editor.TagDelegate;
import org.objectstyle.wolips.wodclipse.core.completion.WodParserCache;

public class TBStringTagDelegate extends TagDelegate {
	private boolean _cssAdded;
	
	@Override
	public void renderNode(IWodElement wodElement, FuzzyXMLElement xmlElement, RenderContext renderContext, StringBuffer htmlBuffer, StringBuffer cssBuffer, Stack<WodParserCache> caches, Stack<FuzzyXMLNode> nodes) {
		IWodBinding valueBinding = wodElement.getBindingNamed("value");
		if (valueBinding.isKeyPath()) {
			htmlBuffer.append("<span class = \"wodclipse_TBString\">[" + valueBinding.getValue() + "]</span>");
		} else {
			htmlBuffer.append(valueBinding.getValue());
		}
		
		if (!_cssAdded) {
			cssBuffer.append("span.wodclipse_TBString {");
			cssBuffer.append("  color: red;");
			cssBuffer.append("}");
			_cssAdded = true;
		}
	}

	@Override
	public void reset() {
		_cssAdded = false;
	}
}
