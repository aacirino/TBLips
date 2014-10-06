/*
 * ====================================================================
 * 
 * The ObjectStyle Group Software License, Version 1.0
 * 
 * Copyright (c) 2005 The ObjectStyle Group and individual authors of the
 * software. All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met: 1.
 * Redistributions of source code must retain the above copyright notice, this
 * list of conditions and the following disclaimer. 2. Redistributions in
 * binary form must reproduce the above copyright notice, this list of
 * conditions and the following disclaimer in the documentation and/or other
 * materials provided with the distribution. 3. The end-user documentation
 * included with the redistribution, if any, must include the following
 * acknowlegement: "This product includes software developed by the ObjectStyle
 * Group (http://objectstyle.org/)." Alternately, this acknowlegement may
 * appear in the software itself, if and wherever such third-party
 * acknowlegements normally appear. 4. The names "ObjectStyle Group" and
 * "Cayenne" must not be used to endorse or promote products derived from this
 * software without prior written permission. For written permission, please
 * contact andrus@objectstyle.org. 5. Products derived from this software may
 * not be called "ObjectStyle" nor may "ObjectStyle" appear in their names
 * without prior written permission of the ObjectStyle Group.
 * 
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED WARRANTIES,
 * INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND
 * FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE
 * OBJECTSTYLE GROUP OR ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT,
 * INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA,
 * OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE,
 * EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 * ====================================================================
 * 
 * This software consists of voluntary contributions made by many individuals
 * on behalf of the ObjectStyle Group. For more information on the ObjectStyle
 * Group, please see <http://objectstyle.org/> .
 *  
 */
package org.objectstyle.wolips.wodclipse.core.document;

import java.util.Iterator;
import java.util.List;

import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.Position;
import org.objectstyle.wolips.bindings.wod.AbstractWodElement;
import org.objectstyle.wolips.bindings.wod.IWodBinding;
import org.objectstyle.wolips.wodclipse.core.Activator;
import org.objectstyle.wolips.wodclipse.core.parser.RulePosition;

/**
 * @author mschrag
 */
public class DocumentWodElement extends AbstractWodElement {
  private RulePosition _elementName;

  private RulePosition _elementType;
  
  private int _endOffset;

  private int _lineNumber;

  public DocumentWodElement(RulePosition elementName, RulePosition elementType) {
    _elementName = elementName;
    _elementType = elementType;
    _lineNumber = -1;
    _endOffset = -1;
  }

  public String getElementName() {
    return _elementName._getTextWithoutException();
  }

  public Position getElementNamePosition() {
    return _elementName.getPosition();
  }

  public String getElementType() {
    return _elementType._getTextWithoutException();
  }

  public Position getElementTypePosition() {
    return _elementType.getPosition();
  }

  public int getNewBindingOffset() {
    int newBindingOffset = getEndOffset() - 1;
    return newBindingOffset;
  }

  public int getNewBindingIndent() {
    int indent = 2;
    List<IWodBinding> bindings = getBindings();
    if (bindings.size() > 0) {
      DocumentWodBinding lastBinding = (DocumentWodBinding)bindings.get(bindings.size() - 1);
      RulePosition nameRulePosition = lastBinding.getNameRulePosition();
      if (nameRulePosition != null) {
        int startOffset = lastBinding.getStartOffset();
        try {
          IRegion lineInformation = nameRulePosition.getDocument().getLineInformationOfOffset(startOffset);
          indent = startOffset - lineInformation.getOffset();
        }
        catch (BadLocationException e) {
          e.printStackTrace();
        }
      }
      
    }
    return indent;
  }

  public int getStartOffset() {
    return _elementName.getTokenOffset();
  }

  public int getEndOffset() {
    int endOffset = _endOffset;
    if (endOffset == -1) {
      endOffset = _elementType.getTokenEndOffset();
      Iterator<IWodBinding> bindingsIter = getBindings().iterator();
      while (bindingsIter.hasNext()) {
        IWodBinding binding = bindingsIter.next();
        endOffset = Math.max(endOffset, binding.getEndOffset());
      }
    }
    return endOffset;
  }
  
  public int getFullEndOffset() {
    return getEndOffset();
  }
  
  public void setEndOffset(int endOffset) {
    _endOffset = endOffset;
  }

  @Override
  public int getLineNumber() {
    int lineNumber = _lineNumber;
    if (lineNumber == -1) {
      try {
        lineNumber = _elementName.getDocument().getLineOfOffset(_elementName.getTokenOffset());
      }
      catch (BadLocationException e) {
        Activator.getDefault().log(e);
      }
    }
    return lineNumber;
  }
}
