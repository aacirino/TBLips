package eomodeldoc;

import java.io.File;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.eclipse.core.runtime.NullProgressMonitor;
import org.objectstyle.wolips.eomodeler.core.model.EOModelGroup;
import org.objectstyle.wolips.eomodeler.core.model.EOModelVerificationFailure;
import org.objectstyle.wolips.eomodeler.doc.EOModelDocGenerator;
import org.objectstyle.wolips.eomodeler.factories.EclipseProjectEOModelGroupFactory;
import org.objectstyle.wolips.eomodeler.factories.IDEAProjectEOModelGroupFactory;
import org.objectstyle.wolips.eomodeler.factories.SimpleManifestEOModelGroupFactory;

public class EOModelDoc {
  @SuppressWarnings("deprecation")
	public static void main(String[] args) throws Exception {
    System.setProperty("javax.xml.xpath.XPathFactory:http://java.sun.com/jaxp/xpath/dom", "com.sun.org.apache.xpath.internal.jaxp.XPathFactoryImpl");
    File modelGroupFolder = new File(".");
    File outputFolder = null;
    File templateFolder = null;
    String entityURLTemplate = null;
    List<String> modelPaths = new LinkedList<String>();
    for (int argNum = 0; argNum < args.length; argNum++) {
      String arg = args[argNum];
      if ("-model".equals(arg)) {
        modelPaths.add(args[++argNum]);
      }
      else if ("-modelgroup".equals(arg)) {
        modelGroupFolder = new File(args[++argNum]);
      }
      else if ("-templates".equals(arg)) {
        templateFolder = new File(args[++argNum]);
      }
      else if ("-output".equals(arg)) {
        outputFolder = new File(args[++argNum]);
      }
      else if ("-entityURLTemplate".equals(arg)) {
        entityURLTemplate = args[++argNum];
      }
    }
    
    if (outputFolder == null) {
      System.out.println("eomodeldoc -output /path/to/output/folder [-model /path/to/model.eomodeld]* [-modelgroup /path/to/working/dir] [-templates /path/to/templates] [-entityURLTemplate \"http://whatever?${entity.classNamePath}.html\"");
      System.exit(0);
    }

    EOModelGroup modelGroup = new EOModelGroup();
    Set<EOModelVerificationFailure> failures = new HashSet<EOModelVerificationFailure>();
    if (modelPaths.size() == 0) {
      new SimpleManifestEOModelGroupFactory().loadModelGroup(modelGroupFolder, modelGroup, failures, true, new NullProgressMonitor());
      new IDEAProjectEOModelGroupFactory().loadModelGroup(modelGroupFolder, modelGroup, failures, true, new NullProgressMonitor());
      new EclipseProjectEOModelGroupFactory().loadModelGroup(modelGroupFolder, modelGroup, failures, true, new NullProgressMonitor());
    }
    else {
      for (String modelPath : modelPaths) {
        modelGroup.loadModelFromURL(new File(modelPath).toURL());
      }
    }
    modelGroup.resolve(failures);
    modelGroup.verify(failures);
    EOModelDocGenerator.generate(modelGroup, outputFolder, templateFolder, entityURLTemplate);
  }
}
