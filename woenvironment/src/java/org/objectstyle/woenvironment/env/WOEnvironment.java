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

package org.objectstyle.woenvironment.env;

import java.io.File;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;

/**
 * @author uli
 * 
 * To prevent static variables create an instance of WOEnvironment to access the
 * environment and WOVariables.
 */

public final class WOEnvironment {
  private WOVariables woVariables;

  public WOEnvironment() {
    this.woVariables = new WOVariables(null);
  }
  
  public WOEnvironment(Map<Object, Object> existingProperties) {
    this.woVariables = new WOVariables(existingProperties);
  }
  
  public WOEnvironment(Hashtable<String, Object> existingProperties) {
    Map<Object, Object> propertiesMap = new HashMap<Object, Object>();
    for (String key : existingProperties.keySet()) {
      propertiesMap.put(key, existingProperties.get(key));
    }
    this.woVariables = new WOVariables(propertiesMap);
  }
  
  public WOEnvironment(WOVariables variables, Map<Object, Object> existingProperties) {
    this.woVariables = new WOVariables(variables, existingProperties);
  }

  /**
   * @return WOVariables
   */
  public WOVariables getWOVariables() {
    return this.woVariables;
  }

  /**
   * Method wo5or51 returns true if the installe WO version is 5.0 or 5.1.
   * 
   * @return boolean
   */
  public boolean wo5or51() {
    return (this.bootstrap() == null);
  }

  /**
   * Method wo52 returns true if the installe WO version is 5.2.
   * 
   * @return boolean
   */
  public boolean wo52() {
    return !this.wo5or51();
  }

  /**
   * Method bootstrap returns the bootstrap.jar if it exists.
   * 
   * @param project
   * @return File
   */
  public File bootstrap() {
    String bootstrapJarPath = getWOVariables().boostrapJar();
    File bootstrapJar = null;
    if (bootstrapJarPath != null) {
      bootstrapJar = new File(bootstrapJarPath);
      if (!bootstrapJar.exists()) {
        bootstrapJar = null;
      }
    }
    return bootstrapJar;
  }

  public boolean variablesConfigured() {
    return getWOVariables().systemRoot() != null && getWOVariables().localRoot() != null;
  }
}
