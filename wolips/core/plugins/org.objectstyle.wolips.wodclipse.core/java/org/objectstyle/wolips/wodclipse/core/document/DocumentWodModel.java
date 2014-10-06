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
import java.util.LinkedList;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.Position;
import org.eclipse.jface.text.rules.WhitespaceRule;
import org.objectstyle.wolips.bindings.wod.AbstractWodModel;
import org.objectstyle.wolips.bindings.wod.IWodBinding;
import org.objectstyle.wolips.bindings.wod.IWodElement;
import org.objectstyle.wolips.bindings.wod.IWodModel;
import org.objectstyle.wolips.bindings.wod.IWodUnit;
import org.objectstyle.wolips.bindings.wod.SimpleWodElement;
import org.objectstyle.wolips.bindings.wod.WodElementProblem;
import org.objectstyle.wolips.bindings.wod.WodProblem;
import org.objectstyle.wolips.wodclipse.core.Activator;
import org.objectstyle.wolips.wodclipse.core.completion.WodCacheEntry;
import org.objectstyle.wolips.wodclipse.core.completion.WodParserCache;
import org.objectstyle.wolips.wodclipse.core.parser.AssignmentOperatorWordDetector;
import org.objectstyle.wolips.wodclipse.core.parser.BindingNameRule;
import org.objectstyle.wolips.wodclipse.core.parser.BindingValueNamespaceRule;
import org.objectstyle.wolips.wodclipse.core.parser.BindingValueRule;
import org.objectstyle.wolips.wodclipse.core.parser.CloseDefinitionWordDetector;
import org.objectstyle.wolips.wodclipse.core.parser.ElementNameRule;
import org.objectstyle.wolips.wodclipse.core.parser.ElementTypeOperatorWordDetector;
import org.objectstyle.wolips.wodclipse.core.parser.ElementTypeRule;
import org.objectstyle.wolips.wodclipse.core.parser.EndAssignmentWordDetector;
import org.objectstyle.wolips.wodclipse.core.parser.ICommentRule;
import org.objectstyle.wolips.wodclipse.core.parser.OpenDefinitionWordDetector;
import org.objectstyle.wolips.wodclipse.core.parser.RulePosition;
import org.objectstyle.wolips.wodclipse.core.parser.StringLiteralRule;
import org.objectstyle.wolips.wodclipse.core.parser.WOOGNLRule;
import org.objectstyle.wolips.wodclipse.core.parser.WodScanner;

/**
 * @author mschrag
 */
public class DocumentWodModel extends AbstractWodModel {
  private IFile _wodFile;

  private IDocument _document;

  public DocumentWodModel(IFile wodFile, IDocument document) {
    _wodFile = wodFile;
    _document = document;
    parse();
  }

  public IDocument getDocument() {
    return _document;
  }

  public void addParseProblem(IWodElement element, String message, RulePosition rulePosition, boolean warning) {
    Position position = rulePosition.getPosition();
    try {
      int lineNumber = _document.getLineOfOffset(position.getOffset());
      WodProblem problem;
      if (element != null) {
        problem = new WodElementProblem(element, message, position, lineNumber, warning);
      }
      else {
        problem = new WodProblem(message, position, lineNumber, warning);
      }
      addParseProblem(problem);
    }
    catch (BadLocationException e) {
      Activator.getDefault().log(e);
    }
  }

