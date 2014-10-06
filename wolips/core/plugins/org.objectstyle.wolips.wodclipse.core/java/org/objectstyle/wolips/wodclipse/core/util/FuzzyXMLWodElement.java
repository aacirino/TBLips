package org.objectstyle.wolips.wodclipse.core.util;

import java.util.Map;

import jp.aonir.fuzzyxml.FuzzyXMLAttribute;
import jp.aonir.fuzzyxml.FuzzyXMLElement;

import org.eclipse.jface.text.Position;
import org.objectstyle.wolips.bindings.api.ApiCache;
import org.objectstyle.wolips.bindings.wod.SimpleWodBinding;
import org.objectstyle.wolips.bindings.wod.SimpleWodElement;
import org.objectstyle.wolips.bindings.wod.TagShortcut;
import org.objectstyle.wolips.variables.BuildProperties;
import org.objectstyle.wolips.wodclipse.core.util.WodHtmlUtils.BindingValue;

public class FuzzyXMLWodElement extends SimpleWodElement {
  private FuzzyXMLElement _xmlElement;

  public FuzzyXMLWodElement(FuzzyXMLElement element, BuildProperties buildProperties) {
    String elementName = element.getName();
    String namespaceElementName = elementName.substring("wo:".length()).trim();
    int elementTypePosition = element.getOffset() + element.getNameOffset() + "wo:".length() + 1;
    int elementTypeLength = namespaceElementName.length();

    TagShortcut matchingTagShortcut = null;
    for (TagShortcut tagShortcut : ApiCache.getTagShortcuts()) {
      if (namespaceElementName.equalsIgnoreCase(tagShortcut.getShortcut())) {
        matchingTagShortcut = tagShortcut;
      }
    }
    if (matchingTagShortcut != null) {
      namespaceElementName = matchingTagShortcut.getActual();
    }

    _setElementName("_temp");
    _setElementType(namespaceElementName);
    setElementTypePosition(new Position(elementTypePosition, elementTypeLength));
    setInline(true);

    if (matchingTagShortcut != null) {
      for (Map.Entry<String, String> shortcutAttribute : matchingTagShortcut.getAttributes().entrySet()) {
        BindingValue value = WodHtmlUtils.toBindingValue(shortcutAttribute.getValue(), buildProperties.getInlineBindingPrefix(), buildProperties.getInlineBindingSuffix());
        SimpleWodBinding wodBinding = new SimpleWodBinding(null, shortcutAttribute.getKey(), value.getValue());
        addBinding(wodBinding);
      }
    }

    FuzzyXMLAttribute[] attributes = element.getAttributes();
    for (FuzzyXMLAttribute attribute : attributes) {
      String namespace = attribute.getNamespace();
      String name = attribute.getName();
      String originalValue = attribute.getValue();
      BindingValue value = WodHtmlUtils.toBindingValue(originalValue, buildProperties.getInlineBindingPrefix(), buildProperties.getInlineBindingSuffix());
      Position valuePosition;
      Position valueNamespacePosition = null;
      if (value.getValueNamespace() != null) {
        int valueNamespaceOffset = originalValue.indexOf(value.getValueNamespace());
        valueNamespacePosition = new Position(element.getOffset() + attribute.getValueDataOffset() + valueNamespaceOffset + 1, value.getValueNamespace().length());
        int valueOffset = originalValue.indexOf(value.getValue(), valueNamespaceOffset + value.getValueNamespace().length());
        valuePosition = new Position(valueNamespacePosition.offset + valueOffset, attribute.getValueDataLength() - valueOffset);
      }
      else {
        valuePosition = new Position(element.getOffset() + attribute.getValueDataOffset() + 1, attribute.getValueDataLength());
      }
      
      SimpleWodBinding wodBinding = new SimpleWodBinding(namespace, name, value.getValueNamespace(), value.getValue(), new Position(attribute.getNamespaceOffset(), attribute.getNamespaceLength()), new Position(attribute.getNameOffset(), attribute.getNameLength()), valueNamespacePosition, valuePosition, -1);
      wodBinding.setStartOffset(attribute.getOffset());
      wodBinding.setEndOffset(attribute.getOffset() + attribute.getLength());
      addBinding(wodBinding);
    }
    
    setStartOffset(element.getOffset());
    setEndOffset(element.getOffset() + element.getOpenTagLength() + 2);
    if (element.hasCloseTag()) {
      setFullEndOffset(element.getCloseTagOffset() + element.getCloseTagLength() + 2);
    }
    else {
      setFullEndOffset(getEndOffset());
    }
    if (attributes.length > 0) {
      int endOffset = attributes[attributes.length - 1].getOffset() + attributes[attributes.length - 1].getLength();
      setNewBindingOffset(endOffset);
    }
    else {
      setNewBindingOffset(element.getOffset() + element.getNameOffset() + element.getNameLength() + 1);
    }
    
    setNewBindingIndent(1);
  }

  public FuzzyXMLElement getXmlElement() {
    return _xmlElement;
  }
}
