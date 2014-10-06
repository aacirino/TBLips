/* ====================================================================
 *
 * The ObjectStyle Group Software License, Version 1.0
 *
 * Copyright (c) 2005 The ObjectStyle Group,
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
package org.objectstyle.wolips.core.resources.tests;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import junit.framework.TestCase;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.objectstyle.wolips.baseforplugins.util.WOLipsNatureUtils;
import org.objectstyle.wolips.core.resources.types.project.ProjectAdapter;

public abstract class AbstractProjectTestCase extends TestCase {

	private ArrayList<IProject> projects;

	/**
	 * @param suffix
	 *            Per convention this is the name of the test method.
	 * @return A project. The name of the project is this.getClass().getName() +
	 *         "-" + suffix. The dots are replaced with "-".
	 */
	public IProject getProject(String suffix) {
		String projectName = this.getClass().getName() + "-" + suffix;
		projectName = projectName.replace('.', '-');
		IProject project = ResourcesPlugin.getWorkspace().getRoot().getProject(projectName);
		this.projects.add(project);
		return project;
	}

	public void initIncrementalFrameworkProject(IProject project, IProgressMonitor monitor) {
		assertFalse(project.exists());
		ProjectAdapter projectAdapter = (ProjectAdapter) project.getAdapter(ProjectAdapter.class);
		assertNull(projectAdapter);

		try {
			project.create(monitor);
			project.open(monitor);
			IProjectDescription description = project.getDescription();
			List<String> naturesList = new ArrayList<String>(Arrays.asList(description.getNatureIds()));
			naturesList.add("org.eclipse.jdt.core.javanature");
			description.setNatureIds(naturesList.toArray(new String[naturesList.size()]));
			project.setDescription(description, monitor);
			boolean success = WOLipsNatureUtils.addIncrementalFrameworkNatureToProject(project, monitor);
			assertTrue(success);
		} catch (CoreException e) {
			assertTrue(false);
		}
	}

	public void initAntFrameworkProject(IProject project, IProgressMonitor monitor) {
		assertFalse(project.exists());
		ProjectAdapter projectAdapter = (ProjectAdapter) project.getAdapter(ProjectAdapter.class);
		assertNull(projectAdapter);

		try {
			project.create(monitor);
			project.open(monitor);
			IProjectDescription description = project.getDescription();
			List<String> naturesList = new ArrayList<String>(Arrays.asList(description.getNatureIds()));
			naturesList.add("org.eclipse.jdt.core.javanature");
			description.setNatureIds(naturesList.toArray(new String[naturesList.size()]));
			project.setDescription(description, monitor);
			boolean success = WOLipsNatureUtils.addAntFrameworkNatureToProject(project, monitor);
			assertTrue(success);
		} catch (CoreException e) {
			assertTrue(false);
		}
	}

	public void initIncrementalApplicationProject(IProject project, IProgressMonitor monitor) {
		assertFalse(project.exists());
		ProjectAdapter projectAdapter = (ProjectAdapter) project.getAdapter(ProjectAdapter.class);
		assertNull(projectAdapter);

		try {
			project.create(monitor);
			project.open(monitor);
			IProjectDescription description = project.getDescription();
			List<String> naturesList = new ArrayList<String>(Arrays.asList(description.getNatureIds()));
			naturesList.add("org.eclipse.jdt.core.javanature");
			description.setNatureIds(naturesList.toArray(new String[naturesList.size()]));
			project.setDescription(description, monitor);
			boolean success = WOLipsNatureUtils.addIncrementalApplicationNatureToProject(project, monitor);
			assertTrue(success);
		} catch (CoreException e) {
			assertTrue(false);
		}
	}

	public void initAntApplicationProject(IProject project, IProgressMonitor monitor) {
		assertFalse(project.exists());
		ProjectAdapter projectAdapter = (ProjectAdapter) project.getAdapter(ProjectAdapter.class);
		assertNull(projectAdapter);

		try {
			project.create(monitor);
			project.open(monitor);
			IProjectDescription description = project.getDescription();
			List<String> naturesList = new ArrayList<String>(Arrays.asList(description.getNatureIds()));
			naturesList.add("org.eclipse.jdt.core.javanature");
			description.setNatureIds(naturesList.toArray(new String[naturesList.size()]));
			project.setDescription(description, monitor);
			boolean success = WOLipsNatureUtils.addAntApplicationNatureToProject(project, monitor);
			assertTrue(success);
		} catch (CoreException e) {
			assertTrue(false);
		}
	}

	protected void setUp() throws Exception {
		super.setUp();
		this.projects = new ArrayList<IProject>();
	}

	protected void tearDown() throws Exception {
		super.tearDown();
		for (int i = 0; i < this.projects.size(); i++) {
			IProject project = this.projects.get(i);
			if (project.exists()) {
				project.delete(true, new NullProgressMonitor());
			}
		}
		this.projects = null;
	}

}