  // MS: all these savedRulePositions should be switched to instead use the rulePositions stack ...
  protected synchronized void parse() {
    clear();

    WodScanner scanner = WodScanner.wodScannerForDocument(_document);
    DocumentWodBinding lastBinding = null;
    DocumentWodElement element = null;
    RulePosition tentativeElementName = null;
    RulePosition savedRulePosition = null;
    RulePosition savedRulePosition2 = null;
    RulePosition savedRulePosition3 = null;
    RulePosition lastRulePosition = null;
    ForgivingStack<RulePosition> rulePositions = new ForgivingStack<RulePosition>();
    // boolean stringLiteralIsABindingName = false;
    RulePosition rulePosition;
    while ((rulePosition = scanner.nextRulePosition()) != null) {
      boolean whitespace = false;
      boolean comment = false;
      if (RulePosition.isRulePositionOfType(rulePosition, WhitespaceRule.class)) {
        whitespace = true;
      }
      else if (RulePosition.isRulePositionOfType(rulePosition, ICommentRule.class)) {
        comment = true;
        if (lastBinding != null) {
          String commentText = rulePosition._getTextWithoutException();
          if (commentText != null && commentText.startsWith("//")) {
            commentText = commentText.substring(2).trim();
            if ("VALID".equalsIgnoreCase(commentText)) {
              lastBinding.setValidate(false);
            }
          }
        }
        else {
          String commentText = rulePosition._getTextWithoutException();
          if (commentText != null && commentText.startsWith("//")) {
            commentText = commentText.substring(2).trim();
            if (commentText.toLowerCase().startsWith("inherit ")) {
            	String componentName = commentText.substring("inherit ".length()).trim();
            	try {
            		WodParserCache inheritCache = WodParserCache.parser(_wodFile.getProject(), componentName);
            		WodCacheEntry wodCacheEntry = inheritCache.getWodEntry();
            		IWodModel parentWodModel = wodCacheEntry.getModel();
            		for (IWodElement parentWodElement : parentWodModel.getElements()) {
            			SimpleWodElement inheritedWodElement = new SimpleWodElement(parentWodElement);
            			inheritedWodElement.setInherited(true);
            			addElement(inheritedWodElement);
            		}
            	}
            	catch (Throwable t) {
                addParseProblem(element, "WOD inheritance of '" + componentName + "' failed: " + t.getMessage() + ".", rulePosition, false);
            	}
            }
          }
        }
      }
      else if (RulePosition.isRulePositionOfType(rulePosition, ElementNameRule.class)) {
        if (RulePosition.isOperatorOfType(lastRulePosition, OpenDefinitionWordDetector.class) || RulePosition.isOperatorOfType(lastRulePosition, EndAssignmentWordDetector.class)) {
          savedRulePosition2 = rulePosition;
          if (lastRulePosition != null && !RulePosition.isOperatorOfType(lastRulePosition, CloseDefinitionWordDetector.class)) {
            tentativeElementName = rulePosition;
          }
          // leave old savedRulePosition
        }
        else {
          if (lastRulePosition != null && !RulePosition.isOperatorOfType(lastRulePosition, CloseDefinitionWordDetector.class)) {
            addParseProblem(element, "The element name '" + rulePosition._getTextWithoutException() + "' must start a WOD declaration", rulePosition, false);
          }
          savedRulePosition = rulePosition;
          element = null;
        }
      }
      else if (RulePosition.isOperatorOfType(rulePosition, ElementTypeOperatorWordDetector.class)) {
        if (!RulePosition.isRulePositionOfType(lastRulePosition, ElementNameRule.class) && !RulePosition.isRulePositionOfType(lastRulePosition, BindingValueNamespaceRule.class)) {
          addParseProblem(element, "A ':' can only appear after an element name or a binding value namespace.", rulePosition, false);
        }
      }
      else if (RulePosition.isRulePositionOfType(rulePosition, ElementTypeRule.class)) {
        if (tentativeElementName != null) {
       		addParseProblem(element, "The element name ' " + tentativeElementName._getTextWithoutException() + "' must start a WOD declaration", tentativeElementName, false);
          tentativeElementName = null;
          savedRulePosition = null;
        }
        else if (!RulePosition.isOperatorOfType(lastRulePosition, ElementTypeOperatorWordDetector.class)) {
          addParseProblem(element, "The element type '" + rulePosition._getTextWithoutException() + "' can only appear after a ':'", rulePosition, false);
        }
        else {
        	rulePositions.clear();
          element = new DocumentWodElement(savedRulePosition, rulePosition);
          addElement(element);
          savedRulePosition = null;
        }
      }
      else if (RulePosition.isOperatorOfType(rulePosition, OpenDefinitionWordDetector.class)) {
        if (!RulePosition.isRulePositionOfType(lastRulePosition, ElementTypeRule.class)) {
          addParseProblem(element, "A '{' can only appear after an element type", rulePosition, false);
        }
      }
      else if (RulePosition.isRulePositionOfType(rulePosition, WOOGNLRule.class)) {
        boolean ognlIsValue = RulePosition.isOperatorOfType(lastRulePosition, AssignmentOperatorWordDetector.class);
        boolean ognlIsName = !ognlIsValue && (RulePosition.isOperatorOfType(lastRulePosition, EndAssignmentWordDetector.class) || RulePosition.isOperatorOfType(lastRulePosition, OpenDefinitionWordDetector.class));
        if (!ognlIsValue && !ognlIsName) {
          addParseProblem(element, "The OGNL value " + rulePosition._getTextWithoutException() + " can only appear after a '{', '=', or ';'.", rulePosition, false);
          savedRulePosition = null;
        }
        else if (ognlIsName) {
          savedRulePosition = rulePosition;
        }
        else if (ognlIsValue) {
          lastBinding = addBinding(element, savedRulePosition2, savedRulePosition, null, rulePosition, scanner);
          savedRulePosition = null;
          savedRulePosition2 = null;
          savedRulePosition3 = null;
        }
      }
      else if (RulePosition.isRulePositionOfType(rulePosition, StringLiteralRule.class)) {
        boolean literalIsValue = RulePosition.isOperatorOfType(lastRulePosition, AssignmentOperatorWordDetector.class);
        boolean literalIsName = !literalIsValue && (RulePosition.isOperatorOfType(rulePositions.peek(), EndAssignmentWordDetector.class) || RulePosition.isOperatorOfType(rulePositions.peek(), OpenDefinitionWordDetector.class));
        if (!literalIsValue && !literalIsName) {
          addParseProblem(element, "The string literal '" + rulePosition._getTextWithoutException() + "' can only appear after a '{', '=', or ';'.", rulePosition, false);
          savedRulePosition = null;
        }
        else if (literalIsName) {
          savedRulePosition = rulePosition;
        }
        else if (literalIsValue) {
          lastBinding = addBinding(element, savedRulePosition2, savedRulePosition, null, rulePosition, scanner);
          savedRulePosition = null;
          savedRulePosition2 = null;
          savedRulePosition3 = null;
        }
        tentativeElementName = null;
      }
      else if (RulePosition.isRulePositionOfType(rulePosition, BindingNameRule.class)) {
        if (!RulePosition.isOperatorOfType(lastRulePosition, OpenDefinitionWordDetector.class) && !RulePosition.isOperatorOfType(lastRulePosition, EndAssignmentWordDetector.class) && !RulePosition.isOperatorOfType(lastRulePosition, ElementTypeOperatorWordDetector.class)) {
          addParseProblem(element, "The binding name '" + rulePosition._getTextWithoutException() + "' can only appear after a '{' or a ';'", rulePosition, false);
        }
        savedRulePosition = rulePosition;
        lastBinding = null;
      }
      else if (RulePosition.isOperatorOfType(rulePosition, AssignmentOperatorWordDetector.class)) {
        if (!RulePosition.isRulePositionOfType(lastRulePosition, BindingNameRule.class) && !RulePosition.isRulePositionOfType(lastRulePosition, StringLiteralRule.class)) {
          addParseProblem(element, "An '=' can only appear after a binding name", rulePosition, false);
        }
      }
      else if (RulePosition.isRulePositionOfType(rulePosition, BindingValueNamespaceRule.class)) {
        if (!RulePosition.isOperatorOfType(lastRulePosition, AssignmentOperatorWordDetector.class)) {
          addParseProblem(element, "The binding value namespace '" + rulePosition._getTextWithoutException() + "' can only appear after an '='", rulePosition, false);
        }
        else {
          savedRulePosition3 = rulePosition;
        }
      }
      else if (RulePosition.isRulePositionOfType(rulePosition, BindingValueRule.class)) {
        if (!RulePosition.isOperatorOfType(lastRulePosition, AssignmentOperatorWordDetector.class) && !(RulePosition.isOperatorOfType(lastRulePosition, ElementTypeOperatorWordDetector.class) && RulePosition.isRulePositionOfType(savedRulePosition3, BindingValueNamespaceRule.class))) {
          addParseProblem(element, "The binding value '" + rulePosition._getTextWithoutException() + "' can only appear after an '=' or a 'xxx:'", rulePosition, false);
        }
        else {
          lastBinding = addBinding(element, savedRulePosition2, savedRulePosition, savedRulePosition3, rulePosition, scanner);
        }
        savedRulePosition = null;
        savedRulePosition2 = null;
        savedRulePosition3 = null;
      }
      else if (RulePosition.isOperatorOfType(rulePosition, EndAssignmentWordDetector.class)) {
        if (!RulePosition.isRulePositionOfType(lastRulePosition, BindingValueRule.class) && !RulePosition.isRulePositionOfType(lastRulePosition, StringLiteralRule.class) && !RulePosition.isRulePositionOfType(lastRulePosition, WOOGNLRule.class)) {
          addParseProblem(element, "A ';' can only appear after a binding value", rulePosition, false);
        }
      }
      else if (RulePosition.isOperatorOfType(rulePosition, CloseDefinitionWordDetector.class)) {
        if (element != null) {
          element.setEndOffset(rulePosition.getTokenOffset() + 1);
        }
        if (!RulePosition.isOperatorOfType(lastRulePosition, OpenDefinitionWordDetector.class) && !RulePosition.isOperatorOfType(lastRulePosition, EndAssignmentWordDetector.class) && !RulePosition.isRulePositionOfType(lastRulePosition, BindingValueRule.class) && !RulePosition.isRulePositionOfType(lastRulePosition, StringLiteralRule.class) && !RulePosition.isRulePositionOfType(lastRulePosition, WOOGNLRule.class)) {
          addParseProblem(element, "A '}' can only appear after a ';' or a '{'", rulePosition, false);
        }
        else {
          element = null;
        }
        lastBinding = null;
      }
      else {
        addParseProblem(element, "'" + rulePosition._getTextWithoutException() + "' is an unknown keyword", rulePosition, false);
      }

      if (!whitespace && !comment) {
        lastRulePosition = rulePosition;
      	rulePositions.push(rulePosition);
      }
    }

    if (lastRulePosition != null && !RulePosition.isOperatorOfType(lastRulePosition, CloseDefinitionWordDetector.class)) {
      addParseProblem(element, "The last entry in a WOD file must be a '}'.", lastRulePosition, false);
    }
  }

