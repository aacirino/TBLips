package org.objectstyle.wolips.wizards.component;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.InvocationTargetException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.SubProgressMonitor;
import org.eclipse.jdt.core.Flags;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.IParent;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.ITypeHierarchy;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.Signature;
import org.eclipse.jdt.internal.corext.codemanipulation.StubUtility;
import org.eclipse.jdt.internal.corext.util.JavaModelUtil;
import org.eclipse.jdt.internal.corext.util.Messages;
import org.eclipse.jdt.internal.ui.JavaPlugin;
import org.eclipse.jdt.internal.ui.JavaPluginImages;
import org.eclipse.jdt.internal.ui.dialogs.StatusInfo;
import org.eclipse.jdt.internal.ui.viewsupport.BasicElementLabels;
import org.eclipse.jdt.internal.ui.wizards.NewWizardMessages;
import org.eclipse.jdt.internal.ui.wizards.SuperInterfaceSelectionDialog;
import org.eclipse.jdt.internal.ui.wizards.TypedElementSelectionValidator;
import org.eclipse.jdt.internal.ui.wizards.TypedViewerFilter;
import org.eclipse.jdt.internal.ui.wizards.dialogfields.DialogField;
import org.eclipse.jdt.internal.ui.wizards.dialogfields.IDialogFieldListener;
import org.eclipse.jdt.internal.ui.wizards.dialogfields.IStringButtonAdapter;
import org.eclipse.jdt.internal.ui.wizards.dialogfields.LayoutUtil;
import org.eclipse.jdt.internal.ui.wizards.dialogfields.SelectionButtonDialogFieldGroup;
import org.eclipse.jdt.internal.ui.wizards.dialogfields.StringButtonDialogField;
import org.eclipse.jdt.ui.CodeGeneration;
import org.eclipse.jdt.ui.wizards.NewContainerWizardPage;
import org.eclipse.jdt.ui.wizards.NewTypeWizardPage;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Widget;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.dialogs.ElementTreeSelectionDialog;
import org.eclipse.ui.internal.ide.IDEWorkbenchMessages;
import org.eclipse.ui.internal.ide.IDEWorkbenchPlugin;
import org.eclipse.ui.model.WorkbenchContentProvider;
import org.eclipse.ui.model.WorkbenchLabelProvider;
import org.eclipse.ui.views.navigator.ResourceComparator;
import org.objectstyle.wolips.baseforplugins.util.CharSetUtils;
import org.objectstyle.wolips.bindings.api.ApiModel;
import org.objectstyle.wolips.wizards.WizardsPlugin;
import org.objectstyle.wolips.wizards.enums.HTML;

/**
 * Page for creating a new TreasureBoat TBComponent type. i.e., 
 * component view with component controller.
 * 
 * Thanks to
 * @author ldeck
 */
public class NewComponentCreationPage extends NewTypeWizardPage {

	//********************************************************************
	//	Component Header
	//********************************************************************

	private void createComponentHeader(Composite parent, int nColumns) {
		Font labellingFont = scaledFont(parent.getFont(), 9);

		// component view section label
		Group componentViewTitle = new Group(parent, SWT.NONE); 
		componentViewTitle.setBackground(componentViewTitle.getDisplay().getSystemColor(SWT.COLOR_TITLE_BACKGROUND_GRADIENT));
		componentViewTitle.setLayout(newGridLayout(nColumns, 0, 1));
		componentViewTitle.setLayoutData(new GridData(GridData.GRAB_HORIZONTAL | GridData.HORIZONTAL_ALIGN_FILL));
		componentViewTitle.setFont(labellingFont);

		// add image label for component view
		Label componentViewTitleIcon = new Label(componentViewTitle, SWT.NONE);
		componentViewTitleIcon.setFont(labellingFont);
		componentViewTitleIcon.setImage(scaledImage(componentViewTitleIcon.getDisplay(), WizardsPlugin.tbComponentImageDescriptor(), LABEL_IMG_SIZE + 2, LABEL_IMG_SIZE + 2));

		Label componentViewTitleLabel = new Label(componentViewTitle, SWT.NONE);
		componentViewTitleLabel.setFont(labellingFont);
		componentViewTitleLabel.setText("Component View");

		// fill 
		LayoutUtil.setHorizontalSpan(componentViewTitle, nColumns);
	}

	//********************************************************************
	//	Component folder View
	//********************************************************************

	private StringButtonDialogField fComponentContainerDialogField;

	private void createComponentContainerControls(Composite parent, int nColumns) {
		fComponentContainerDialogField.doFillIntoGrid(parent, nColumns);
		LayoutUtil.setWidthHint(fComponentContainerDialogField.getTextControl(null), getMaxFieldWidth());
	}

	//********************************************************************
	//	HTML View
	//********************************************************************

	private IStatus fComponentHTMLStatus;
	private Combo fComponentHTMLCombo;

	/**
	 * Populate a SWT Combo with HTML doctypes
	 * 
	 * @param c
	 */
	private void populateHTMLCombo(Combo c) {
		for (HTML entry : HTML.values()) {
			c.add(entry.getDisplayString());
		}
		selectHTMLDocTypePreference(c);
	}

	/*
	 * here we create the HTML Line
	 */
	private void createComponentHTMLControls(Composite parent, @SuppressWarnings("unused") int nColumns) {	
		Composite componentHTMLControls = parent;

		Label htmlLabel = new Label(componentHTMLControls, SWT.LEFT);
		htmlLabel.setText("HTML template:");
		htmlLabel.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		Composite controlsComposite = new Composite(componentHTMLControls, SWT.NONE);
		GridLayout layout = new GridLayout(2, false);
		layout.marginWidth = 0;
		controlsComposite.setLayout(layout);

		GridData data = new GridData(GridData.FILL_HORIZONTAL | GridData.GRAB_HORIZONTAL);
		controlsComposite.setLayoutData(data);

		fComponentHTMLCombo = new Combo(controlsComposite, SWT.DROP_DOWN);
		fComponentHTMLCombo.setLayoutData(new GridData(GridData.FILL_HORIZONTAL | GridData.GRAB_HORIZONTAL));
		fComponentHTMLCombo.addModifyListener(new ModifyListener() {

			@SuppressWarnings("synthetic-access")
			public void modifyText(ModifyEvent modifyevent) {
				setComponentHTMLKey(fComponentHTMLCombo.getText());
			}

		});
		populateHTMLCombo(fComponentHTMLCombo);

		LayoutUtil.setHorizontalSpan(fComponentHTMLCombo, 1);
		LayoutUtil.setWidthHint(fComponentHTMLCombo, getMaxFieldWidth());

		LayoutUtil.setHorizontalSpan(controlsComposite, 2);

		LayoutUtil.setHorizontalSpan(new Label(componentHTMLControls, SWT.NONE), 1);
	}

	/*
	 * here we have the current value for the HTML key
	 */
	private void setComponentHTMLKey(String key) {
		_componentHTMLKey = key;
	}
	private String getComponentHTMLKey() {
		return _componentHTMLKey;
	}
	private String _componentHTMLKey;





	// TODO bellow
	private static final String HTML_DOCTYPE_KEY = "NewComponentCreationPage.htmlDocType";

