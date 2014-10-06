/*
 * ==================================================================== The
 * ObjectStyle Group Software License, Version 1.0 Copyright (c) 2006 The
 * ObjectStyle Group, and individual authors of the software. All rights
 * reserved. Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * 1. Redistributions of source code must retain the above copyright notice,
 * this list of conditions and the following disclaimer. 2. Redistributions in
 * binary form must reproduce the above copyright notice, this list of
 * conditions and the following disclaimer in the documentation and/or other
 * materials provided with the distribution. 3. The end-user documentation
 * included with the redistribution, if any, must include the following
 * acknowlegement: "This product includes software developed by the ObjectStyle
 * Group (http://objectstyle.org/)." Alternately, this acknowlegement may appear
 * in the software itself, if and wherever such third-party acknowlegements
 * normally appear. 4. The names "ObjectStyle Group" and "Cayenne" must not be
 * used to endorse or promote products derived from this software without prior
 * written permission. For written permission, please contact
 * andrus@objectstyle.org. 5. Products derived from this software may not be
 * called "ObjectStyle" nor may "ObjectStyle" appear in their names without
 * prior written permission of the ObjectStyle Group. THIS SOFTWARE IS PROVIDED
 * ``AS IS'' AND ANY EXPRESSED OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED
 * TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
 * PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE OBJECTSTYLE GROUP OR ITS
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS;
 * OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY,
 * WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR
 * OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF
 * ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 * ==================================================================== This
 * software consists of voluntary contributions made by many individuals on
 * behalf of the ObjectStyle Group. For more information on the ObjectStyle
 * Group, please see <http://objectstyle.org/>.
 */
package org.objectstyle.woproject.maven2.wobootstrap;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.io.File;
import java.util.Properties;

import junit.framework.TestCase;

import org.apache.commons.io.FileUtils;
import org.apache.maven.plugin.MojoExecutionException;
import org.easymock.EasyMock;
import org.objectstyle.woproject.maven2.wobootstrap.locator.WebObjectsLocator;

/**
 * Criado em 30/09/2006
 */

/**
 * @author <a href="mailto:hprange@moleque.com.br">Henrique Prange</a>
 */
public class TestAbstractBootstrapMojo extends TestCase {
	protected String input[];

	protected MockBootstrapMojo mojo;

	protected String output[];

	/*
	 * (non-Javadoc)
	 * 
	 * @see junit.framework.TestCase#setUp()
	 */
	@Override
	protected void setUp() throws Exception {
		super.setUp();

		input = new String[] { "c:/JavaDirectToWeb.jar", "c:/JavaFoundation.jar", "c:/javaeoutil.jar", "c:/JavaXML.jar" };

		output = new String[] { "JavaDirectToWeb", "JavaFoundation", "javaeoutil", "JavaXML" };

		File versionFile = FileUtils.toFile(getClass().getResource("/version.plist"));

		WebObjectsLocator mockLocator = EasyMock.createMock(WebObjectsLocator.class);

		EasyMock.expect(mockLocator.getWebObjectsVersionFile()).andReturn(versionFile);

		EasyMock.replay(new Object[] { mockLocator });

		mojo = new MockBootstrapMojo(mockLocator);
	}

	public void testArtifatIdConvention() throws Exception {
		for (int i = 0; i < input.length; i++) {
			File jar = new File(input[i]);

			assertEquals(output[i], mojo.getArtifactIdForJar(jar));
		}
	}

	public void testCaseInsensitiveNameMapping() throws Exception {
		for (int i = 0; i < input.length; i++) {
			input[i] = input[i].toLowerCase();
		}

		for (int i = 0; i < input.length; i++) {
			File jar = new File(input[i]);

			assertEquals(output[i], mojo.getArtifactIdForJar(jar));
		}
	}

	public void testCustomWebObjectsLocator() throws Exception {
		final String path = "path_to_webobject";
		final String version = "5.3.3";

		mojo = new MockBootstrapMojo(path, version);

		mojo.initializeLocator();

		assertEquals(new File(path), mojo.locator.getWebObjectsLibFolder());

		mojo.initialize();

		Properties properties = mojo.artifactProperties;

		assertEquals(version, properties.getProperty("version"));
	}

	public void testExceptionIfOnlyLibFolderOrOnlyVersionIsProvided() throws Exception {
		mojo = new MockBootstrapMojo("a path", null);

		try {
			mojo.initializeLocator();

			fail("Must throw an exception about illegal parameters");
		} catch (MojoExecutionException exception) {
			// It is ok
		}

		mojo = new MockBootstrapMojo(null, "a version");

		try {
			mojo.initializeLocator();

			fail("Must throw an exception about illegal parameters");
		} catch (MojoExecutionException exception) {
			// It is ok
		}

	}

	public void testFillValidProperties() throws Exception {
		File resourcesPath = FileUtils.toFile(getClass().getResource("/"));

		String filePath = "JavaWOExtensions.jar";

		File mockJar = new File(resourcesPath, filePath);

		Properties properties = mojo.fillProperties(mockJar);

		assertEquals("com.webobjects", properties.getProperty("groupId"));
		assertNotNull(properties.getProperty("version"));
		assertEquals("jar", properties.getProperty("packaging"));
		assertEquals(mockJar.getAbsolutePath(), properties.getProperty("file"));
		assertEquals("JavaWOExtensions", properties.getProperty("artifactId"));
		assertNotNull(properties.getProperty("pomFile"));
	}

	public void testNoJarMapping() throws Exception {
		String filePath = "c:\\JavaWOExtensionsXXX.jar";

		File invalidJar = new File(filePath);

		Properties properties = mojo.fillProperties(invalidJar);

		assertNull(properties);
	}

	public void testNullJarFile() throws Exception {
		assertNull(mojo.getArtifactIdForJar(null));
		assertNull(mojo.fillProperties(null));
	}

	public void testWarningForNotMappedArtifact() throws Exception {
		WebObjectsLocator mockLocator = EasyMock.createMock(WebObjectsLocator.class);

		File versionFile = FileUtils.toFile(getClass().getResource("/version.plist"));

		EasyMock.expect(mockLocator.getWebObjectsVersionFile()).andReturn(versionFile);

		File file = FileUtils.toFile(getClass().getResource("/mock-jar1.jar")).getParentFile();

		EasyMock.expect(mockLocator.getWebObjectsLibFolder()).andReturn(file);

		EasyMock.replay(mockLocator);

		mojo.locator = mockLocator;
		mojo.nullFillProperties = true;

		mojo.execute();

		assertThat(mojo.getLog().warningCount, is(2));
		assertThat(mojo.executeGoalCount, is(0));
	}

	public void testWebObjectsNotInstalled() throws Exception {
		WebObjectsLocator mockLocator = EasyMock.createMock(WebObjectsLocator.class);

		EasyMock.expect(mockLocator.getWebObjectsVersionFile()).andReturn(null);

		mojo.locator = mockLocator;

		EasyMock.replay(new Object[] { mockLocator });

		try {
			mojo.execute();

			fail("WebOjects not installed, must throws an exception.");
		} catch (MojoExecutionException exception) {
			assertEquals("WebObjects version is missing. Maybe WebObjects isn't installed.", exception.getMessage());
		}
	}
}
