/* ====================================================================
 *
 * The ObjectStyle Group Software License, Version 1.0
 *
 * Copyright (c) 2006 The ObjectStyle Group
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

package org.objectstyle.wolips.launching.browsersupport;

import java.net.MalformedURLException;
import java.net.URL;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.ui.console.IConsole;
import org.eclipse.debug.ui.console.IConsoleLineTracker;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IRegion;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.browser.IWebBrowser;
import org.eclipse.ui.browser.IWorkbenchBrowserSupport;
import org.eclipse.ui.internal.browser.WebBrowserUIPlugin;
import org.objectstyle.wolips.launching.LaunchingPlugin;
import org.objectstyle.wolips.launching.delegates.WOJavaLocalApplicationLaunchConfigurationDelegate;

public class ConsoleLineTracker implements IConsoleLineTracker {

	private boolean urlFound;

	private IConsole currentConsole;

	private boolean webserverURLFound;

	private boolean webserverConnect;

	public ConsoleLineTracker() {
		super();
	}

	public void init(IConsole console) {
		// process type
		String openBrowser = null;
		try {
			openBrowser = console.getProcess().getLaunch().getLaunchConfiguration().getAttribute(WOJavaLocalApplicationLaunchConfigurationDelegate.ATTR_WOLIPS_LAUNCH_OPEN_IN_BROWSER, "false");
		} catch (CoreException e) {
			LaunchingPlugin.getDefault().log(e);
		}
		String webServerConnectString = null;
		try {
			webServerConnectString = console.getProcess().getLaunch().getLaunchConfiguration().getAttribute(WOJavaLocalApplicationLaunchConfigurationDelegate.ATTR_WOLIPS_LAUNCH_WEBSERVER_CONNECT, "false");
		} catch (CoreException e) {
			LaunchingPlugin.getDefault().log(e);
		}

		if (openBrowser != null && openBrowser.equals("true")) {
			urlFound = false;
			webserverConnect = webServerConnectString != null && webServerConnectString.equals("true");
			webserverURLFound = false;
			this.currentConsole = console;
		} else {
			urlFound = true;
		}

	}

	public void lineAppended(IRegion line) {
		if (urlFound) {
			return;
		}
		int offset = line.getOffset();
		int length = line.getLength();
		String text = null;
		try {
			text = currentConsole.getDocument().get(offset, length);
		} catch (BadLocationException e) {
			e.printStackTrace();
		}
		if (text != null && text.startsWith("http://")) {
			final String urlString = text;
			if (!webserverConnect && !webserverURLFound) {
				webserverURLFound = true;
				return;
			}
			urlFound = true;
			Display.getDefault().asyncExec(new Runnable() {

				public void run() {
					URL url;
					try {
						url = new URL(urlString);
						IWorkbenchBrowserSupport browserSupport = WebBrowserUIPlugin.getInstance().getWorkbench().getBrowserSupport();
						IWebBrowser browser = browserSupport.createBrowser(IWorkbenchBrowserSupport.LOCATION_BAR | IWorkbenchBrowserSupport.NAVIGATION_BAR, null, null, null);
						browser.openURL(url);
					} catch (MalformedURLException e1) {
						LaunchingPlugin.getDefault().log(e1);
					} catch (PartInitException e) {
						LaunchingPlugin.getDefault().log(e);
					}

				}

			});

		}
	}

	public void dispose() {
		this.currentConsole = null;
	}
}