	/**
	 * Pick the previous encoding preference else default to
	 * HTML.TRANSITIONAL_XHTML10
	 * 
	 * @param c
	 */
	private void selectHTMLDocTypePreference(Combo c) {
		String previousDocType = this.getDialogSettings().get(HTML_DOCTYPE_KEY);

		if (previousDocType != null && previousDocType.length() > 0) {
			int i = 0;
			for (HTML entry : HTML.values()) {
				if (previousDocType.equals(entry.getDisplayString())) {
					c.select(i);
					return;
				}
				i++;
			}
		}
		// default
		c.select(0);
	}

	protected IStatus componentHTMLChanged() {
		return fComponentHTMLStatus;
	}

	private HTML getSelectedHTML() {
		return HTML.getValueForKey(getComponentHTMLKey());
	}

	//********************************************************************
	//	WOO : Encoding Charset
	//********************************************************************

	/**
	 * @param componentAPIEncodingCombo
	 */
	private void populateComponentWOOEncodingCombo(Combo componentAPIEncodingCombo) {
		for (String charset : CharSetUtils.defaultCharsetEncodingNames()) {
			componentAPIEncodingCombo.add(charset);
		}
		selectCharsetEncodingPreference(componentAPIEncodingCombo);
	}

	/**
	 * @param componentAPIEncodingCombo
	 */
	private void selectCharsetEncodingPreference(Combo combo) {
		String previousEncoding = this.getDialogSettings().get(CHARSET_ENCODING_KEY);
		if (previousEncoding != null && previousEncoding.length() > 0) {
			int i = 0;
			for (String encoding : Charset.availableCharsets().keySet()) {
				if (previousEncoding.equals(encoding)) {
					combo.select(i);
					return;
				}
				i++;
			}
		}
		combo.setText("UTF-8");
	}

	private Combo fComponentWOOEncodingCombo;

	private void createComponentWOOControls(Composite parent, @SuppressWarnings("unused") int nColumns) {
		Composite componentWOOComposite = parent;
		Label label = new Label(componentWOOComposite, SWT.LEFT);
		label.setText("Charset encoding:");

		Composite controlsComposite = new Composite(componentWOOComposite, SWT.LEFT);
		GridLayout layout = new GridLayout(3, false);
		layout.marginWidth = 0;
		controlsComposite.setLayout(layout);

		GridData data = new GridData(GridData.FILL_HORIZONTAL | GridData.GRAB_HORIZONTAL);
		controlsComposite.setLayoutData(data);

		fComponentWOOEncodingCombo = new Combo(controlsComposite, SWT.LEFT);
		fComponentWOOEncodingCombo.setLayoutData(new GridData(GridData.FILL_HORIZONTAL | GridData.GRAB_HORIZONTAL));
		fComponentWOOEncodingCombo.addModifyListener(new ModifyListener() {

			@SuppressWarnings("synthetic-access")
			public void modifyText(ModifyEvent modifyevent) {
				setComponentWOOEncodingKey(fComponentWOOEncodingCombo.getText());
			}
		});

		populateComponentWOOEncodingCombo(fComponentWOOEncodingCombo);

		LayoutUtil.setHorizontalSpan(fComponentWOOEncodingCombo, 2);
		LayoutUtil.setHorizontalGrabbing(fComponentWOOEncodingCombo);
		LayoutUtil.setWidthHint(fComponentWOOEncodingCombo, getMaxFieldWidth());

		label = new Label(controlsComposite, SWT.LEFT);
		label.setText("UTF-8 recommended");
		label.setFont(scaledFont(label.getFont(), 10));
		label.setLayoutData(new GridData());

		LayoutUtil.setHorizontalSpan(controlsComposite, 2);
		LayoutUtil.setHorizontalSpan(new Label(componentWOOComposite, SWT.NONE), 1);
	}

	protected IStatus componentWOOChanged() {
		return fComponentWOOStatus;
	}

	//********************************************************************
	//	API View
	//********************************************************************

	private Button fComponentAPICheckbox;

	private void createComponentAPIControls(Composite parent, @SuppressWarnings("unused") int nColumns) {
		Composite componentAPIComposite = parent;

		LayoutUtil.setHorizontalSpan(new Label(componentAPIComposite, SWT.NONE), 1);

		fComponentAPICheckbox = new Button(componentAPIComposite, SWT.CHECK);
		fComponentAPICheckbox.setText("API file");
		fComponentAPICheckbox.setSelection(getDialogSettings().getBoolean(API_CHECKBOX_KEY));
		fComponentAPICheckbox.addSelectionListener(new ButtonSelectionAdaptor());

		LayoutUtil.setHorizontalSpan(new Label(componentAPIComposite, SWT.NONE), 2);
	}

	protected IStatus componentAPIChanged() {
		return this.fComponentAPIStatus;
	}

	//********************************************************************
	//	Controller Header
	//********************************************************************

	private void createControllerHeader(Composite parent, int nColumns) {
		Font labellingFont = scaledFont(parent.getFont(), 9);

		// java controller section label
		Group controllerComposite = new Group(parent, SWT.NONE); 
		Color color = new Color(controllerComposite.getDisplay(), 187, 226, 174);
		controllerComposite.setBackground(color);
		controllerComposite.setLayout(newGridLayout(nColumns, 0, 1));
		controllerComposite.setLayoutData(new GridData(GridData.GRAB_HORIZONTAL | GridData.HORIZONTAL_ALIGN_FILL));
		controllerComposite.setFont(labellingFont);

		// add image label for controller view
		Label controllerViewTitleIcon = new Label(controllerComposite, SWT.NONE);
		controllerViewTitleIcon.setFont(labellingFont);
		controllerViewTitleIcon.setImage(scaledImage(controllerViewTitleIcon.getDisplay(), JavaPluginImages.DESC_TOOL_NEWCLASS, LABEL_IMG_SIZE + 2, LABEL_IMG_SIZE + 2));

		Label controllerViewTitleLabel = new Label(controllerComposite, SWT.NONE);
		controllerViewTitleLabel.setFont(labellingFont);
		controllerViewTitleLabel.setText("Component Controller");

		// fill 
		LayoutUtil.setHorizontalSpan(controllerComposite, nColumns);
	}














	//********************************************************************
	//	Superclass View
	//********************************************************************

	@Override
	protected void createSuperClassControls(Composite composite, int columns) {
		super.createSuperClassControls(composite, columns);
	}

	private static final String DEFAULT_SUPERCLASS_NAME = "org.treasureboat.webcore.components.TBComponent";

	@Override
	protected IStatus superClassChanged() {
		StatusInfo superclassStatus = new StatusInfo();

		if (!DEFAULT_SUPERCLASS_NAME.equals(getSuperClass())) {
			// Check if a superClass is selected
			if (getSuperClass() == null || getSuperClass().matches("\\s*")) {
				superclassStatus.setError("The super type must be assignable to " + DEFAULT_SUPERCLASS_NAME);
			}

			// Check if we have TBComponent as super
			else if (getDefaultTBComponentType() == null) {
				superclassStatus.setError(DEFAULT_SUPERCLASS_NAME + " is not on the classpath");
			}

			else {
				try {
					IType type = getJavaProject().findType(getSuperClass());
					ITypeHierarchy typeHierarchy = type.newSupertypeHierarchy(new NullProgressMonitor());
					if (!JavaModelUtil.isSuperType(typeHierarchy, getDefaultTBComponentType(), type)) {
						superclassStatus.setError("The super type must be assignable to " + DEFAULT_SUPERCLASS_NAME);
					}
				} catch (JavaModelException e) {
					System.err.println(getClass().getName() + ".superClassChanged() Failed to determine superclass hierarchy.");
					e.printStackTrace(System.err);
					superclassStatus.setWarning("Unable to determine superclass hierarchy");
				}
			}
		}
		if (!superclassStatus.isError()) {
			IStatus status = super.superClassChanged();
			if (!superclassStatus.isWarning() && status.getSeverity() == (IStatus.ERROR | IStatus.WARNING)) {
				return status;
			}
		}
		return superclassStatus;
	}

