package org.objectstyle.wolips.eomodeler.factories;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import java.util.Set;

import org.objectstyle.wolips.baseforplugins.util.URLUtils;
import org.objectstyle.wolips.eomodeler.core.model.AbstractEOClassLoader;
import org.objectstyle.wolips.eomodeler.core.model.EOModel;

public class SimpleManifestEOClassLoaderFactory extends AbstractEOClassLoader {
	@Override
	protected void fillInDevelopmentClasspath(Set<URL> classpathUrls) throws Exception {
		// DO NOTHING
	}

	@Override
	protected void fillInModelClasspath(EOModel model, Set<URL> classpathUrls) throws Exception {
		File modelFolder = URLUtils.cheatAndTurnIntoFile(model.getModelURL()).getParentFile();
		fillInClasspathURLs(new File(modelFolder, "EntityModeler.classpath"), classpathUrls);
		fillInClasspathURLs(new File(modelFolder, ".EntityModeler.classpath"), classpathUrls);
		for (File modelParentFolder = modelFolder.getParentFile(); modelParentFolder != null; modelParentFolder = modelParentFolder.getParentFile()) {
			fillInClasspathURLs(new File(modelParentFolder, "EntityModeler.classpath"), classpathUrls);
			fillInClasspathURLs(new File(modelParentFolder, ".EntityModeler.classpath"), classpathUrls);
		}
		fillInClasspathURLs(new File(System.getProperty("user.home"), "EntityModeler.classpath"), classpathUrls);
		fillInClasspathURLs(new File(System.getProperty("user.home"), ".EntityModeler.classpath"), classpathUrls);
		fillInClasspathURLs(new File(System.getProperty("user.home") + "/Library", "EntityModeler.classpath"), classpathUrls);
		fillInClasspathURLs(new File(System.getProperty("user.home") + "/Library", ".EntityModeler.classpath"), classpathUrls);
		fillInClasspathURLs(new File(System.getProperty("user.home") + "/Library/Preferences", "EntityModeler.classpath"), classpathUrls);
		fillInClasspathURLs(new File(System.getProperty("user.home") + "/Library/Preferences", ".EntityModeler.classpath"), classpathUrls);
	}

	protected void addClasspathURL(File manifestItem, final Set<URL> classpathUrls) throws IOException {
		File searchFolder = manifestItem.getCanonicalFile();
		if (searchFolder != null && searchFolder.exists()) {
			if (searchFolder.getName().endsWith(".framework")) {
				File javaFolder = new File(searchFolder, "Resources/Java");
				if (javaFolder.exists()) {
					classpathUrls.add(javaFolder.toURL());
					File[] jarFiles = javaFolder.listFiles();
					for (File jarFile : jarFiles) {
						if (jarFile.getName().toLowerCase().endsWith(".jar")) {
							System.out.println("SimpleManifestEOClassLoaderFactory.fillInClasspathURLs:   jar = " + jarFile);
							classpathUrls.add(jarFile.toURL());
						}
					}
				}
			} else {
				classpathUrls.add(searchFolder.toURL());
			}
		}
	}
	
	protected void fillInClasspathURLs(File manifestFile, final Set<URL> classpathUrls) throws IOException {
		if (manifestFile.exists()) {
			System.out.println("SimpleManifestEOClassLoaderFactory.fillInClasspathURLs: Manifest = " + manifestFile + " ...");
			BufferedReader manifestReader = new BufferedReader(new FileReader(manifestFile));
			try {
				String searchFolderPath;
				while ((searchFolderPath = manifestReader.readLine()) != null) {
					if (searchFolderPath.contains("*")) {
						SimpleManifestUtilities.fillInSearchFolders(manifestFile.getParentFile(), searchFolderPath, new SimpleManifestUtilities.SearchFolderDelegate() {
							public void fileMatched(File file) throws IOException {
								addClasspathURL(file, classpathUrls);
							}
						});
					}
					else {
						addClasspathURL(new File(searchFolderPath), classpathUrls);
					}
				}
			} finally {
				manifestReader.close();
			}
		}
	}
}