  protected DocumentWodBinding addBinding(DocumentWodElement element, RulePosition namespaceRulePosition, RulePosition nameRulePosition, RulePosition valueNamespaceRulePosition, RulePosition valueRulePosition, WodScanner scanner) {
    DocumentWodBinding binding = null;
    if (element == null) {
      addParseProblem(element, "A binding must appear in a declaration", valueRulePosition, false);
    }
    else if (nameRulePosition == null) {
      addParseProblem(element, "A binding must have a name", valueRulePosition, false);
    }
    else if (valueRulePosition == null) {
      addParseProblem(element, "A binding must have a value", valueRulePosition, false);
    }
    else {
      binding = new DocumentWodBinding(namespaceRulePosition, nameRulePosition, valueNamespaceRulePosition, valueRulePosition);
      element.addBinding(binding);
    }
    return binding;
  }

  public String getName() {
    return _wodFile.getName();
  }

  public IFile getWodFile() {
    return _wodFile;
  }

  public int getStartOffset() {
    return 0;
  }

  public int getEndOffset() {
    return _document.getLength();
  }

  public IWodElement getWodElementAtIndex(int index) {
    IWodElement elementAtIndex = null;
    Iterator<IWodElement> elementsIter = getElements().iterator();
    while (elementAtIndex == null && elementsIter.hasNext()) {
      IWodElement element = elementsIter.next();
      if (isIndexContainedByWodUnit(index, element)) {
        elementAtIndex = element;
      }
    }
    return elementAtIndex;
  }