	/*
	 * this will return the type for superclass
	 * it get called for check to see if the superclass is set correct and
	 * in the hierarchy of TBComponent  
	 */
	private IType getDefaultTBComponentType() {
		if (_tbComponentType == null && getJavaProject() != null) {
			try {
				_tbComponentType = getJavaProject().findType(DEFAULT_SUPERCLASS_NAME);
			} catch (JavaModelException e) {
				e.printStackTrace(System.err);
			}
		}
		return _tbComponentType;
	}
	private IType _tbComponentType;







































	/**
	 * Represents an abstract project layout
	 * @author ldeck
	 */
	// TODO amalgamate with some other util
	private abstract static class AbstractLayout {

		public final boolean isReservedJavaSourcesPath(IPath path) {
			return isReservedPath(path, reservedPathPrefixes(), reservedJavaSourcePaths());
		}

		private boolean isReservedPath(IPath path, List<String> prefixes, List<String> reservedPaths) {
			if (path != null) {
				IPath relPath = path.makeAbsolute().makeRelative().removeFirstSegments(1);
				String relPathString = relPath.toString().toLowerCase();
				for (String reject : reservedPathRejects()) {
					if (relPathString.startsWith(reject.toLowerCase())) {
						return true;
					}
				}
				for (String prefix : prefixes) {
					for (String reservedPath : reservedPaths) {
						if (relPathString.startsWith((prefix + reservedPath).toLowerCase())) {
							return true;
						}
					}
				}
			}
			return false;
		}

		public final boolean isReservedResourcesPath(IPath path) {
			return isReservedPath(path, reservedPathPrefixes(), reservedResourceSourcePaths());
		}

		protected abstract List<String> reservedJavaSourcePaths();

		protected abstract List<String> reservedPathPrefixes();

		protected abstract List<String> reservedPathRejects();

		protected abstract List<String> reservedResourceSourcePaths();
	}




	/**
	 * Listener for registered button events.
	 * 
	 * @author ldeck
	 */
	class ButtonSelectionAdaptor implements SelectionListener {

		public void widgetDefaultSelected(SelectionEvent event) {
			handleSelectionEvent(event);
		}

		public void widgetSelected(SelectionEvent event) {
			handleSelectionEvent(event);
		}
	}








	/**
	 * Component container field adaptor.
	 * @author ldeck
	 */
	class ComponentContainerFieldAdapter implements IStringButtonAdapter, IDialogFieldListener {

		public void changeControlPressed(DialogField field) {
			componentContainerChangeControlPressed(field);
		}

		public void dialogFieldChanged(DialogField field) {
			componentContainerDialogFieldChanged(field);
		}
	}

	/**
	 * Worker for creating a file with the given contents.
	 * @author ldeck
	 */
	class FileCreationWorker implements IRunnableWithProgress {

		private final IFolder fileContainer;
		private final String fileContents;
		private final String fileExtension;
		private final String fileName;

		public FileCreationWorker(final IFolder container, String name, String extension, String initialContents) {
			this.fileContainer = container;
			this.fileName = name;
			this.fileExtension = extension;
			this.fileContents = initialContents == null ? "" : initialContents;
		}

		private IFile createFileHandle(IPath filePath) {
			return IDEWorkbenchPlugin.getPluginWorkspace().getRoot().getFile(filePath);
		}

		/**
		 * @see org.eclipse.jface.operation.IRunnableWithProgress#run(org.eclipse.core.runtime.IProgressMonitor)
		 */
		public void run(IProgressMonitor progressMonitor) throws InvocationTargetException, InterruptedException {
			try {
				IPath containerPath = this.fileContainer.getFullPath();
				IPath newFilePath = containerPath.addTrailingSeparator().append(this.fileName + "." + this.fileExtension);

				final IFile newFileHandle = createFileHandle(newFilePath);
				final InputStream initialContents;
				try {
					initialContents = new ByteArrayInputStream(this.fileContents.getBytes("UTF-8"));

					newFileHandle.create(initialContents, true, progressMonitor);	
				}
				catch (UnsupportedEncodingException e) {
					throw new InvocationTargetException(e);
				}
				catch (CoreException e) {
					System.err.println(getClass().getName() + ".run(IProgressMonitor) Creating file failed..." + e.getLocalizedMessage());
					e.printStackTrace(System.err);
					// TODO (ldeck) provide relevant logging
					IDEWorkbenchPlugin.log(getClass(), "run(IProgressMonitor)", e.getCause());
					MessageDialog.openError(getShell(), IDEWorkbenchMessages.WizardNewFileCreationPage_internalErrorTitle, NLS.bind(IDEWorkbenchMessages.WizardNewFileCreationPage_internalErrorMessage, e.getMessage()));
				}

			} finally {
				progressMonitor.worked(1);
			}
		}
	}

	/**
	 * Represents the wolips fluffy woproject layout
	 * @author ldeck
	 */
	private static class FluffyLayout extends AbstractLayout {

		private static final List<String> RESERVED_FLUFFY_PREFIX_PATHS = Arrays.asList("");

		private static final List<String> RESERVED_FLUFFY_RESOURCE_PATHS = Arrays.asList("components");

		private static final List<String> RESERVED_FLUFFY_SOURCE_PATHS = Arrays.asList("Sources", "Tests", "src");

		private static final List<String> RESERVED_ROOT_PATHS = Arrays.asList("bin", "build", "resources", "lib", "libraries", "webserverresources", "woproject");

		@Override
		protected List<String> reservedJavaSourcePaths() {
			return RESERVED_FLUFFY_SOURCE_PATHS;
		}

		@Override
		protected List<String> reservedPathPrefixes() {
			return RESERVED_FLUFFY_PREFIX_PATHS;
		}

		@Override
		protected List<String> reservedPathRejects() {
			return RESERVED_ROOT_PATHS;
		}

		@Override
		protected List<String> reservedResourceSourcePaths() {
			return RESERVED_FLUFFY_RESOURCE_PATHS;
		}

	}


	/**
	 * Represents the maven2 woproject layout
	 * @author ldeck
	 */
	private static class MavenLayout extends AbstractLayout {

		private static final List<String> RESERVED_MAVEN2_PREFIX_PATHS = Arrays.asList("src/main/", "src/test/", "src/itest/");

		private static final List<String> RESERVED_MAVEN2_RESOURCE_PATHS = Arrays.asList("components", "resources", "webserver-resources");

		private static final List<String> RESERVED_MAVEN2_SOURCE_PATHS = Arrays.asList("java");

		private static final List<String> RESERVED_ROOT_PATHS = Arrays.asList("target");

		@Override
		protected List<String> reservedJavaSourcePaths() {
			return RESERVED_MAVEN2_SOURCE_PATHS;
		}

