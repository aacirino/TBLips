package org.objectstyle.wolips.launching.classpath;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.jdt.core.IClasspathContainer;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.internal.launching.LaunchingMessages;
import org.eclipse.jdt.internal.launching.LaunchingPlugin;
import org.eclipse.jdt.internal.launching.RuntimeClasspathEntry;
import org.eclipse.jdt.launching.IJavaLaunchConfigurationConstants;
import org.eclipse.jdt.launching.IRuntimeClasspathEntry;
import org.eclipse.jdt.launching.IRuntimeClasspathEntryResolver2;
import org.eclipse.jdt.launching.IVMInstall;
import org.eclipse.jdt.launching.JavaRuntime;
import org.objectstyle.wolips.core.resources.types.project.ProjectAdapter;
import org.objectstyle.wolips.jdt.classpath.WOFrameworkClasspathContainer;

/**
 * Default resolver for a contributed classpath entry
 */
public class WORuntimeClasspathEntryResolver implements IRuntimeClasspathEntryResolver2 {
	/**
	 * Performs default resolution for a container entry.
	 * Delegates to the Java model.
	 */
	
	public IRuntimeClasspathEntry[] resolveRuntimeClasspathEntry(IRuntimeClasspathEntry entry, ILaunchConfiguration configuration) throws CoreException {
		IJavaProject project = entry.getJavaProject();
		if (project == null) {
			project = JavaRuntime.getJavaProject(configuration);
		}
		if (project == null || entry == null) {
			// cannot resolve without entry or project context
			return new IRuntimeClasspathEntry[0];
		}
		IClasspathContainer container = JavaCore.getClasspathContainer(entry.getPath(), project);
		if (container == null) {
			abort(MessageFormat.format(LaunchingMessages.JavaRuntime_Could_not_resolve_classpath_container___0__1, new Object[] { entry.getPath().toString() }), null);
			// execution will not reach here - exception will be thrown
			return null;
		}
		IClasspathEntry[] cpes = container.getClasspathEntries();
		int property = -1;
		switch (container.getKind()) {
		case IClasspathContainer.K_APPLICATION:
			property = IRuntimeClasspathEntry.USER_CLASSES;
			break;
		case IClasspathContainer.K_DEFAULT_SYSTEM:
			property = IRuntimeClasspathEntry.STANDARD_CLASSES;
			break;
		case IClasspathContainer.K_SYSTEM:
			property = IRuntimeClasspathEntry.BOOTSTRAP_CLASSES;
			break;
		}
		List<IRuntimeClasspathEntry> resolved = new ArrayList<IRuntimeClasspathEntry>(cpes.length);
		for (IClasspathEntry cpe : cpes) {
			if (cpe.getEntryKind() == IClasspathEntry.CPE_PROJECT) {
				IProject p = ResourcesPlugin.getWorkspace().getRoot().getProject(cpe.getPath().segment(0));
				IJavaProject jp = JavaCore.create(p);
				IRuntimeClasspathEntry classpath = JavaRuntime.newDefaultProjectClasspathEntry(jp);
				IRuntimeClasspathEntry[] entries = JavaRuntime.resolveRuntimeClasspathEntry(classpath, jp);
				for (IRuntimeClasspathEntry e : entries) {
					if (!resolved.contains(e)) {
						resolved.add(e);
					}
				}
			} else {
				IRuntimeClasspathEntry e = new RuntimeClasspathEntry(cpe);
				if (!resolved.contains(e)) {
					resolved.add(e);
				}
			}
		}
		// set classpath property
		IRuntimeClasspathEntry[] result = new IRuntimeClasspathEntry[resolved.size()];
		for (int i = 0; i < result.length; i++) {
			result[i] = resolved.get(i);
			result[i].setClasspathProperty(property);
		}
		return result;	}