  public IWodUnit getWodUnitAtIndex(int index) {
    IWodUnit wodUnit = null;

    IWodElement elementAtIndex = getWodElementAtIndex(index);
    if (elementAtIndex != null) {
      Iterator<IWodBinding> bindingsIter = elementAtIndex.getBindings().iterator();
      while (wodUnit == null && bindingsIter.hasNext()) {
        IWodBinding binding = bindingsIter.next();
        if (isIndexContainedByWodUnit(index, binding)) {
          wodUnit = binding;
        }
      }
      if (wodUnit == null) {
        wodUnit = elementAtIndex;
      }
    }

    if (wodUnit == null) {
      wodUnit = this;
    }

    return wodUnit;
  }

  protected boolean isIndexContainedByWodUnit(int index, IWodUnit wodUnit) {
    return index >= wodUnit.getStartOffset() && index <= wodUnit.getEndOffset();
  }

  protected static class ForgivingStack<T> {
  	private List<T> _contents;
  	
  	public ForgivingStack() {
  		_contents = new LinkedList<T>();
  	}
  	
  	public void push(T obj) {
  		_contents.add(0, obj);
  	}
  	
  	public void clear() {
  		_contents.clear();
  	}
  	
  	public T peek() {
  		return peek(0);
  	}
  	
  	public T peek(int offset) {
  		T obj = null;
  		if (_contents.size() > offset) {
  			obj = _contents.get(offset);
  		}
  		return obj;
  	}
  }
}
