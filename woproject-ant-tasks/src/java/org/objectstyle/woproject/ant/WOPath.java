/* ====================================================================
 *
 * The ObjectStyle Group Software License, Version 1.0
 *
 * Copyright (c) 2002 - 2006 The ObjectStyle Group
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

import java.util.LinkedList;
import java.util.List;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.types.Path;
import org.apache.tools.ant.types.ResourceCollection;
import org.objectstyle.woenvironment.env.WOEnvironment;


/**
 * A subclass of Path with support for frameworks.
 *
 * @author Chuck Hill
 */
public class WOPath extends Path {
  private List<FrameworkSet> _frameworkSets;
  
    /**
     * Invoked by IntrospectionHelper for <code>setXXX(Path p)</code>
     * attribute setters.
     * @param aProject the <CODE>Project</CODE> for this path.
     * @param path the <CODE>String</CODE> path definition.
     */
    public WOPath(Project aProject, String path) {
        super(aProject, path);
        _frameworkSets = new LinkedList<FrameworkSet>();
    }

    /**
     * Construct an empty <CODE>Path</CODE>.
     * @param aProject the <CODE>Project</CODE> for this path.
     */
    public WOPath(Project aProject) {
        super(aProject);
        _frameworkSets = new LinkedList<FrameworkSet>();
    }
    
    /**
     * Adds a nested <code>&lt;framework&gt;</code> element.
     * @param framework FrameworkSet to add as a FileSet
     * @throws BuildException
     */
    public void addFrameworks(FrameworkSet framework) throws BuildException {
      _frameworkSets.add(framework);
      addFileset(framework);
    }

    // MS: TOTAL HACK ... I don't know where to hook in to expand FrameworkSets into jars, but this
    // seems to be called before it's used, so I think this will do it
    @Override
    protected ResourceCollection assertFilesystemOnly(ResourceCollection rc) {
      add(FrameworkSet.jarsPathForFrameworkSets(getProject(), _frameworkSets, new WOEnvironment(getProject().getProperties()).getWOVariables()));
      return super.assertFilesystemOnly(rc);
    }
    
    @Override
    public String toString() {
      return super.toString();
    }
}