	public IRuntimeClasspathEntry[] resolveRuntimeClasspathEntry(IRuntimeClasspathEntry entry, IJavaProject project) throws CoreException {
		if (project == null || entry == null) {
			// cannot resolve without entry or project context
			return new IRuntimeClasspathEntry[0];
		}
		ProjectAdapter projectAdapter = (ProjectAdapter)project.getProject().getAdapter(ProjectAdapter.class);
		IClasspathEntry cp = entry.getClasspathEntry();
		if (projectAdapter != null && projectAdapter.isFramework()) {
			if (cp != null && !cp.isExported() && cp.getPath() != null && cp.getPath().segmentCount() > 0 && WOFrameworkClasspathContainer.ID.equals(cp.getPath().segment(0))) {
				return new IRuntimeClasspathEntry[0];
			}
		}
		IClasspathContainer container = JavaCore.getClasspathContainer(entry.getPath(), project);
		if (container == null) {
			abort(MessageFormat.format(LaunchingMessages.JavaRuntime_Could_not_resolve_classpath_container___0__1, new Object[] { entry.getPath().toString() }), null);
			// execution will not reach here - exception will be thrown
			return null;
		}
		IClasspathEntry[] cpes = container.getClasspathEntries();
		int property = -1;
		switch (container.getKind()) {
		case IClasspathContainer.K_APPLICATION:
			property = IRuntimeClasspathEntry.USER_CLASSES;
			break;
		case IClasspathContainer.K_DEFAULT_SYSTEM:
			property = IRuntimeClasspathEntry.STANDARD_CLASSES;
			break;
		case IClasspathContainer.K_SYSTEM:
			property = IRuntimeClasspathEntry.BOOTSTRAP_CLASSES;
			break;
		}
		List<IRuntimeClasspathEntry> resolved = new ArrayList<IRuntimeClasspathEntry>(cpes.length);
		for (IClasspathEntry cpe : cpes) {
			if (cpe.getEntryKind() == IClasspathEntry.CPE_PROJECT) {
				IProject p = ResourcesPlugin.getWorkspace().getRoot().getProject(cpe.getPath().segment(0));
				IJavaProject jp = JavaCore.create(p);
				IRuntimeClasspathEntry classpath = JavaRuntime.newDefaultProjectClasspathEntry(jp);
				IRuntimeClasspathEntry[] entries = JavaRuntime.resolveRuntimeClasspathEntry(classpath, jp);
				for (IRuntimeClasspathEntry e : entries) {
					if (!resolved.contains(e)) {
						resolved.add(e);
					}
				}
			} else {
				IRuntimeClasspathEntry e = new RuntimeClasspathEntry(cpe);
				if (!resolved.contains(e)) {
					resolved.add(e);
				}
			}
		}
		// set classpath property
		IRuntimeClasspathEntry[] result = new IRuntimeClasspathEntry[resolved.size()];
		for (int i = 0; i < result.length; i++) {
			result[i] = resolved.get(i);
			result[i].setClasspathProperty(property);
		}
		return result;
	}

	/**
	 * Throws a core exception with an internal error status.
	 * 
	 * @param message the status message
	 * @param exception lower level exception associated with the
	 *  error, or <code>null</code> if none
	 */
	private void abort(String message, Throwable exception) throws CoreException {
		abort(message, IJavaLaunchConfigurationConstants.ERR_INTERNAL_ERROR, exception);
	}

	/**
	 * Throws a core exception with an internal error status.
	 * 
	 * @param message the status message
	 * @param code status code
	 * @param exception lower level exception associated with the
	 * 
	 *  error, or <code>null</code> if none
	 */
	private void abort(String message, int code, Throwable exception) throws CoreException {
		throw new CoreException(new Status(IStatus.ERROR, LaunchingPlugin.getUniqueIdentifier(), code, message, exception));
	}

	public IVMInstall resolveVMInstall(IClasspathEntry entry) throws CoreException {
		return null;
	}

	public boolean isVMInstallReference(IClasspathEntry entry) {
		return false;
	}}