		@Override
		protected List<String> reservedPathPrefixes() {
			return RESERVED_MAVEN2_PREFIX_PATHS;
		}

		@Override
		protected List<String> reservedPathRejects() {
			return RESERVED_ROOT_PATHS;
		}

		@Override
		protected List<String> reservedResourceSourcePaths() {
			return RESERVED_MAVEN2_RESOURCE_PATHS;
		}

	}

	private static final String API_CHECKBOX_KEY = "NewComponentCreationPage.apiCheckbox";

	private static final String BODY_CHECKBOX_KEY = "NewComponentCreationPage.bodyCheckbox";

	private static final String CHARSET_ENCODING_KEY = "NewComponentCreationPage.encoding";

	protected static final String COMPONENT_CONTAINER = "NewComponentCreationPage.componentContainer";



	private static final int LABEL_IMG_SIZE = 12;

	private static final String PAGE_NAME = "NewComponentCreationPage";

	private static final List<String> PREFERRED_COMPONENT_CONTAINER_PATHS = Arrays.asList("Components", "src/main/components");

	private static final List<String> PREFERRED_SOURCE_CONTAINER_PATHS = Arrays.asList("Sources", "src/main/java");

	private static final String SETTINGS_CREATECONSTR = "create_constructor";

	private static final String SETTINGS_CREATEUNIMPLEMENTED = "create_unimplemented";


	private static final String WOD_CHECKBOX_KEY = "NewComponentCreationPage.wodCheckbox";

	private static final String WOO_CHECKBOX_KEY = "NewComponentCreationPage.wooCheckbox";

	private static IJavaElement findClassWithName(IJavaElement el, String name) {
		if (el != null && el.getElementName().equals(name + ".java")) {
			return el;
		}
		else if (el instanceof IPackageFragment || el instanceof IPackageFragmentRoot) {
			try {
				for (IJavaElement child : ((IParent)el).getChildren()) {
					IJavaElement result = findClassWithName(child, name);
					if (result != null) {
						return result;
					}
				}
			} catch (JavaModelException e) {
				System.err.println(NewComponentCreationPage.class.getName() + ".findClassWithName(IJavaElement,String)");
				e.printStackTrace(System.err);
			}
		}
		return null;
	}

	private static IJavaElement findMainClass(IJavaElement el) {
		return findClassWithName(el, "Main");
	}

	private static boolean isReservedResourcesPath(IPath path) {
		return new FluffyLayout().isReservedResourcesPath(path) || new MavenLayout().isReservedResourcesPath(path);
	}

	private static boolean isReservedSourcesPath(IPath path) {
		return new FluffyLayout().isReservedJavaSourcesPath(path) || new MavenLayout().isReservedJavaSourcesPath(path);
	}

	/**
	 * Convenience function for creating a new grid layout.
	 * @param nColumns
	 * @param marginHeight
	 * @param verticalSpacing
	 * @return
	 */
	private static GridLayout newGridLayout(int nColumns, int marginHeight, int verticalSpacing) {
		GridLayout layout = new GridLayout(nColumns, false);
		layout.marginHeight = marginHeight;
		layout.verticalSpacing = verticalSpacing;
		return layout;
	}

	private static Font scaledFont(Font font, int height) {
		FontData[] labellingFontData = font.getFontData();
		for (FontData fd : labellingFontData) {
			fd.setHeight(height);
		}
		return new Font(font.getDevice(), labellingFontData);
	}

	private static Image scaledImage(Display display, ImageDescriptor imageDescriptor, int width, int height) {
		ImageData imageData =  imageDescriptor.getImageData().scaledTo(width, height);
		return new Image(display, imageData);
	}

	/**
	 * @param lineDelimiter
	 * @param selectedEncoding
	 * @return
	 */
	private static String wooContentsWithEncoding(String lineDelimiter, String selectedEncoding) {
		StringBuilder buff = new StringBuilder();
		buff.append("{").append(lineDelimiter);
		buff.append("	\"WebObjects Release\" = \"WebObjects 5.0\";").append(lineDelimiter);
		buff.append("	encoding = \""+selectedEncoding+"\";").append(lineDelimiter);
		buff.append("}").append(lineDelimiter);
		return buff.toString();
	}





	private IStatus fComponentAPIStatus;


	private IStatus fComponentContainerStatus;




	private IStatus fComponentWODStatus;

	private Button fComponentWOOCheckbox;


	private IStatus fComponentWOOStatus;

	private SelectionButtonDialogFieldGroup fMethodStubsButtons;

	private boolean vComponentAPIEnabled;

	private IFolder vComponentContainerRoot;


	//	private boolean vComponentWODEnabled;

	private boolean vComponentWOOEnabled;

	private String vComponentWOOEncodingKey;


	//********************************************************************
	//	Constructor : コンストラクタ
	//********************************************************************

	public NewComponentCreationPage() {
		this(true, PAGE_NAME);
	}

	/**
	 * @param isClass
	 * @param pageName
	 */
	public NewComponentCreationPage(boolean isClass, String pageName) {
		super(isClass, pageName);

		setTitle("TreasureBoat Component");
		setDescription("Create a new component view with controller class");

		/* HTML Area */
		fComponentHTMLStatus = new StatusInfo();





		//		this._componentImageDescriptor = AbstractUIPlugin.imageDescriptorFromPlugin("org.objectstyle.wolips.wizards", "icons/wobuilder/WOComponentBundle.png");


		String buttonNames2[] = { NewWizardMessages.NewClassWizardPage_methods_constructors, NewWizardMessages.NewClassWizardPage_methods_inherited };
		this.fMethodStubsButtons = new SelectionButtonDialogFieldGroup(32, buttonNames2, 1);
		this.fMethodStubsButtons.setLabelText(NewWizardMessages.NewClassWizardPage_methods_label);




		ComponentContainerFieldAdapter componentContainerAdaptor = new ComponentContainerFieldAdapter();
		this.fComponentContainerDialogField = new StringButtonDialogField(componentContainerAdaptor);
		this.fComponentContainerDialogField.setDialogFieldListener(componentContainerAdaptor);
		this.fComponentContainerDialogField.setLabelText(getComponentContainerLabel());
		this.fComponentContainerDialogField.setButtonLabel(NewWizardMessages.NewContainerWizardPage_container_button);

		this.fComponentContainerStatus = new StatusInfo();
		this.fComponentWODStatus = new StatusInfo();
		this.fComponentWOOStatus = new StatusInfo();
		this.fComponentAPIStatus = new StatusInfo();


		this.vComponentAPIEnabled = false;
		this.vComponentContainerRoot = null;
		this._componentHTMLKey = HTML.BLANK_CONTENT.getDisplayString();
		//		this.vComponentWODEnabled = true;
		this.vComponentWOOEnabled = true;
		this.vComponentWOOEncodingKey = "UTF-8";
	}

