/* ====================================================================
 * 
 * The ObjectStyle Group Software License, Version 1.0 
 *
 * Copyright (c) 2004 - 2006 The ObjectStyle Group 
 * and individual authors of the software.  All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer. 
 *
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the
 *    distribution.
 *
 * 3. The end-user documentation included with the redistribution, if
 *    any, must include the following acknowlegement:  
 *       "This product includes software developed by the 
 *        ObjectStyle Group (http://objectstyle.org/)."
 *    Alternately, this acknowlegement may appear in the software itself,
 *    if and wherever such third-party acknowlegements normally appear.
 *
 * 4. The names "ObjectStyle Group" and "Cayenne" 
 *    must not be used to endorse or promote products derived
 *    from this software without prior written permission. For written 
 *    permission, please contact andrus@objectstyle.org.
 *
 * 5. Products derived from this software may not be called "ObjectStyle"
 *    nor may "ObjectStyle" appear in their names without prior written
 *    permission of the ObjectStyle Group.
 *
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED.  IN NO EVENT SHALL THE OBJECTSTYLE GROUP OR
 * ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF
 * USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 * ====================================================================
 *
 * This software consists of voluntary contributions made by many
 * individuals on behalf of the ObjectStyle Group.  For more
 * information on the ObjectStyle Group, please see
 * <http://objectstyle.org/>.
 *
 */

package org.objectstyle.wolips.goodies.core.mac;

import java.io.File;
import java.io.IOException;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;

import org.eclipse.core.resources.IResource;

/**
 * @author ulrich
 * @author Mike Schrag
 * @author q
 */
public class NativeHelper {
	public static void revealInFileBrowser(IResource resource) {
		try {
			String resourcePath = resource.getLocation().toOSString().replaceAll(" ", "\\ ");
			String openInFinder = "tell application \"Finder\"\n reveal POSIX file \"" + resourcePath + "\"\n activate\nend tell";
			executeAppleScript(openInFinder);
		} catch (Throwable t) {
			t.printStackTrace();
		}
	}

	public static void cdInShell(IResource resource) {
		try {
	        String containerPath = getParentOfResource(resource).replaceAll(" ", "\\ ");
			String openInTerminalString = "tell application \"Terminal\"\n do script \"cd \\\"" + containerPath + "\\\"\"\n activate\nend tell";
			executeAppleScript(openInTerminalString);
		} catch (Throwable t) {
			t.printStackTrace();
		}
	}

	private static String getParentOfResource(IResource resource) {
        File file = resource.getLocation().toFile();
        File path = null;
        if (file != null) {
            if (!file.isDirectory() && file.getParentFile() != null) {
                path = file.getParentFile();
            } else {
                path = file;
            }
        }
        return path.getPath();
	}
	
	private static void _executeAppleScript(String script) {
		Runtime runtime = Runtime.getRuntime();
		String[] args = { "/usr/bin/osascript", "-e", script };
		try {
			runtime.exec(args);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static void executeAppleScript(String script) {
		try {
			ScriptEngineManager mgr = new ScriptEngineManager();
			ScriptEngine engine = mgr.getEngineByName("AppleScript");
			engine.eval(script);
		} catch (Throwable e) {
			_executeAppleScript(script);
		}
	}
}