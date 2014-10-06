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
package org.objectstyle.wolips.wodclipse.core.parser;

import org.eclipse.jface.text.rules.ICharacterScanner;
import org.eclipse.jface.text.rules.IPredicateRule;
import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.Token;

/**
 * @author mike
 */
public class SingleWordRule implements IPredicateRule {
	private IToken _token;

	private char[] _acceptableCharacters;

	private char _unacceptablePrefixCharacter;
	
	private char _stopCharacter;
	
	private char _stopCharacter2;

  public SingleWordRule(IToken token, char[] acceptableCharacters, char stopCharacter) {
    this(token, (char)0, acceptableCharacters, stopCharacter);
  }
  
	public SingleWordRule(IToken token, char unacceptablePrefixCharacter, char[] acceptableCharacters, char stopCharacter) {
		this(token, unacceptablePrefixCharacter, acceptableCharacters, stopCharacter, (char)0);
	}
  
	public SingleWordRule(IToken token, char unacceptablePrefixCharacter, char[] acceptableCharacters, char stopCharacter, char stopCharacter2) {
		_token = token;
		_unacceptablePrefixCharacter = unacceptablePrefixCharacter;
		_acceptableCharacters = acceptableCharacters;
		_stopCharacter = stopCharacter;
		_stopCharacter2 = stopCharacter2;
	}

	public IToken getSuccessToken() {
		return _token;
	}

	public IToken evaluate(ICharacterScanner scanner) {
		return evaluate(scanner, false);
	}

	public IToken evaluate(ICharacterScanner scanner, boolean resume) {
		IToken token = Token.UNDEFINED;
		boolean acceptable = true;
		
		int whitespaceCount = 0;
		int unreadCount = 0;
		int wordCount = 0;
		
		int ch;
		if (_unacceptablePrefixCharacter != 0 && scanner.getColumn() > 0) {
		  ch = 0;
		  int readCount = 0;
		  while (scanner.getColumn() > 0 && ch != -1) {
		    scanner.unread();
	      ch = scanner.read();
	      if (Character.isWhitespace(ch)) {
	        scanner.unread();
	        readCount ++;
	      }
	      else {
	        break;
	      }
		  }
		  for (int i = 0; i < readCount; i ++) {
		    scanner.read();
		  }
      if (ch == _unacceptablePrefixCharacter) {
        acceptable = false;
      }
		}

		if (acceptable) {
  		while ((ch = scanner.read()) != ICharacterScanner.EOF) {
  			unreadCount++;
  			if (ch == _stopCharacter || (_stopCharacter2 != 0 && ch == _stopCharacter2)) {
  				token = _token;
  				scanner.unread();
  				break;
  			} else if (Character.isWhitespace((char) ch)) {
  				whitespaceCount++;
  			} else if (isAcceptableCharacter((char) ch, unreadCount - whitespaceCount)) {
  				if ((wordCount == 0 || whitespaceCount > 0) && (++wordCount >= 2) && !isMultiWordAllowed()) {
  					break;
  				}
  				whitespaceCount = 0;
  			} else {
  				break;
  			}
  		}
  
  		if (token == _token) {
  			unreadCount = whitespaceCount;
  		}
  		if (ch == ICharacterScanner.EOF) {
  			unreadCount++;
  		}
  		for (int i = 0; i < unreadCount; i++) {
  			scanner.unread();
  		}
		}

		return token;
	}

	protected boolean isMultiWordAllowed() {
		return false;
	}
	
	protected boolean isAcceptableCharacter(char ch, int index) {
		boolean acceptableCharacter = Character.isJavaIdentifierPart(ch);
		for (int i = 0; !acceptableCharacter && i < _acceptableCharacters.length; i++) {
			acceptableCharacter = (_acceptableCharacters[i] == ch);
		}
		return acceptableCharacter;
	}
}
