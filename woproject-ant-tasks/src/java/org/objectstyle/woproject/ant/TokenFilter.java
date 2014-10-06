/* ====================================================================
 * 
 * The ObjectStyle Group Software License, Version 1.0 
 *
 * Copyright (c) 2002 The ObjectStyle Group 
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
package org.objectstyle.woproject.ant;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Writer;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.tools.ant.BuildException;

/**
 * Line-based writer that does Ant-style token replacement.
 * 
 * @author andrus
 */
class TokenFilter {

	private Map tokens;

	private String lineEnd;

	TokenFilter(Map tokens) {
		this.lineEnd = System.getProperty("line.separator");

		// compile tokens
		this.tokens = new HashMap(tokens.size() * 2);
		Iterator it = tokens.entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry entry = (Map.Entry) it.next();
			this.tokens.put(Pattern.compile(entry.getKey().toString()), entry.getValue());
		}
	}

	void copy(String fromResource, File to) throws BuildException {
		try {

			InputStream in = getClass().getClassLoader().getResourceAsStream(fromResource);

			if (in == null) {
				throw new IOException("Resource not found: " + fromResource);
			}

			FileUtil.ensureParentDirExists(to);
			Writer out = new FileWriter(to);

			try {
				BufferedReader reader = new BufferedReader(new InputStreamReader(in));
				String line;
				while ((line = reader.readLine()) != null) {
					writeLine(out, line);
				}
			} finally {
				try {
					out.close();
				} catch (IOException ioex) {
					// ignore
				}

				try {
					in.close();
				} catch (IOException ioex) {
					// ignore
				}
			}
		} catch (IOException e) {
			throw new BuildException("Error copying resource " + fromResource, e);
		}
	}

	void writeLine(Writer out, String line) throws IOException {

		if (line.length() > 0) {

			StringBuffer buffer = new StringBuffer(line);

			// not very efficient - may need to rewrite
			Iterator it = tokens.entrySet().iterator();
			while (it.hasNext()) {
				Map.Entry entry = (Map.Entry) it.next();

				Pattern token = (Pattern) entry.getKey();
				String replacement = entry.getValue().toString();

				// can't use Pattern.replaceAll as it will pre-process
				// replacement strings breaking things like backslashes

				Matcher matcher = token.matcher(buffer);
				int start = 0;
				while (matcher.find(start)) {
					buffer.replace(matcher.start(), matcher.end(), replacement);
					start = matcher.start() + replacement.length();
				}
			}

			out.write(buffer.toString());
		} else {
			out.write(line);
		}

		out.write(lineEnd);
	}
}
