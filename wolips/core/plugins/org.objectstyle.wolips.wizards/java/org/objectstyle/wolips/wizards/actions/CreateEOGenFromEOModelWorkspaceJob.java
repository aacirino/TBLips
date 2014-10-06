/*
 * ====================================================================
 * 
 * The ObjectStyle Group Software License, Version 1.0
 * 
 * Copyright (c) 2006 The ObjectStyle Group and individual authors of the
 * software. All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * 
 * 1. Redistributions of source code must retain the above copyright notice,
 * this list of conditions and the following disclaimer.
 * 
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 * 
 * 3. The end-user documentation included with the redistribution, if any, must
 * include the following acknowlegement: "This product includes software
 * developed by the ObjectStyle Group (http://objectstyle.org/)." Alternately,
 * this acknowlegement may appear in the software itself, if and wherever such
 * third-party acknowlegements normally appear.
 * 
 * 4. The names "ObjectStyle Group" and "Cayenne" must not be used to endorse or
 * promote products derived from this software without prior written permission.
 * For written permission, please contact andrus@objectstyle.org.
 * 
 * 5. Products derived from this software may not be called "ObjectStyle" nor
 * may "ObjectStyle" appear in their names without prior written permission of
 * the ObjectStyle Group.
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
 * This software consists of voluntary contributions made by many individuals on
 * behalf of the ObjectStyle Group. For more information on the ObjectStyle
 * Group, please see <http://objectstyle.org/>.
 *  
 */
package org.objectstyle.wolips.wizards.actions;

import java.util.HashSet;
import java.util.Set;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.WorkspaceJob;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.objectstyle.wolips.baseforplugins.util.URLUtils;
import org.objectstyle.wolips.baseforuiplugins.utils.ErrorUtils;
import org.objectstyle.wolips.eogenerator.core.model.EOGeneratorModel;
import org.objectstyle.wolips.eomodeler.core.model.EOModel;
import org.objectstyle.wolips.eomodeler.core.model.EOModelGroup;
import org.objectstyle.wolips.eomodeler.core.model.EOModelVerificationFailure;
import org.objectstyle.wolips.eomodeler.core.model.IEOModelGroupFactory;
import org.objectstyle.wolips.eomodeler.editors.EOModelErrorDialog;
import org.objectstyle.wolips.wizards.EOGeneratorWizard;

public class CreateEOGenFromEOModelWorkspaceJob extends WorkspaceJob {
	private IResource _modelFile;

	private boolean _createEOModelGroup;

	public CreateEOGenFromEOModelWorkspaceJob(IResource modelFile, boolean createEOModelGroup) {
		super((createEOModelGroup) ? "Creating EOModelGroup File" : "Creating EOGenerator File ...");
		_modelFile = modelFile;
		_createEOModelGroup = createEOModelGroup;
	}

	public IStatus runInWorkspace(IProgressMonitor monitor) throws CoreException {
		try {
			String extension = (_createEOModelGroup) ? ".eomodelgroup" : ".eogen";
			EOModelGroup modelGroup = new EOModelGroup();
			final Set<EOModelVerificationFailure> failures = new HashSet<EOModelVerificationFailure>();
			IEOModelGroupFactory.Utility.loadModelGroup(_modelFile, modelGroup, failures, true, _modelFile.getLocationURI().toURL(), new NullProgressMonitor());
			EOModel model = modelGroup.getEditingModel();
			if (model != null) {
				EOGeneratorModel eogenModel = EOGeneratorWizard.createEOGeneratorModel(_modelFile.getParent(), model, false);
				String eogenBasePath = URLUtils.cheatAndTurnIntoFile(model.getModelURL()).getAbsolutePath();
				int dotIndex = eogenBasePath.lastIndexOf('.');
				eogenBasePath = eogenBasePath.substring(0, dotIndex);
				String eogenPath = eogenBasePath + extension;
				IFile eogenFile = _modelFile.getWorkspace().getRoot().getFileForLocation(new Path(eogenPath));
				for (int dupeNum = 0; eogenFile.exists(); dupeNum++) {
					eogenPath = eogenBasePath + dupeNum + extension;
					eogenFile = _modelFile.getWorkspace().getRoot().getFileForLocation(new Path(eogenPath));
				}
				eogenModel.writeToFile(eogenFile, null);
			}
			if (!failures.isEmpty()) {
				Display.getDefault().asyncExec(new Runnable() {
					public void run() {
						new EOModelErrorDialog(new Shell(), failures).open();
					}
				});
			}
		} catch (final Throwable t) {
			t.printStackTrace();
			Display.getDefault().asyncExec(new Runnable() {
				public void run() {
					ErrorUtils.openErrorDialog(new Shell(), "EOGen Creation Failed", t);
				}
			});
		}
		return new Status(IStatus.OK, org.objectstyle.wolips.eogenerator.ui.Activator.PLUGIN_ID, IStatus.OK, "Done", null);
	}
}