	private IFolder chooseComponentContainer(String title, String message, IPath initialPath) {
		//System.out.println("building choose componentContainer for " + initialPath.getDevice() + ":" + initialPath.getFileExtension());
		Class acceptedClasses[] = {
				IProject.class, IPackageFragmentRoot.class, IFolder.class
		};
		org.eclipse.ui.dialogs.ISelectionStatusValidator validator = new TypedElementSelectionValidator(acceptedClasses, false) {

			/**
			 * @see org.eclipse.jdt.internal.ui.wizards.TypedElementSelectionValidator#isSelectedValid(java.lang.Object)
			 */
			@Override
			protected boolean isSelectedValid(Object elem) {
				if (super.isSelectedValid(elem)) {
					if (elem instanceof IPackageFragmentRoot) {
						IPackageFragmentRoot fr = (IPackageFragmentRoot) elem;
						if (isReservedSourcesPath(fr.getPath())) {
							return false;
						}
					}
					return true;
				}
				return false;
			}
		};
		org.eclipse.jface.viewers.ViewerFilter filter = new TypedViewerFilter(acceptedClasses) {

			List<String> rejectPathExtensions = Arrays.asList(
					"wo");
			List<String> rejectRelativePaths = Arrays.asList(
					"Libraries",
					"WebServerResources",
					"bin",
					"build",
					"dist",
					"lib",
					"target",
					"woproject");

			/**
			 * @see org.eclipse.jdt.internal.ui.wizards.TypedViewerFilter#select(org.eclipse.jface.viewers.Viewer, java.lang.Object, java.lang.Object)
			 */
			@Override
			public boolean select(Viewer viewer, Object parentElement, Object element) {
				if (super.select(viewer, parentElement, element)) {
					IFolder folder = (IFolder)element;
					if (!folder.getName().startsWith(".") && !rejectRelativePaths.contains(folder.getProjectRelativePath().toString())) {
						for (String pathExtension : rejectPathExtensions) {
							if (folder.getName().endsWith(pathExtension)) {
								return false;
							}
						}
						try {
							// filter package fragment roots that have any package fragment children
							IPackageFragmentRoot fragmentRoot = getJavaProject().getPackageFragmentRoot(folder);
							for (IJavaElement el : fragmentRoot.getChildren()) {
								if (el instanceof IPackageFragment || el instanceof ICompilationUnit) {
									return false;
								}
							}
						} catch (JavaModelException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						return true;
					}
				}
				return false;
			}
		};
		org.eclipse.jface.viewers.ILabelProvider lp = new WorkbenchLabelProvider();
		org.eclipse.jface.viewers.ITreeContentProvider cp = new WorkbenchContentProvider();

		IProject currProject = getJavaProject().getProject();
		ElementTreeSelectionDialog dialog = new ElementTreeSelectionDialog(getShell(), lp, cp);
		dialog.setValidator(validator);
		dialog.setTitle(title);
		dialog.setMessage(message);
		dialog.addFilter(filter);
		dialog.setInput(currProject);
		dialog.setComparator(new ResourceComparator(1));
		IResource res = currProject.findMember(initialPath);
		if(res != null)
			dialog.setInitialSelection(res);
		if(dialog.open() == 0)
			return (IFolder)dialog.getFirstResult();
		return null;
	}

	/**
	 * @param field
	 */
	protected void componentContainerChangeControlPressed(DialogField field) {
		if (field == fComponentContainerDialogField) {
			IPath initialPath = new Path(this.fComponentContainerDialogField.getText());
			String title = "Existing Component Folder";
			String message = "Choose folder as component source folder:";
			IFolder folder = chooseComponentContainer(title, message, initialPath);
			if (folder != null) {
				setComponentContainerRoot(folder, true);
			}
		}
	}

	protected IStatus componentContainerChanged() {
		StatusInfo status = new StatusInfo();

		//fComponentContainerRoot = null;
		String str = getComponentContainerRootText();
		if (str.length() == 0) {
			status.setError(NewWizardMessages.NewContainerWizardPage_error_EnterContainerName);
			return status;
		}
		else if (str.endsWith(".wo")) {
			status.setError("A component cannot be created within another component");
			return status;
		}

		IPath path = new Path(str);
		IResource res = getWorkspaceRoot().findMember(path);
		if (res != null) {
			int resType = res.getType();
			if (resType == IResource.PROJECT || resType == IResource.FOLDER) {
				IProject proj = res.getProject();
				if (!proj.isOpen()) {
					status.setError(Messages.format(NewWizardMessages.NewContainerWizardPage_error_ProjectClosed, BasicElementLabels.getPathLabel(proj.getFullPath(), false)));
					return status;
				}
				IJavaProject jproject = JavaCore.create(proj);

				IPackageFragmentRoot fragmentRoot = jproject.getPackageFragmentRoot(res);
				if (res.exists()) {
					try {
						if (!proj.hasNature(JavaCore.NATURE_ID)) {
							if (resType == IResource.PROJECT) {
								status.setError(NewWizardMessages.NewContainerWizardPage_warning_NotAJavaProject);
							} else {
								status.setWarning(NewWizardMessages.NewContainerWizardPage_warning_NotInAJavaProject);
							}
							return status;
						}
						if (fragmentRoot.isArchive()) {
							status.setError(Messages.format(NewWizardMessages.NewContainerWizardPage_error_ContainerIsBinary, BasicElementLabels.getPathLabel(path, false)));
							return status;
						}
						// now throws a JavaModelException if not a source folder
						if (fragmentRoot.getKind() == IPackageFragmentRoot.K_BINARY) {
							status.setWarning(Messages.format(NewWizardMessages.NewContainerWizardPage_warning_inside_classfolder, BasicElementLabels.getPathLabel(path, false)));
						} else if (!jproject.isOnClasspath(fragmentRoot)) {
							status.setWarning(Messages.format(NewWizardMessages.NewContainerWizardPage_warning_NotOnClassPath, BasicElementLabels.getPathLabel(path, false)));
						}
					} catch (JavaModelException e) {
						// no problems. Just a standard components folder.

					} catch (CoreException e) {
						System.err.println(getClass().getName() + ".componentContainerChanged threw an exception.");
						e.printStackTrace(System.err);
						status.setWarning(NewWizardMessages.NewContainerWizardPage_warning_NotAJavaProject);
					}
				}
				return status;
			}
			status.setError(Messages.format(NewWizardMessages.NewContainerWizardPage_error_NotAFolder, BasicElementLabels.getPathLabel(path, false)));
			return status;
		}
		status.setError(Messages.format(NewWizardMessages.NewContainerWizardPage_error_ContainerDoesNotExist, BasicElementLabels.getPathLabel(path, false)));
		return status;
	}

	/**
	 * @param field
	 */
	protected void componentContainerDialogFieldChanged(DialogField field) {
		handleFieldChanged(COMPONENT_CONTAINER);
	}



	/**
	 * @see org.eclipse.jdt.ui.wizards.NewTypeWizardPage#createCommentControls(org.eclipse.swt.widgets.Composite, int)
	 */
	@Override
	protected void createCommentControls(Composite parent, int nColumns) {
		super.createCommentControls(parent, nColumns);
		enableCommentControl(true);
		setAddComments(true, true);
	}















	/**
	 * @see org.eclipse.jface.dialogs.IDialogPage#createControl(org.eclipse.swt.widgets.Composite)
	 */
	public void createControl(Composite parent) {
		initializeDialogUnits(parent);
		Composite composite = new Composite(parent, 0);
		composite.setFont(parent.getFont());

		int nColumns = 4;
		composite.setLayout(newGridLayout(nColumns, 5, 5));




		/* Name of the Control (super) */
		createTypeNameControls(composite, nColumns);

		/* Separator Line (super) */
		createSeparator(composite, nColumns);

		/* Component Header */
		createComponentHeader(composite, nColumns);

		/* Component folder View */
		createComponentContainerControls(composite, nColumns);

		/* HTML View */
		createComponentHTMLControls(composite, nColumns);

		/* WOO Charset Encoding */
		createComponentWOOControls(composite, nColumns);

		/* API View */	
		createComponentAPIControls(composite, nColumns);

		/* Separator Line (super) */
		createSeparator(composite, nColumns);

		/* Controller Header */
		createControllerHeader(composite, nColumns);







		createContainerControls(composite, nColumns);
		createPackageControls(composite, nColumns);
		//createEnclosingTypeControls(composite, nColumns);
		//createSeparator(composite, nColumns);
		//createTypeNameControls(composite, nColumns);
		//createModifierControls(composite, nColumns);
		createSuperClassControls(composite, nColumns);
		if (isSuperInterfacesEnabled()) {
			createSuperInterfacesControls(composite, nColumns);
		}
		//createMethodStubSelectionControls(composite, nColumns);
		//createCommentControls(composite, nColumns);

		setControl(composite);
		Dialog.applyDialogFont(composite);
		PlatformUI.getWorkbench().getHelpSystem().setHelp(composite, "org.eclipse.jdt.ui.new_class_wizard_page_context");
	}


	private void createMethodStubSelectionControls(Composite composite, int nColumns) {
		org.eclipse.swt.widgets.Control labelControl = fMethodStubsButtons.getLabelControl(composite);
		LayoutUtil.setHorizontalSpan(labelControl, nColumns);
		DialogField.createEmptySpace(composite);
		org.eclipse.swt.widgets.Control buttonGroup = fMethodStubsButtons.getSelectionButtonsGroup(composite);
		LayoutUtil.setHorizontalSpan(buttonGroup, nColumns - 1);
		setMethodStubSelection(false, true, false, true);
	}

	@Override
	protected void createPackageControls(Composite composite, int nColumns) {
		super.createPackageControls(composite, nColumns);
	}

	/**
	 * @see org.eclipse.jdt.ui.wizards.NewTypeWizardPage#createType(org.eclipse.core.runtime.IProgressMonitor)
	 */
	@Override
	public void createType(IProgressMonitor monitor) throws CoreException, InterruptedException {
		IProgressMonitor progressMonitor = monitor;
		if (progressMonitor == null) 
			progressMonitor = new NullProgressMonitor();

		IPath componentContainerPath = getComponentContainer().getProjectRelativePath();
		IProject project = getComponentContainer().getProject();

		progressMonitor.beginTask("Creating component....", 8 + componentContainerPath.segmentCount() + 4);

		// create controller
		super.createType(new SubProgressMonitor(progressMonitor, 8));

		// create component.wo folder
		String componentName = getTypeName();
		IPath componentBundlePath = componentContainerPath.addTrailingSeparator().append(componentName + ".wo");
		IFolder componentBundleFolder = project.getFolder(componentBundlePath);
		if (!componentBundleFolder.exists()) {
			prepareFolder(componentBundleFolder, new SubProgressMonitor(progressMonitor, componentBundlePath.segmentCount()));
		} else {
			progressMonitor.worked(componentBundlePath.segmentCount());
		}

		// gather contents to populate wod, woo, html files
		String lineDelimiter = StubUtility.getLineDelimiterUsed(getJavaProject());
		Map<String, String> filesContents = new HashMap<String, String>();
		filesContents.put("wod", "");
		filesContents.put("woo", wooContentsWithEncoding(lineDelimiter, getComponentWOOEncodingKey()));
		progressMonitor.worked(1);
		filesContents.put("html", getSelectedHTML().getHTML(lineDelimiter));
		progressMonitor.worked(1);

		// create wod, woo, html files
		for (Entry<String, String> entry : filesContents.entrySet()) {
			FileCreationWorker fileCreator = new FileCreationWorker(componentBundleFolder, componentName, entry.getKey(), entry.getValue());
			try {
				fileCreator.run(progressMonitor);
			} catch (InvocationTargetException e) {
				CoreException exception = new CoreException(Status.CANCEL_STATUS);
				exception.setStackTrace(e.getStackTrace());
				throw exception;
			}
		}
		if (vComponentAPIEnabled) {
			IFolder componentContainerFolder = project.getFolder(componentContainerPath);
			String apiContent = ApiModel.blankContent(componentName);
			FileCreationWorker fileCreator = new FileCreationWorker(componentContainerFolder, componentName, "api", apiContent);
			try {
				fileCreator.run(progressMonitor);
			} catch (InvocationTargetException e) {
				CoreException exception = new CoreException(Status.CANCEL_STATUS);
				exception.setStackTrace(e.getStackTrace());
				throw exception;
			}
		}
		progressMonitor.done();
	}

	protected void createTypeMembers(IType type, NewTypeWizardPage.ImportsManager imports, IProgressMonitor monitor) throws CoreException {
		boolean doMain = isCreateMain();
		boolean doConstr = isCreateConstructors();
		boolean doInherited = isCreateInherited();
		IMethod[] methods = createInheritedMethods(type, doConstr, doInherited, imports, new SubProgressMonitor(monitor, 1));
		IMethod mainMethod = null;
		if (doMain) {
			StringBuffer buf = new StringBuffer();
			String comment = CodeGeneration.getMethodComment(type.getCompilationUnit(), type.getTypeQualifiedName('.'), "main", new String[] { "args" }, new String[0], Signature.createTypeSignature("void", true), null, "\n");
			if (comment != null) {
				buf.append(comment);
				buf.append("\n");
			}
			buf.append("public static void main(");
			buf.append(imports.addImport("java.lang.String"));
			buf.append("[] args) {");
			buf.append("\n");
			String content = CodeGeneration.getMethodBodyContent(type.getCompilationUnit(), type.getTypeQualifiedName('.'), "main", false, "", "\n");
			if (content != null && content.length() != 0)
				buf.append(content);
			buf.append("\n");
			buf.append("}");
			mainMethod = type.createMethod(buf.toString(), null, false, null);
		}

		// TODO (lachlan) add serialVersionUID automatically
		//		boolean doSerialVersionUID = true;
		//		if (doSerialVersionUID) {
		//			boolean defaultSerialUID = false;
		//			ICleanUp serialVersionCleanUp = createCleanUp(defaultSerialUID);
		//			serialVersionCleanUp.createFix(cleanupcontext);
		//
		//			OR the following after class creation somewhere
		//
		//			ObjectStreamClass osclass = ObjectStreamClass.lookup(null);
		//			osclass.getSerialVersionUID();
		//			etc
		//		}

		if (monitor != null)
			monitor.done();
	}

	private void doStatusUpdate() {
		IStatus statuses[] = { 
				fComponentContainerStatus,
				fComponentHTMLStatus,
				fComponentWODStatus,
				fComponentWOOStatus,
				fComponentAPIStatus,
				fContainerStatus,
				isEnclosingTypeSelected() ? fEnclosingTypeStatus : fPackageStatus,
						fTypeNameStatus,
						fModifierStatus,
						fSuperClassStatus,
						fSuperInterfacesStatus
		};
		//		for (IStatus status : statuses) {
		//			System.out.println("status:" + status.getCode() + " " + status.getMessage() + " " + status.getSeverity());
		//		}	
		updateStatus(statuses);
	}

	private IFolder getComponentContainer() {
		return this.vComponentContainerRoot;
	}

	private String getComponentContainerLabel() {
		return "Component folder:";
	}

	public String getComponentContainerRootText() {
		return fComponentContainerDialogField.getText();
	}

	private String getComponentWOOEncodingKey() {
		return this.vComponentWOOEncodingKey;
	}



	protected void handleFieldChanged(String fieldName) {
		//System.out.println("Handling fieldName changed:" + fieldName);

		super.handleFieldChanged(fieldName);
		if (COMPONENT_CONTAINER.equals(fieldName)) {
			this.fComponentAPIStatus = this.componentAPIChanged();
			this.fComponentContainerStatus = componentContainerChanged();
			this.fComponentHTMLStatus = componentHTMLChanged();
			this.fComponentWOOStatus = componentWOOChanged();
		}
		if (NewContainerWizardPage.CONTAINER.equals(fieldName)) {
			this.fPackageStatus = packageChanged();
			this.fEnclosingTypeStatus = enclosingTypeChanged();
			this.fTypeNameStatus = typeNameChanged();
			this.fSuperClassStatus = superClassChanged();
			this.fSuperInterfacesStatus = superInterfacesChanged();
		}
		if (NewTypeWizardPage.MODIFIERS.equals(fieldName)) {
			int modifiers = getModifiers();
			setComponentViewEnabled(Flags.isPublic(modifiers) && !Flags.isAbstract(modifiers));
		}
		doStatusUpdate();
	}






	protected void handleSelectionEvent(SelectionEvent event) {
		Widget w = event.widget;
		if (w instanceof Button) {
			refreshButtonSettings((Button) w);
		}
	}

	public void init(IStructuredSelection selection) {
		IJavaElement jelem = getInitialJavaElement(selection);
		initComponentContainerPage(selection, jelem);
		initContainerPage(jelem);
		initTypePage(jelem);
		doStatusUpdate();
		boolean createMain = false;
		boolean createConstructors = true;
		boolean createUnimplemented = true;
		IDialogSettings dialogSettings = getDialogSettings();
		if (dialogSettings != null) {
			IDialogSettings section = dialogSettings.getSection("NewClassWizardPage");
			if (section != null) {
				//createMain = section.getBoolean("create_main");
				createConstructors = section.getBoolean(SETTINGS_CREATECONSTR);
				createUnimplemented = section.getBoolean(SETTINGS_CREATEUNIMPLEMENTED);
			}
		}
		setMethodStubSelection(createMain, createConstructors, createUnimplemented, true);
	}

	protected void initComponentContainerPage(IStructuredSelection selection, IJavaElement jelem) {

		IProject project = null;
		IResource resource = null;

		if (jelem != null) {
			project = jelem.getJavaProject().getProject();
			if (jelem.getElementType() != IJavaElement.JAVA_PROJECT) {
				if (isFragmentRootAndResourcesCompatible(jelem)) {
					try {
						resource = jelem.getCorrespondingResource();
					} catch (JavaModelException e) {
						System.err.println(getClass().getName() + ".initComponentContainerPage(IStructuredSelection,IJavaElement)");
						e.printStackTrace(System.err);
					}
				}
			}
			else if (selection != null && !selection.isEmpty()) {
				Object el = selection.getFirstElement();
				if (el instanceof IResource) {
					resource = (IResource) el;
					project = resource.getProject();
					if (resource instanceof IFile) {
						resource = ((IFile)resource).getParent();
					}
					else if (resource.getType() != IResource.FOLDER) {
						resource = null;
					}
				}
			}
		}

		IFolder componentFolder = null;
		if (resource instanceof IFolder) {
			componentFolder = (IFolder) resource;
			IPath componentFolderPath = componentFolder.getFullPath();
			if (isReservedSourcesPath(componentFolderPath)) {
				componentFolder = null;
			}
		}

		if (componentFolder == null) {
			if (getComponentContainer() != null) {
				componentFolder = getComponentContainer();
			}
			else if (selection != null) {
				for (String container : PREFERRED_COMPONENT_CONTAINER_PATHS) {
					IResource el = project.findMember(container);
					if (el instanceof IFolder) {
						componentFolder = (IFolder) el;
						break;
					}
				}
			}
		}
		setComponentContainerRoot(componentFolder, true);
	}

	protected void initContainerPage(IJavaElement elem) {
		IPackageFragmentRoot initRoot = null;
		if (elem != null) {
			try {
				initRoot = JavaModelUtil.getPackageFragmentRoot(elem);

				IPath initRootPath = initRoot == null ? null : initRoot.getPath();
				if (isReservedResourcesPath(initRootPath)) {
					initRoot = null;
					initRootPath = null;
				}

				if (initRoot == null) {
					IJavaProject jproject = elem.getJavaProject();
					if (jproject != null && jproject.exists()) {
						IPackageFragmentRoot roots[] = jproject.getPackageFragmentRoots();
						CONTAINERS: for (String root : PREFERRED_SOURCE_CONTAINER_PATHS) {
							for (int i = 0; i < roots.length; i++) {
								if (roots[i].getKind() != IPackageFragmentRoot.K_SOURCE) {
									IPath rootPath = roots[i].getPath().makeRelative().removeFirstSegments(1);
									if (!root.equals(rootPath.toString())) {
										continue;
									}
								}
								initRoot = roots[i];
								break CONTAINERS;
							}
						}
					}
				}
			}
			catch (JavaModelException e) {
				JavaPlugin.log(e);
			}
		}
		setPackageFragmentRoot(initRoot, true);
	}

	protected void initSuperInterfaces(IJavaElement elem) {
		if (isSuperInterfacesEnabled() && (getSuperInterfaces() == null || getSuperInterfaces().size() == 0)) {
			List<String> interfaces = new ArrayList<String>();
			if (elem instanceof ICompilationUnit) {
				ICompilationUnit unit = (ICompilationUnit) elem;

				try {
					if (elem.getElementType() == IJavaElement.COMPILATION_UNIT) {
						IType primaryType = unit.findPrimaryType();
						if (primaryType.exists() && primaryType.isInterface()) {
							String interfaceName = SuperInterfaceSelectionDialog.getNameWithTypeParameters(primaryType);
							if (interfaceName != null) {
								interfaces.add(interfaceName);
							}
						}
					}

				} catch (JavaModelException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			if (interfaces.size() > 0) {
				setSuperInterfaces(interfaces, true);
			}
		}
	}

	@Override
	protected void initTypePage(IJavaElement elem) {
		super.initTypePage(elem);

		if (getJavaProject() != null) {

			// auto-populate superclass from selection (if WOComponent assignable)
			if (elem instanceof ICompilationUnit) {
				ICompilationUnit unit = (ICompilationUnit) elem;
				try {
					IType primaryType = unit.findPrimaryType();
					if (primaryType != null) {
						ITypeHierarchy supertypeHierarchy = primaryType.newSupertypeHierarchy(new NullProgressMonitor());
						if (supertypeHierarchy.contains(getDefaultTBComponentType())) {
							setSuperClass(primaryType.getFullyQualifiedName(), true);
						}
					}
				} catch (JavaModelException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

			// auto-populate implementing interface (if selected)
			initSuperInterfaces(elem);

			// if the package fragment isn't set default to Main.class's package 
			if (getPackageFragment() == null || getPackageFragment().isDefaultPackage()) {
				IPackageFragmentRoot froot = getPackageFragmentRoot();
				if (froot != null) {
					IJavaElement compilationUnit = findMainClass(froot);
					if (compilationUnit instanceof ICompilationUnit) {
						IType type = ((ICompilationUnit) compilationUnit).findPrimaryType();
						if (type != null) {
							setPackageFragment(type.getPackageFragment(), true);
						}
					}
				}
			}
		}
		// if the superclass isn't initialised set the default
		boolean requiresSuperclass = getSuperClass() == null || getSuperClass().matches("\\s*");
		if (!requiresSuperclass) {
			requiresSuperclass = true;
			try {
				IType supertype = getJavaProject().findType(getSuperClass());
				ITypeHierarchy supertypeHierarchy = supertype.newSupertypeHierarchy(new NullProgressMonitor());
				if (supertypeHierarchy.contains(getDefaultTBComponentType())) {
					requiresSuperclass = false;
				}
			} catch (JavaModelException e) {
				// nothing to do, invalid type
			} catch (NullPointerException e) {
				// nothing to do, invalid type
			}
		}
		if (requiresSuperclass) {
			setSuperClass(DEFAULT_SUPERCLASS_NAME, true);
		}
	}

	public boolean isCreateConstructors() {
		return fMethodStubsButtons.isSelected(1);
	}

	public boolean isCreateInherited() {
		return fMethodStubsButtons.isSelected(2);
	}

	public boolean isCreateMain() {
		return false;
	}

	private boolean isFragmentRootAndResourcesCompatible(IJavaElement el) {
		boolean result = false;
		if (el instanceof IPackageFragmentRoot) {
			IPackageFragmentRoot froot = (IPackageFragmentRoot) el;
			IPath frootPath = froot.getPath();

			if (!isReservedResourcesPath(frootPath)) {
				result = true;
				try {
					for (IJavaElement child : froot.getChildren()) {
						if (child instanceof IPackageFragment || child instanceof ICompilationUnit) {
							result = false;
							break;
						}
					}
				} catch (JavaModelException e) {
					System.err.println(getClass().getName() + ".isFragmentRootAndResourcesCompatible(IJavaElement)");
					e.printStackTrace(System.err);
					result = false;
				}
			}
		}
		return result;
	}

	protected boolean isSuperInterfacesEnabled() {
		return false;
	}



	private void prepareFolder(IFolder folder, IProgressMonitor progressMonitor) throws CoreException {
		if (!folder.exists()) {
			IContainer parent = folder.getParent();
			if (parent instanceof IFolder) {
				prepareFolder((IFolder) parent, progressMonitor);
			}
			folder.create(false, true, progressMonitor);
		} else {
			progressMonitor.worked(1);
		}
	}

	protected void refreshButtonSettings(Button button) {
		if (button != null) {
			if (button.equals(this.fComponentWOOCheckbox)) {
				this.fComponentWOOEncodingCombo.setEnabled(this.fComponentWOOCheckbox.getSelection());
			}
			else if (button.equals(this.fComponentAPICheckbox)) {
				this.vComponentAPIEnabled = fComponentAPICheckbox.getSelection();
			}
		}
	}



	/**
	 * @param folder
	 * @param canBeModified
	 */
	private void setComponentContainerRoot(IFolder folder, boolean canBeModified) {
		this.vComponentContainerRoot = folder;
		StringBuilder path = new StringBuilder();
		if (folder != null) {
			path.append(folder.getProject().getName());
			path.append(folder.getProject().getProjectRelativePath().addTrailingSeparator());
			path.append(folder.getProjectRelativePath());
		}

		fComponentContainerDialogField.setText(path.toString());
		fComponentContainerDialogField.setEnabled(canBeModified);
	}

	protected void setComponentViewEnabled(boolean enabled) {
		//		if (enabled) {
		//			this.componentViewTitle.setBackground(componentViewTitle.getDisplay().getSystemColor(SWT.COLOR_TITLE_BACKGROUND_GRADIENT));
		//			this.componentViewTitleLabel.setForeground(componentViewTitle.getDisplay().getSystemColor(SWT.COLOR_TITLE_FOREGROUND));
		//		} else {
		//			this.componentViewTitle.setBackground(componentViewTitle.getDisplay().getSystemColor(SWT.COLOR_TITLE_INACTIVE_BACKGROUND));
		//			this.componentViewTitleLabel.setForeground(componentViewTitle.getDisplay().getSystemColor(SWT.COLOR_TITLE_INACTIVE_FOREGROUND));
		//		}

		fComponentAPICheckbox.setEnabled(enabled);
		this.fComponentContainerDialogField.setEnabled(enabled);
		this.fComponentHTMLCombo.setEnabled(enabled);

		//		this.fComponentWODCheckbox.setEnabled(enabled);
		this.fComponentWOOCheckbox.setEnabled(enabled);
		if (enabled) {
			refreshButtonSettings(this.fComponentWOOCheckbox);
		} else {
			this.fComponentWOOEncodingCombo.setEnabled(enabled);
		}
	}

	private void setComponentWOOEncodingKey(String encodingKey) {
		this.vComponentWOOEncodingKey = encodingKey;
		this.vComponentWOOEnabled = encodingKey != null && !encodingKey.matches("^\\s*$");
	}

	public void setMethodStubSelection(boolean createMain, boolean createConstructors, boolean createInherited, boolean canBeModified) {
		//fMethodStubsButtons.setSelection(0, createMain);
		fMethodStubsButtons.setSelection(0, createConstructors);
		fMethodStubsButtons.setSelection(1, createInherited);
		fMethodStubsButtons.setEnabled(canBeModified);
	}

	public void setVisible(boolean visible) {
		super.setVisible(visible);
		if (visible) {
			setFocus();
		} else {
			IDialogSettings dialogSettings = getDialogSettings();
			if (dialogSettings != null) {
				IDialogSettings section = dialogSettings.getSection("NewClassWizardPage");
				if (section == null)
					section = dialogSettings.addNewSection("NewClassWizardPage");
				section.put(SETTINGS_CREATECONSTR, isCreateConstructors());
				section.put(SETTINGS_CREATEUNIMPLEMENTED, isCreateInherited());
			}
		}
	}


	@Override
	protected IStatus typeNameChanged() {
		IStatus typeNameStatus = super.typeNameChanged();

		if (typeNameStatus.getSeverity() != IStatus.ERROR && getTypeName() != null && !getTypeName().matches("\\s*")) {
			if (getComponentContainer() == null) {
				if (typeNameStatus.getSeverity() != IStatus.WARNING) {
					typeNameStatus = new StatusInfo(IStatus.WARNING, "The component source folder is required");
				}
			}
			else {
				String componentName = getTypeName() + ".wo";
				try {
					for (IResource member : getComponentContainer().members()) {
						if (member.getName().equalsIgnoreCase(componentName)) {
							String typeName = "component";
							if (member.getType() == IResource.FILE) {
								typeName = "file";
							}
							typeNameStatus = new StatusInfo(IStatus.ERROR, "The component name matches an existing " + typeName);
						}
					}
				} catch (CoreException e) {
					System.err.println(getClass().getName() + ".typeNameChanged() failed to iterate members");
					e.printStackTrace(System.err);
				}
			}
		}
		return typeNameStatus;
	}

}
