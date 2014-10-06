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

import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.Position;
import org.objectstyle.wolips.bindings.wod.AbstractWodBinding;
import org.objectstyle.wolips.wodclipse.core.Activator;
import org.objectstyle.wolips.wodclipse.core.parser.RulePosition;

/**
 * @author mschrag
 */
public class DocumentWodBinding extends AbstractWodBinding {
  private RulePosition _namespace;

  private RulePosition _name;

  private RulePosition _valueNamespace;

  private RulePosition _value;

  private int _lineNumber;

  public DocumentWodBinding(RulePosition namespace, RulePosition name, RulePosition valueNamespace, RulePosition value) {
    _namespace = namespace;
    _name = name;
    _valueNamespace = valueNamespace;
    _value = value;
    _lineNumber = -1;
  }

  public RulePosition getNamespaceRulePosition() {
    return _namespace;
  }

  public RulePosition getNameRulePosition() {
    return _name;
  }

  public String getNamespace() {
    return (_namespace == null) ? null : _namespace._getTextWithoutException();
  }

  public Position getNamespacePosition() {
    return (_namespace == null) ? null : _namespace.getPosition();
  }

  public String getName() {
    return _name._getTextWithoutException();
  }

  public Position getNamePosition() {
    return _name.getPosition();
  }
  
  public String getValueNamespace() {
    return _valueNamespace == null ? null : _valueNamespace._getTextWithoutException();
  }

  public Position getValueNamespacePosition() {
    return _valueNamespace == null ? null : _valueNamespace.getPosition();
  }

  public String getValue() {
    return _value._getTextWithoutException();
  }

  public Position getValuePosition() {
    return _value.getPosition();
  }

  public int getStartOffset() {
    int startOffset;
    if (_namespace == null) {
      startOffset = _name.getTokenOffset();
    }
    else {
      startOffset = _namespace.getTokenEndOffset();
    }
    return startOffset;
  }

  public int getEndOffset() {
    return _value.getTokenEndOffset();
  }

  @Override
  public int getLineNumber() {
    int lineNumber = _lineNumber;
    if (lineNumber == -1) {
      try {
        lineNumber = _name.getDocument().getLineOfOffset(getStartOffset());
      }
      catch (BadLocationException e) {
        Activator.getDefault().log(e);
      }
    }
    return lineNumber;
  }
}
