/*
 * ====================================================================
 * 
 * The ObjectStyle Group Software License, Version 1.0
 * 
 * Copyright (c) 2005 - 2006 The ObjectStyle Group and individual authors of the
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
package org.objectstyle.wolips.wodclipse.core.builder;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IncrementalProjectBuilder;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.ITypeHierarchy;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jface.preference.IPreferenceStore;
import org.objectstyle.wolips.bindings.Activator;
import org.objectstyle.wolips.bindings.preferences.PreferenceConstants;
import org.objectstyle.wolips.core.resources.builder.AbstractFullAndIncrementalBuilder;
import org.objectstyle.wolips.core.resources.types.SuperTypeHierarchyCache;
import org.objectstyle.wolips.core.resources.types.WOHierarchyScope;
import org.objectstyle.wolips.locate.LocatePlugin;
import org.objectstyle.wolips.locate.result.LocalizedComponentsLocateResult;
import org.objectstyle.wolips.variables.BuildProperties;
import org.objectstyle.wolips.variables.VariablesPlugin;
import org.objectstyle.wolips.wodclipse.core.completion.WodParserCache;
import org.objectstyle.wolips.wodclipse.core.util.WodModelUtils;

public class WodBuilder extends AbstractFullAndIncrementalBuilder {
	private static ExecutorService validationThreadPool;
	private static ValidationProgressJob _validationJob;

	private boolean _validateTemplatesAtAll;
	private boolean _validateTemplatesNow;
	private int _buildKind;
	private boolean _threadedBuild;

	static {
		WodBuilder._validationJob = new ValidationProgressJob();
		WodBuilder.validationThreadPool = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors() * 2);
	}

	public WodBuilder() {
		super();
	}

	public boolean isEnabled() {
		return true;
	}

	// MS: This should probably move up to a higher level utility method
	protected static boolean getBooleanProperty(String propertiesKey, String preferencesKey, IProject project, IPreferenceStore preferenceStore) {
		boolean value;
		BuildProperties buildProperties = (BuildProperties) project.getAdapter(BuildProperties.class);
		String buildPropertiesValueStr = buildProperties.get(propertiesKey);
		if (buildPropertiesValueStr != null) {
			value = "true".equalsIgnoreCase(buildPropertiesValueStr);
		}
		else {
			String globalPropertiesValueStr = VariablesPlugin.getDefault().getGlobalVariables().getString(propertiesKey + "." + project.getName());
			if (globalPropertiesValueStr != null) {
				value = "true".equalsIgnoreCase(globalPropertiesValueStr);
			}
			else {
				value = preferenceStore.getBoolean(preferencesKey);
			}
		}
		return value;
	}

	public boolean buildStarted(int kind, Map args, IProgressMonitor monitor, IProject project, Map buildCache) {
		_buildKind = kind;
		if (getBooleanProperty("component.validateTemplates", PreferenceConstants.VALIDATE_TEMPLATES_KEY, project, Activator.getDefault().getPreferenceStore())) {
			_validateTemplatesAtAll = true;
			if (!getBooleanProperty("component.validateTemplatesOnBuild", PreferenceConstants.VALIDATE_TEMPLATES_ON_BUILD_KEY, project, Activator.getDefault().getPreferenceStore())) {
				_validateTemplatesNow = false;
			}
			else {
				_validateTemplatesNow = true;
			}
		}
		_threadedBuild = getBooleanProperty("component.threadedValidation", PreferenceConstants.THREADED_VALIDATION_KEY, project, Activator.getDefault().getPreferenceStore());
		if (kind == IncrementalProjectBuilder.FULL_BUILD) {
			WodParserCache.getModelGroupCache().clearCacheForProject(project);
			WodParserCache.getTypeCache().clearCacheForProject(project);
			WOHierarchyScope.clearCacheForProject(project);
		}
		return false;
	}

	public boolean buildPreparationDone(int kind, Map args, IProgressMonitor monitor, IProject project, Map buildCache) {
		return false;
	}

	public void handleClasses(IResource resource, IProgressMonitor monitor, Map buildCache) {
		// DO NOTHING
	}

	public void handleSource(IResource resource, IProgressMonitor progressMonitor, Map buildCache) {
		if (_validateTemplatesNow) {
			try {
				if (_buildKind == IncrementalProjectBuilder.INCREMENTAL_BUILD || _buildKind == IncrementalProjectBuilder.AUTO_BUILD) {
					ICompilationUnit compilationUnit = JavaCore.createCompilationUnitFrom((IFile) resource);
					if (compilationUnit != null) {
						IType type = compilationUnit.findPrimaryType();
						if (type != null) {
							IType woElementType = type.getJavaProject().findType("com.webobjects.appserver.WOElement", progressMonitor);
							if (woElementType != null) {
								ITypeHierarchy typeHierarchy = SuperTypeHierarchyCache.getTypeHierarchy(type, progressMonitor);
								if (typeHierarchy != null && typeHierarchy.contains(woElementType)) {
									LocalizedComponentsLocateResult results = LocatePlugin.getDefault().getLocalizedComponentsLocateResult(resource);
									IFile wodFile = results.getFirstWodFile();
									if (wodFile != null && wodFile.exists()) {
										wodFile.touch(progressMonitor);
										validateWodFile(wodFile, progressMonitor);
									}
								}
							}
						}
					}
				}
				// touchRelatedResources(_resource, _progressMonitor, _buildCache);
			}
			catch (Throwable t) {
				Activator.getDefault().log(t);
			}
		}
	}

	public void handleClasspath(IResource resource, IProgressMonitor monitor, Map buildCache) {
		// DO NOTHING
	}

	@SuppressWarnings("unchecked")
	protected Set<IContainer> componentBuildCache(Map buildCache) {
		Set<IContainer> builtComponents = (Set<IContainer>) buildCache.get("builtComponents");
		if (builtComponents == null) {
			builtComponents = new HashSet<IContainer>();
			buildCache.put("builtComponents", builtComponents);
		}
		return builtComponents;
	}

	public void handleOther(IResource resource, IProgressMonitor monitor, Map buildCache) {
		// ignore
	}

	protected boolean shouldValidate(IResource resource, Map buildCache) {
		boolean validate = false;
		Set<IContainer> builtComponents = componentBuildCache(buildCache);
		IContainer woFolder;
		if (resource instanceof IFile) {
			woFolder = resource.getParent();
		}
		else if (resource instanceof IContainer) {
			woFolder = (IContainer) resource;
		}
		else {
			woFolder = null;
		}
		if (woFolder != null && !builtComponents.contains(woFolder)) {
			validate = true;
			builtComponents.add(woFolder);
		}
		return validate;
	}

	public void handleWebServerResources(IResource resource, IProgressMonitor monitor, Map buildCache) {
		// DO NOTHING
	}

	public void handleWoappResources(IResource resource, IProgressMonitor monitor, Map buildCache) {
		if (_validateTemplatesNow) {
			try {
				boolean validate = false;
				if (resource instanceof IFile) {
					if (resource.getParent().getName().endsWith(".wo")) {
						IFile file = (IFile) resource;
						String fileExtension = file.getFileExtension();
						if ("wod".equals(fileExtension)) {
							validate = shouldValidate(file, buildCache);
						}
						else if ("html".equals(fileExtension)) {
							validate = shouldValidate(file, buildCache);
						}
						else if ("api".equals(fileExtension)) {
							// should we really do something with the component when
							// we change the api?
							// shoulnd't we validate all files using the api?
							validate = false;
						}
						else if ("woo".equals(fileExtension)) {
							validate = shouldValidate(file, buildCache);
						}
					}
				}
				else if (resource instanceof IContainer) {
					IContainer folder = (IContainer) resource;
					String fileExtension = folder.getFileExtension();
					if ("wo".equals(fileExtension)) {
						validate = shouldValidate(folder, buildCache);
					}
				}

				if (validate) {
					validateWodFile(resource, monitor);
				}
			}
			catch (Throwable e) {
				Activator.getDefault().log(e);
			}
		}
		else if (!_validateTemplatesAtAll || (_buildKind == IncrementalProjectBuilder.FULL_BUILD || _buildKind == IncrementalProjectBuilder.CLEAN_BUILD)) {
			if (resource instanceof IFile) {
				IFile file = (IFile) resource;
				String fileExtension = file.getFileExtension();
				if ("wod".equals(fileExtension)) {
					WodModelUtils.deleteProblems(file);
				}
				else if ("html".equals(fileExtension)) {
					WodModelUtils.deleteProblems(file);
				}
				else if ("woo".equals(fileExtension)) {
					WodModelUtils.deleteProblems(file);
				}
			}
			else if (resource instanceof IContainer) {
				IContainer folder = (IContainer) resource;
				String fileExtension = folder.getFileExtension();
				if ("wo".equals(fileExtension)) {
					String componentName = folder.getName().substring(0, folder.getName().lastIndexOf('.'));
					WodModelUtils.deleteProblems(folder.getFile(new Path(componentName + ".html")));
					WodModelUtils.deleteProblems(folder.getFile(new Path(componentName + ".wod")));
					WodModelUtils.deleteProblems(folder.getFile(new Path(componentName + ".woo")));
				}
			}
		}
	}

	protected void validateWodFile(IResource resource, IProgressMonitor progressMonitor) {
		// System.out.println("WodBuilder.validateWodFile: " + resource);
		WodBuilder.validateComponent(resource, _threadedBuild, progressMonitor);
	}

	public static void validateComponent(IResource resource, boolean threaded, IProgressMonitor progressMonitor) {
		if (threaded) {
			WodBuilder.validationThreadPool.execute(new ValidatingComponent(resource, progressMonitor));
		}
		else {
			WodBuilder._validateComponent(resource, progressMonitor, true);
		}
	}

	public static void _validateComponent(IResource resource, IProgressMonitor progressMonitor, boolean showProgress) {
		if (resource != null) {
			// System.out.println("WodBuilder._validateComponent: " + resource);
			String resourceName = resource.getName();
			if (progressMonitor != null) {
				if (showProgress) {
					progressMonitor.subTask("Locating components for " + resourceName + " ...");
				}
			}
			try {
				WodParserCache cache = WodParserCache.parser(resource);
				if (progressMonitor != null && cache.getWodEntry().getFile() != null) {
					if (showProgress) {
						progressMonitor.subTask("Building WO " + cache.getWodEntry().getFile().getName() + " ...");
					}
				}
				cache.clearParserCache();
				cache.parse();
				cache.validate(true, false);
			}
			catch (Throwable t) {
				t.printStackTrace();
			}
			// System.out.println("WodBuilder._validateComponent: done with " +
			// resource);
		}
	}

	public static class ValidationProgressJob extends Job {
		private ConcurrentLinkedQueue<IResource> _validatingResources;
		private Object _lock = new Object();

		public ValidationProgressJob() {
			super("Component Validation");
			setPriority(Job.LONG);
			setUser(false);
			// setProperty(IProgressConstants.ICON_PROPERTY, getImage());

			_validatingResources = new ConcurrentLinkedQueue<IResource>();
		}

		public void start(IResource resource) {
			_validatingResources.add(resource);
			WOHierarchyScope.incrementReferenceCountForProject(resource.getProject());
			synchronized (_lock) {
				_lock.notifyAll();
			}
			schedule();
		}

		public void finish(IResource resource) {
			_validatingResources.remove(resource);
			WOHierarchyScope.decrementReferenceCountForProject(resource.getProject());
			synchronized (_lock) {
				_lock.notifyAll();
			}
		}

		@Override
		protected IStatus run(IProgressMonitor monitor) {
			IStatus status = null;
			long completedAtStart = ((ThreadPoolExecutor) WodBuilder.validationThreadPool).getCompletedTaskCount();
			while (!monitor.isCanceled() && status == null) {
				synchronized (_lock) {
					if (_validatingResources.size() == 0) {
						monitor.done();
						status = Status.OK_STATUS;
					}
					else {
						setName("Component Validation");

						try {
							_lock.wait();
						}
						catch (InterruptedException e) {
							e.printStackTrace();
						}
					}
				}

				if (_validatingResources.size() > 0 && !monitor.isCanceled()) {
					monitor.beginTask("Validating ...", IProgressMonitor.UNKNOWN);
					StringBuffer sb = new StringBuffer();
					sb.append("Validating ");
					Iterator<IResource> resourceIter = _validatingResources.iterator();
					while (resourceIter.hasNext()) {
						IResource resource = resourceIter.next();
						sb.append(resource.getName());
						if (resourceIter.hasNext()) {
							sb.append(", ");
						}
					}

					long taskCount = ((ThreadPoolExecutor) WodBuilder.validationThreadPool).getTaskCount() - completedAtStart;
					long completedTaskCount = ((ThreadPoolExecutor) WodBuilder.validationThreadPool).getCompletedTaskCount() - completedAtStart;
					sb.append(" (" + completedTaskCount + " of " + taskCount + ")");
					
					sb.append("...");
					monitor.setTaskName(sb.toString());

					monitor.beginTask(sb.toString(), (int) taskCount);
					monitor.worked((int) completedTaskCount);
				}
			}
			return monitor.isCanceled() ? Status.CANCEL_STATUS : status;
		}
	}

	public static class ValidatingComponent implements Runnable {
		private IResource _resource;
		private IProgressMonitor _monitor;

		public ValidatingComponent(IResource resource, IProgressMonitor monitor) {
			_resource = resource;
			_monitor = monitor;
		}

		public void run() {
			if (_monitor == null || !_monitor.isCanceled()) {
				_validationJob.start(_resource);
				try {
					WodBuilder._validateComponent(_resource, _monitor, false);
				}
				finally {
					_validationJob.finish(_resource);
				}
			}
			else {
				// System.out.println("BuildingComponent.run: cancelled " + _resource);
			}
		}
	}
}