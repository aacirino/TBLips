//
// ${entity.prefixClassNameWithoutPackage}.java
//
// DO NOT EDIT. 
// Make changes to ${entity.classNameWithOptionalPackage}.java instead.
//
// version 7.1.3
//
// 2008-2014 by TreasureBoat.org
//

#if (!$entity.partialEntitySet)
/*
 * The following List is for Localization :
 * ----------------------------------------

  "Pages.Query${entity.classNameWithOptionalPackage}" = "Query ${entity.classNameWithOptionalPackage}";
  "Pages.List${entity.classNameWithOptionalPackage}" = "List ${entity.classNameWithOptionalPackage}";
  "Pages.Inspect${entity.classNameWithOptionalPackage}" = "Inspect ${entity.classNameWithOptionalPackage}";
  "Pages.Edit${entity.classNameWithOptionalPackage}" = "Edit ${entity.classNameWithOptionalPackage}";
  "Pages.Create${entity.classNameWithOptionalPackage}" = "Create ${entity.classNameWithOptionalPackage}";
  
  "Pages.QueryEmbedded${entity.classNameWithOptionalPackage}" = "Query Embedded ${entity.classNameWithOptionalPackage}";
  "Pages.ListEmbedded${entity.classNameWithOptionalPackage}" = "List Embedded ${entity.classNameWithOptionalPackage}";
  "Pages.InspectEmbedded${entity.classNameWithOptionalPackage}" = "Inspect Embedded ${entity.classNameWithOptionalPackage}";
  "Pages.EditEmbedded${entity.classNameWithOptionalPackage}" = "Edit Embedded ${entity.classNameWithOptionalPackage}";
  "Pages.CreateEmbedded${entity.classNameWithOptionalPackage}" = "Create Embedded ${entity.classNameWithOptionalPackage}";

#foreach ($attribute in $entity.sortedClassAttributes)
  "PropertyKey.${attribute.name}" = "$attribute.name";
#end

#foreach ($relationship in $entity.sortedClassRelationships)
  "PropertyKey.${relationship.name}" = "$relationship.name";
#end
 * ----------------------------------------
 */

/*
 * The following List is for D2W/D3W :
 * ----------------------------------------

  // D2W

  300 : entity.name = '${entity.classNameWithOptionalPackage}' => isEntityEditable = "true" [com.webobjects.directtoweb.BooleanAssignment]

  100 : pageConfiguration like '*${entity.classNameWithOptionalPackage}' => navigationState = "XX.xx" [com.webobjects.directtoweb.Assignment]
  105 : pageConfiguration = 'Create${entity.classNameWithOptionalPackage}' => navigationState = "XX.xx.create${entity.classNameWithOptionalPackage}" [com.webobjects.directtoweb.Assignment]
  105 : pageConfiguration = 'Query${entity.classNameWithOptionalPackage}' => navigationState = "XX.xx.query${entity.classNameWithOptionalPackage}" [com.webobjects.directtoweb.Assignment]
  
  401 : pageConfiguration = 'Query${entity.classNameWithOptionalPackage}' => displayPropertyKeys = ("[MainTab]", "(MainSection)"#foreach ($attribute in $entity.sortedClassAttributes), "$attribute.name" #end #foreach ($relationship in $entity.sortedClassRelationships), "$relationship.name" #end) [com.webobjects.directtoweb.Assignment] 
  402 : pageConfiguration = 'List${entity.classNameWithOptionalPackage}' => displayPropertyKeys = ("[MainTab]", "(MainSection)"#foreach ($attribute in $entity.sortedClassAttributes), "$attribute.name" #end #foreach ($relationship in $entity.sortedClassRelationships), "$relationship.name" #end) [com.webobjects.directtoweb.Assignment] 
  403 : pageConfiguration = 'Inspect${entity.classNameWithOptionalPackage}' => displayPropertyKeys = ("[MainTab]", "(MainSection)"#foreach ($attribute in $entity.sortedClassAttributes), "$attribute.name" #end #foreach ($relationship in $entity.sortedClassRelationships), "$relationship.name" #end) [com.webobjects.directtoweb.Assignment] 
  404 : pageConfiguration = 'Edit${entity.classNameWithOptionalPackage}' => displayPropertyKeys = ("[MainTab]", "(MainSection)"#foreach ($attribute in $entity.sortedClassAttributes), "$attribute.name" #end #foreach ($relationship in $entity.sortedClassRelationships), "$relationship.name" #end) [com.webobjects.directtoweb.Assignment] 
  405 : pageConfiguration = 'Create${entity.classNameWithOptionalPackage}' => displayPropertyKeys = ("[MainTab]", "(MainSection)"#foreach ($attribute in $entity.sortedClassAttributes), "$attribute.name" #end #foreach ($relationship in $entity.sortedClassRelationships), "$relationship.name" #end) [com.webobjects.directtoweb.Assignment] 
   
  // D3W

  200 : EN = '#' => crud = "0100" [com.webobjects.directtoweb.Assignment]
  600 : PC = 'EditRelationshipEmbedded#' => cruds = "00001" [com.webobjects.directtoweb.Assignment]
  
  401 : PC = 'Query#' => DPK = ("[MainTab]", "(MainSection)"#foreach ($attribute in $entity.sortedClassAttributes), "$attribute.name"#end#foreach($relationship in $entity.sortedClassRelationships), "$relationship.name" #end) [com.webobjects.directtoweb.Assignment] 
  402 : PC = 'List#' => DPK = ("[MainTab]", "(MainSection)"#foreach ($attribute in $entity.sortedClassAttributes), "$attribute.name"#end#foreach($relationship in $entity.sortedClassRelationships), "$relationship.name" #end) [com.webobjects.directtoweb.Assignment] 
  403 : PC = 'Inspect#' => DPK = ("[MainTab]", "(MainSection)"#foreach ($attribute in $entity.sortedClassAttributes), "$attribute.name"#end#foreach($relationship in $entity.sortedClassRelationships), "$relationship.name" #end) [com.webobjects.directtoweb.Assignment] 
  404 : PC = 'Edit#' => DPK = ("[MainTab]", "(MainSection)"#foreach ($attribute in $entity.sortedClassAttributes), "$attribute.name"#end#foreach($relationship in $entity.sortedClassRelationships), "$relationship.name" #end) [com.webobjects.directtoweb.Assignment] 
  405 : PC = 'Create#' => DPK = ("[MainTab]", "(MainSection)"#foreach ($attribute in $entity.sortedClassAttributes), "$attribute.name"#end#foreach($relationship in $entity.sortedClassRelationships), "$relationship.name" #end) [com.webobjects.directtoweb.Assignment] 

 * ----------------------------------------
 */
#end

#if ($entity.superclassPackageName)
package ${entity.superclassPackageName};

#end
import ${entity.superclassPackageName}.${entity.classNameWithOptionalPackage};

import java.util.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.treasureboat.enterprise.eof.TBEOExternalPrimaryKeyHelper;
import org.treasureboat.enterprise.eof.TBEOGenericRecord;
import org.treasureboat.enterprise.eof.delete.ITBEnterpriseVirtualDeleteSupport;
import org.treasureboat.foundation.crypting.TBFCrypto;
import org.treasureboat.webcore.appserver.TBSession;
import org.treasureboat.webcore.concurrency.TBWConcurrencyUtilities;
import org.treasureboat.webcore.override.core.TBWCoreQualifierBase;
import org.treasureboat.webcore.security.domain.ITBWDomain;
import org.treasureboat.webcore.security.domain.TBWMultiDomainSupport;
import org.treasureboat.webcore.security.grant.TBWGrantAccess;
import org.treasureboat.webcore.security.password.TBWAccessPermission;

import com.webobjects.eoaccess.EOEntity;
import com.webobjects.eoaccess.EOUtilities;
import com.webobjects.eocontrol.*;
import com.webobjects.foundation.*;

import er.extensions.eof.ERXEOAccessUtilities;
import er.extensions.eof.ERXFetchSpecification;
import er.extensions.eof.ERXGenericRecord;
import er.extensions.eof.ERXKey;
import er.extensions.qualifiers.ERXAndQualifier;
import er.extensions.validation.ERXValidationException;

#if ($entity.parentSet)
    #set ($parentClass = ${entity.parent.classNameWithDefault})
    #set ($parentClazzClass = "${entity.parent.classNameWithDefault}.${entity.parent.classNameWithoutPackage}Clazz<T>")
#else
    #set ($parentClass = "ERXGenericRecord")
    #set ($parentClazzClass = "ERXGenericRecord.ERXGenericRecordClazz<T>")
#end
@SuppressWarnings("all")
public abstract class ${entity.prefixClassNameWithoutPackage} extends #if ($entity.parentClassNameSet)${entity.parentClassName}#elseif ($entity.partialEntitySet)er.extensions.partials.ERXPartial<${entity.partialEntity.className}>#elseif ($entity.parentSet)${entity.parent.classNameWithDefault}#elseif ($EOGenericRecord)${EOGenericRecord}#else TBEOGenericRecord#end {

  private static final long serialVersionUID = 1L;

  /** 
   * <a href="http://wiki.wocommunity.org/display/documentation/Wonder+Logging">new org.slf4j.Logger</a> 
   */
  static final Logger log = LoggerFactory.getLogger(${entity.classNameWithoutPackage}.class);

#if (!$entity.partialEntitySet)
  //********************************************************************
  //  Constructor : コンストラクター
  //********************************************************************

  public ${entity.prefixClassNameWithoutPackage}() {
    super();
    
    addFetchSpecificationToEntity();
  }

  //********************************************************************
  //  D2W Fetchspecification : D2W 用フェッチ・スペシフィケーション
  //********************************************************************

  /** 
   * add FetchSpecifications that are coming from the EO Model
   * 
   * エンティティにフェッチ・スペシフィケーションを追加バインディングします 
   */
  public void addFetchSpecificationToEntity() {
    if(_addFetchSpecificationToEntity == null) {
      addFetchSpecification();
      _addFetchSpecificationToEntity = Boolean.TRUE;
    }
  }
  private static Boolean _addFetchSpecificationToEntity = null;
  
  protected void addFetchSpecification() {}
 #end 
 
  //********************************************************************
  //  setup configuration : セットアップ定義
  //********************************************************************

  protected boolean useCoreQualifierForToManyRelationships() {
   return false;
  }
 
  //********************************************************************
  //  Entity : エンティティ
  //********************************************************************
  
#if ($entity.partialEntitySet)
  /** Entity Name = $entity.partialEntity.name */
  public static final String ENTITY_NAME = "$entity.partialEntity.name";
#else
  /** Entity Name = $entity.name */
  public static final String ENTITY_NAME = "$entity.name";
#end

#if (!$entity.partialEntitySet)
  //********************************************************************
  //  Access Properties : アクセス・プロパティ
  //********************************************************************
  
  protected static String ACCSESS_CREATE = "${entity.name}.create";
  protected static String ACCSESS_READ = "${entity.name}.read";
  protected static String ACCSESS_UPDATE = "${entity.name}.update";
  protected static String ACCSESS_DELETE = "${entity.name}.delete";

  /**
   * you can override, TBWGrantAccess or use the role system
   * 
   * @return true if it allowed to create Data
   */
  public static boolean canCreate() {
    // Check Grant for Batch Processing
    if(TBWGrantAccess.isEntityGrantForCreate(ENTITY_NAME)) {
      return true;
    }
    return TBWAccessPermission.instance().can(ACCSESS_CREATE);
  }

  /**
   * you can override or use the role system
   * 
   * @return true if it allowed to read Data
   */
  public boolean canRead() {
    return TBWAccessPermission.instance().can(ACCSESS_READ);
  }
  
  /** 
   * you can override, TBWGrantAccess or use the role system
   * 
   * @return true if it allowed to update Data
   */
  @Override
  public boolean canUpdate() {
    // Check Grant for Batch Processing
    if(TBWGrantAccess.isEntityGrantForUpdate(ENTITY_NAME)) {
      return true;
    }
    return TBWAccessPermission.instance().can(ACCSESS_UPDATE);
  }
  
  /** 
   * you can override, TBWGrantAccess or use the role system
   * 
   * @return true if it allowed to delete Data
   */
  @Override
  public boolean canDelete() {
    // Check Grant for Batch Processing
    if(TBWGrantAccess.isEntityGrantForDelete(ENTITY_NAME)) {
      return true;
    } 
    return TBWAccessPermission.instance().can(ACCSESS_DELETE);
  }
#end

  //********************************************************************
  //  Attribute : アトリビュート
  //********************************************************************

  // Attribute Keys
#foreach ($attribute in $entity.sortedClassAttributes)
  public static final ERXKey<$attribute.javaClassName> ${attribute.uppercaseUnderscoreName} = new ERXKey<$attribute.javaClassName>("$attribute.name");
#end

  // Attributes
#foreach ($attribute in $entity.sortedClassAttributes)
  public static final String ${attribute.uppercaseUnderscoreName}_KEY = ${attribute.uppercaseUnderscoreName}.key();
#end

  //********************************************************************
  //  Relationship : リレーションシップ
  //********************************************************************

  // Relationship Keys
#foreach ($relationship in $entity.sortedClassRelationships)
  public static final ERXKey<$relationship.actualDestination.classNameWithDefault> ${relationship.uppercaseUnderscoreName} = new ERXKey<$relationship.actualDestination.classNameWithDefault>("$relationship.name");
#end

  // Relationships
#foreach ($relationship in $entity.sortedClassRelationships)
  public static final String ${relationship.uppercaseUnderscoreName}_KEY = ${relationship.uppercaseUnderscoreName}.key();
#end

#if (!$entity.partialEntitySet)
  //********************************************************************
  //  clazz methods : クラス・メソッド
  //********************************************************************

  public static class _${entity.classNameWithoutPackage}Clazz<T extends ${entity.classNameWithOptionalPackage}> extends ${parentClazzClass} {
    /* more clazz methods here */
  }

	public ${entity.classNameWithOptionalPackage}.${entity.classNameWithoutPackage}Clazz clazz() {
    return ${entity.classNameWithOptionalPackage}.clazz;
  }
#else
  //********************************************************************
  //  partial methods : 部分EOメソッド
  //********************************************************************
  
  public static NSArray<String> _partialAttributes = null;
  public static NSArray<String> _partialRelationships = null;
  
  public static NSArray<String> partialAttributes() {
    if ( _partialAttributes == null ) {
      synchronized(ENTITY_NAME) {
        NSMutableArray<String> partialList = new NSMutableArray<String>();
#foreach ($attribute in $entity.sortedClassAttributes)
        partialList.addObject( ${attribute.uppercaseUnderscoreName}_KEY );
#end
        _partialAttributes = partialList.immutableClone();
      }
    }
    return _partialAttributes;
  }

  public static NSArray<String> partialRelationships() {
    if ( _partialRelationships == null ) {
      synchronized(ENTITY_NAME) {
        NSMutableArray<String> partialList = new NSMutableArray<String>();
#foreach ($relationship in $entity.sortedClassRelationships)
        partialList.addObject( ${relationship.uppercaseUnderscoreName}_KEY );
#end
        _partialRelationships = partialList.immutableClone();
      }
    }
    return _partialRelationships;
  }
#end

  //********************************************************************
  //  Attribute Accessor : アトリビュート・アクセス
  //********************************************************************

#foreach ($attribute in $entity.sortedClassAttributes)
#if (!$attribute.inherited)
#if ($attribute.userInfo.ERXConstantClassName)
  public $attribute.userInfo.ERXConstantClassName ${attribute.name}() {
    Number value = (Number)storedValueForKey(${attribute.uppercaseUnderscoreName}_KEY);
    return ($attribute.userInfo.ERXConstantClassName)value;
  }

  public void set${attribute.capitalizedName}($attribute.userInfo.ERXConstantClassName value) {
    takeStoredValueForKey(value, ${attribute.uppercaseUnderscoreName}_KEY);
  }
#else
  public $attribute.javaClassName ${attribute.name}() {
    return ($attribute.javaClassName) storedValueForKey(${attribute.uppercaseUnderscoreName}_KEY);
  }

  public void set${attribute.capitalizedName}($attribute.javaClassName value) {
    if (${entity.prefixClassNameWithoutPackage}.log.isDebugEnabled()) {
      ${entity.prefixClassNameWithoutPackage}.log.debug("updating $attribute.name from {} to {}", ${attribute.name}(), value);
    }
    takeStoredValueForKey(value, ${attribute.uppercaseUnderscoreName}_KEY);
  }

  public Object validate${attribute.capitalizedName}(Object value) throws NSValidation.ValidationException {
    ${entity.prefixClassNameWithoutPackage}.log.debug("validate $attribute.name");
    return ERXValidationException.validateForUserInfo(this, ${attribute.uppercaseUnderscoreName}_KEY, value);
  }
#end

#end
#end
  //********************************************************************
  //  ToOne relationship : ToOne リレーションシップ
  //********************************************************************

#foreach ($relationship in $entity.sortedClassToOneRelationships)
#if (!$relationship.inherited) 
  public $relationship.actualDestination.classNameWithDefault ${relationship.name}() {
    return ($relationship.actualDestination.classNameWithDefault)storedValueForKey(${relationship.uppercaseUnderscoreName}_KEY);
  }

  private void set${relationship.capitalizedName}($relationship.actualDestination.classNameWithDefault value) {
    takeStoredValueForKey(value, ${relationship.uppercaseUnderscoreName}_KEY);
  }
  
  public void set${relationship.capitalizedName}Relationship($relationship.actualDestination.classNameWithDefault value) {
    if (${entity.prefixClassNameWithoutPackage}.log.isDebugEnabled()) {
      ${entity.prefixClassNameWithoutPackage}.log.debug("updating $relationship.name from {} to {}", ${relationship.name}(), value);
    }
    if (ERXGenericRecord.InverseRelationshipUpdater.updateInverseRelationships()) {
      set${relationship.capitalizedName}(value);
    } else if (value == null) {
      $relationship.actualDestination.classNameWithDefault oldValue = ${relationship.name}();
      if (oldValue != null) {
        removeObjectFromBothSidesOfRelationshipWithKey(oldValue, ${relationship.uppercaseUnderscoreName}_KEY);
      }
    } else {
      addObjectToBothSidesOfRelationshipWithKey(value, ${relationship.uppercaseUnderscoreName}_KEY);
    }
  }
  
#end
#end
  //********************************************************************
  //  Editing Context replacement : 編集コンテキスト入れ替え
  //********************************************************************

#if (!$entity.partialEntitySet)
  @Override
  public $entity.classNameWithOptionalPackage localInstanceIn(EOEditingContext editingContext) {
    $entity.classNameWithOptionalPackage localInstance = ($entity.classNameWithOptionalPackage)EOUtilities.localInstanceOfObject(editingContext, this);
      if (localInstance == null) {
        throw new IllegalStateException("You attempted to localInstance " + this + ", which has not yet committed.");
      }
      return localInstance;
  }
#end

  //********************************************************************
  //  toMany relationship : toMany リレーションシップ
  //********************************************************************

#foreach ($relationship in $entity.sortedClassToManyRelationships)
#if (!$relationship.inherited) 
  public NSArray<${relationship.actualDestination.classNameWithDefault}> ${relationship.name}() {
    NSArray<${relationship.actualDestination.classNameWithDefault}> results = (NSArray<${relationship.actualDestination.classNameWithDefault}>)storedValueForKey("${relationship.name}");
    if(useCoreQualifierForToManyRelationships()) { // CoreQualifier
      if(!TBWConcurrencyUtilities.isStopRestrictDeleteQualifier()) {
        NSMutableArray<${relationship.actualDestination.classNameWithDefault}> marr = new NSMutableArray<${relationship.actualDestination.classNameWithDefault}>(results.count());
        for (${relationship.actualDestination.classNameWithDefault} one : results) {

          if (one instanceof ITBEnterpriseVirtualDeleteSupport) {
            if(one.deleted() == null) {
              marr.addObject(one);
            }
          } else {
            marr.addObject(one);
          }

        }
        results = marr.immutableClone();
      }     
    }
    return results;
  }

#if (!$relationship.inverseRelationship || $relationship.flattened || !$relationship.inverseRelationship.classProperty)
  public NSArray<${relationship.actualDestination.classNameWithDefault}> ${relationship.name}(EOQualifier qualifier) {
    return ${relationship.name}(qualifier, null);
  }
#else
  public NSArray<${relationship.actualDestination.classNameWithDefault}> ${relationship.name}(EOQualifier qualifier) {
    return ${relationship.name}(qualifier, null, false);
  }

  public NSArray<${relationship.actualDestination.classNameWithDefault}> ${relationship.name}(EOQualifier qualifier, boolean fetch) {
    return ${relationship.name}(qualifier, null, fetch);
  }
#end

  public NSArray<${relationship.actualDestination.classNameWithDefault}> ${relationship.name}(EOQualifier qualifier, NSArray<EOSortOrdering> sortOrderings#if ($relationship.inverseRelationship && !$relationship.flattened && $relationship.inverseRelationship.classProperty), boolean fetch#end) {
    NSArray<${relationship.actualDestination.classNameWithDefault}> results;
#if ($relationship.inverseRelationship && !$relationship.flattened && $relationship.inverseRelationship.classProperty)
    if (fetch) {
      EOQualifier fullQualifier;
#if (${relationship.actualDestination.genericRecord})
  EOQualifier inverseQualifier = new EOKeyValueQualifier("${relationship.inverseRelationship.name}", EOQualifier.QualifierOperatorEqual, this);
#elseif ($relationship.destination.entity.partialEntitySet)
EOQualifier inverseQualifier = new EOKeyValueQualifier(${relationship.destination.classNameWithDefault}.${relationship.inverseRelationship.uppercaseUnderscoreName}_KEY, EOQualifier.QualifierOperatorEqual, this);
#else
          EOQualifier inverseQualifier = new EOKeyValueQualifier(${relationship.actualDestination.classNameWithDefault}.${relationship.inverseRelationship.uppercaseUnderscoreName}_KEY, EOQualifier.QualifierOperatorEqual, this);
#end
      
          if (qualifier == null) {
            fullQualifier = inverseQualifier;
          } else {
            NSMutableArray qualifiers = new NSMutableArray();
            qualifiers.addObject(qualifier);
            qualifiers.addObject(inverseQualifier);
            fullQualifier = new EOAndQualifier(qualifiers);
          }

#if (${relationship.actualDestination.genericRecord})
          EOEntity relationshipEntity = ERXEOAccessUtilities.entityNamed(editingContext(), ${relationship.actualDestination.name}.ENTITY_NAME);
          fetchSpec.setIsDeep(true);
          results = (NSArray<${relationship.actualDestination.classNameWithDefault}>)editingContext().objectsWithFetchSpecification(fetchSpec);
#else
          results = ${relationship.actualDestination.classNameWithDefault}.fetch${relationship.actualDestination.pluralName}(editingContext(), fullQualifier, sortOrderings);
#end
          if(useCoreQualifierForToManyRelationships()) { // CoreQualifier
            EOEntity relationshipEntity = ERXEOAccessUtilities.entityNamed(editingContext(), ${entity.name}.ENTITY_NAME);
            EOQualifier coreQualifier = TBWCoreQualifierBase.delegate().qualifier(relationshipEntity);
            if(coreQualifier != null) {
              results = (NSArray<${relationship.actualDestination.classNameWithDefault}>)EOQualifier.filteredArrayWithQualifier(results, coreQualifier);
            }
          }
    } else {
#end
      results = ${relationship.name}();
      if (qualifier != null) {
        results = (NSArray<${relationship.actualDestination.classNameWithDefault}>)EOQualifier.filteredArrayWithQualifier(results, qualifier);
      }
      if (sortOrderings != null) {
        results = (NSArray<${relationship.actualDestination.classNameWithDefault}>)EOSortOrdering.sortedArrayUsingKeyOrderArray(results, sortOrderings);
      }
#if ($relationship.inverseRelationship && !$relationship.flattened && $relationship.inverseRelationship.classProperty)
    }
#end
      return results;
  }
  
  public void addTo${relationship.capitalizedName}($relationship.actualDestination.classNameWithDefault object) {
    includeObjectIntoPropertyWithKey(object, "${relationship.name}");
  }

  public void removeFrom${relationship.capitalizedName}($relationship.actualDestination.classNameWithDefault object) {
    excludeObjectFromPropertyWithKey(object, "${relationship.name}");
  }

  public void addTo${relationship.capitalizedName}Relationship($relationship.actualDestination.classNameWithDefault object) {
    ${entity.prefixClassNameWithoutPackage}.log.debug("adding {} to ${relationship.name} relationship", object);
    if (ERXGenericRecord.InverseRelationshipUpdater.updateInverseRelationships()) {
      addTo${relationship.capitalizedName}(object);
    } else {
      addObjectToBothSidesOfRelationshipWithKey(object, "${relationship.name}");
    }
  }

  public void removeFrom${relationship.capitalizedName}Relationship($relationship.actualDestination.classNameWithDefault object) {
    ${entity.prefixClassNameWithoutPackage}.log.debug("removing {} from ${relationship.name} relationship", object);
    if (ERXGenericRecord.InverseRelationshipUpdater.updateInverseRelationships()) {
      removeFrom${relationship.capitalizedName}(object);
    } else {
      removeObjectFromBothSidesOfRelationshipWithKey(object, "${relationship.name}");
    }
  }

  public $relationship.actualDestination.classNameWithDefault create${relationship.capitalizedName}Relationship() {
    EOClassDescription eoClassDesc = EOClassDescription.classDescriptionForEntityName("${relationship.actualDestination.name}");
    EOEnterpriseObject eo = eoClassDesc.createInstanceWithEditingContext(editingContext(), null);
    editingContext().insertObject(eo);
    addObjectToBothSidesOfRelationshipWithKey(eo, "${relationship.name}");
    return ($relationship.actualDestination.classNameWithDefault) eo;
  }

  public void delete${relationship.capitalizedName}Relationship($relationship.actualDestination.classNameWithDefault object) {
    removeObjectFromBothSidesOfRelationshipWithKey(object, "${relationship.name}");
#if (!$relationship.ownsDestination)
      editingContext().deleteObject(object);
#end
  }

  public void deleteAll${relationship.capitalizedName}Relationships() {
    Enumeration objects = ${relationship.name}().immutableClone().objectEnumerator();
    while (objects.hasMoreElements()) {
      delete${relationship.capitalizedName}Relationship(($relationship.actualDestination.classNameWithDefault)objects.nextElement());
    }
  }

#end
#end
  //********************************************************************
  //  Instance : インスタンス化
  //********************************************************************

  public #if (!$entity.partialEntitySet)static #end${entity.classNameWithOptionalPackage}#if (!$entity.partialEntitySet) create#else init#end${entity.name}(EOEditingContext editingContext
#foreach ($attribute in $entity.sortedClassAttributes)
#if (!$attribute.allowsNull)
#set ($restrictingQualifierKey = 'false')
#foreach ($qualifierKey in $entity.restrictingQualifierKeys)#if ($attribute.name == $qualifierKey)#set ($restrictingQualifierKey = 'true')#end#end
#if ($restrictingQualifierKey == 'false')
#if ($attribute.userInfo.ERXConstantClassName)    , ${attribute.userInfo.ERXConstantClassName}#else   , ${attribute.javaClassName}#end ${attribute.name}
#end
#end
#end
#foreach ($relationship in $entity.sortedClassToOneRelationships)
#if ($relationship.mandatory && !($relationship.ownsDestination && $relationship.propagatesPrimaryKey))   , ${relationship.actualDestination.classNameWithDefault} ${relationship.name}
#end
#end
    )
  {
    ${entity.classNameWithOptionalPackage} eo = (${entity.classNameWithOptionalPackage})#if ($entity.partialEntitySet)this;#else EOUtilities.createAndInsertInstance(editingContext, ${entity.prefixClassNameWithoutPackage}.ENTITY_NAME);#end

#foreach ($attribute in $entity.sortedClassAttributes)
#if (!$attribute.allowsNull)
#set ($restrictingQualifierKey = 'false')
#foreach ($qualifierKey in $entity.restrictingQualifierKeys) 
#if ($attribute.name == $qualifierKey)
#set ($restrictingQualifierKey = 'true')
#end
#end
#if ($restrictingQualifierKey == 'false')
    eo.set${attribute.capitalizedName}(${attribute.name});
#end
#end
#end
#foreach ($relationship in $entity.sortedClassToOneRelationships)
#if ($relationship.mandatory && !($relationship.ownsDestination && $relationship.propagatesPrimaryKey))
    eo.set${relationship.capitalizedName}Relationship(${relationship.name});
#end
#end
    return eo;
  }
  
  /* EO creation with Stamped EO Support */
  public #if (!$entity.partialEntitySet)static #end${entity.classNameWithOptionalPackage}#if (!$entity.partialEntitySet) create#else init#end${entity.name}WithStampedSupport(EOEditingContext editingContext
#foreach ($attribute in $entity.sortedClassAttributes)
#if (!$attribute.allowsNull)
#set ($restrictingQualifierKey = 'false')
#foreach ($qualifierKey in $entity.restrictingQualifierKeys)#if ($attribute.name == $qualifierKey)#set ($restrictingQualifierKey = 'true')#end#end
#if ($restrictingQualifierKey == 'false' && $attribute.name != 'created' && $attribute.name != 'lastModified')
#if ($attribute.userInfo.ERXConstantClassName)    , ${attribute.userInfo.ERXConstantClassName}#else   , ${attribute.javaClassName}#end ${attribute.name}
#end
#end
#end
#foreach ($relationship in $entity.sortedClassToOneRelationships)
#if ($relationship.mandatory && !($relationship.ownsDestination && $relationship.propagatesPrimaryKey) && $relationship.name != 'createdBy' && $relationship.name != 'lastModifiedBy')   , ${relationship.actualDestination.classNameWithDefault} ${relationship.name}
#end
#end
    )
  {
    ${entity.classNameWithOptionalPackage} eo = (${entity.classNameWithOptionalPackage})#if ($entity.partialEntitySet)this;#else EOUtilities.createAndInsertInstance(editingContext, ${entity.prefixClassNameWithoutPackage}.ENTITY_NAME);#end

#foreach ($attribute in $entity.sortedClassAttributes)
#if (!$attribute.allowsNull)
#set ($restrictingQualifierKey = 'false')
#foreach ($qualifierKey in $entity.restrictingQualifierKeys) 
#if ($attribute.name == $qualifierKey)
#set ($restrictingQualifierKey = 'true')
#end
#end
#if ($restrictingQualifierKey == 'false' && $attribute.name != 'created' && $attribute.name != 'lastModified')
    eo.set${attribute.capitalizedName}(${attribute.name});
#end
#end
#end
#foreach ($relationship in $entity.sortedClassToOneRelationships)
#if ($relationship.mandatory && !($relationship.ownsDestination && $relationship.propagatesPrimaryKey) && $relationship.name != 'createdBy' && $relationship.name != 'lastModifiedBy')
    eo.set${relationship.capitalizedName}Relationship(${relationship.name});
#end
#end
    return eo;
  }

#if (!$entity.partialEntitySet) 
  public static ${entity.classNameWithOptionalPackage} createAndInsertInstance(EOEditingContext context) {
    log.debug("{} : createAndInsertInstance", ENTITY_NAME);
    
    return (${entity.classNameWithOptionalPackage})EOUtilities.createAndInsertInstance(context, ENTITY_NAME);
  }
#end

  // ========== [検索関連] ==========
#if (!$entity.partialEntitySet)
  //********************************************************************
  //  Fetch (NSArray) : フェッチ (NSArray)
  //********************************************************************

#if ($entity.parentSet)
  public static ERXFetchSpecification<${entity.classNameWithOptionalPackage}> fetchSpecFor${entity.name}() {
    return new ERXFetchSpecification<${entity.classNameWithOptionalPackage}>(${entity.prefixClassNameWithoutPackage}.ENTITY_NAME, null, null, false, true, null);
  }
#else
  public static ERXFetchSpecification<${entity.classNameWithOptionalPackage}> fetchSpec() {
    return new ERXFetchSpecification<${entity.classNameWithOptionalPackage}>(${entity.prefixClassNameWithoutPackage}.ENTITY_NAME, null, null, false, true, null);
  }
#end

  /* 
   * standard fetch 
   */
  public static NSArray<${entity.classNameWithOptionalPackage}> fetchAll${entity.pluralName}(EOEditingContext editingContext) {
    return ${entity.prefixClassNameWithoutPackage}.fetchAll${entity.pluralName}(editingContext, null);
  }

  public static NSArray<${entity.classNameWithOptionalPackage}> fetchAll${entity.pluralName}(EOEditingContext editingContext, NSArray<EOSortOrdering> sortOrderings) {
    return ${entity.prefixClassNameWithoutPackage}.fetch${entity.pluralName}(editingContext, null, sortOrderings);
  }
 
  public static NSArray<${entity.classNameWithOptionalPackage}> fetch${entity.pluralName}(EOEditingContext editingContext, EOQualifier qualifier, NSArray<EOSortOrdering> sortOrderings) {
    EOFetchSpecification fetchSpec = new EOFetchSpecification(${entity.prefixClassNameWithoutPackage}.ENTITY_NAME, qualifier, sortOrderings);
    fetchSpec.setIsDeep(true);
    NSArray<${entity.classNameWithOptionalPackage}> eoObjects = (NSArray<${entity.classNameWithOptionalPackage}>)editingContext.objectsWithFetchSpecification(fetchSpec);
    return eoObjects;
  }

  /* 
   * coreQualifier fetch 
   */  
  public static NSArray<${entity.classNameWithOptionalPackage}> fetchAll${entity.pluralName}WithCoreQualifier(EOEditingContext editingContext, ITBWDomain domain) {
    return ${entity.prefixClassNameWithoutPackage}.fetchAll${entity.pluralName}WithCoreQualifier(editingContext, domain, null);
  }
  
  public static NSArray<${entity.classNameWithOptionalPackage}> fetchAll${entity.pluralName}WithCoreQualifier(EOEditingContext editingContext, ITBWDomain domain, NSArray<EOSortOrdering> sortOrderings) {
    EOEntity entity = ERXEOAccessUtilities.entityNamed(editingContext, ${entity.prefixClassNameWithoutPackage}.ENTITY_NAME);
    return ${entity.prefixClassNameWithoutPackage}.fetch${entity.pluralName}(editingContext, TBWCoreQualifierBase.delegate().qualifier(entity, domain), sortOrderings);
  }
  
  public static NSArray<${entity.classNameWithOptionalPackage}> fetch${entity.pluralName}WithCoreQualifier(EOEditingContext editingContext, EOQualifier qualifier, NSArray<EOSortOrdering> sortOrderings) {
    EOEntity entity = ERXEOAccessUtilities.entityNamed(editingContext, ${entity.prefixClassNameWithoutPackage}.ENTITY_NAME);

    TBWMultiDomainSupport multiDomainSupport = (TBWMultiDomainSupport) TBSession.session().multiDomain();
    ITBWDomain domain = multiDomainSupport.currentDomain();

    ERXAndQualifier andQualifier = new ERXAndQualifier(TBWCoreQualifierBase.delegate().qualifier(entity, domain), qualifier);

    EOFetchSpecification fetchSpec = new EOFetchSpecification(${entity.prefixClassNameWithoutPackage}.ENTITY_NAME, andQualifier, sortOrderings);
    fetchSpec.setIsDeep(true);
    NSArray<${entity.classNameWithOptionalPackage}> eoObjects = (NSArray<${entity.classNameWithOptionalPackage}>)editingContext.objectsWithFetchSpecification(fetchSpec);
    return eoObjects;
  }  
  
  //********************************************************************
  //  Fetch ($entity.name) :  フェッチ ($entity.name)
  //********************************************************************
  
  public static ${entity.classNameWithOptionalPackage} fetch${entity.name}(EOEditingContext editingContext, String keyName, Object value) {
    return ${entity.prefixClassNameWithoutPackage}.fetch${entity.name}(editingContext, new EOKeyValueQualifier(keyName, EOQualifier.QualifierOperatorEqual, value));
  }

  public static ${entity.classNameWithOptionalPackage} fetch${entity.name}(EOEditingContext editingContext, EOQualifier qualifier) {
    NSArray<${entity.classNameWithOptionalPackage}> eoObjects = ${entity.prefixClassNameWithoutPackage}.fetch${entity.pluralName}(editingContext, qualifier, null);
    ${entity.classNameWithOptionalPackage} eoObject;
    int count = eoObjects.count();
    if (count == 0) {
      eoObject = null;
    } else if (count == 1) {
      eoObject = (${entity.classNameWithOptionalPackage})eoObjects.objectAtIndex(0);
    } else {
      throw new IllegalStateException("There was more than one ${entity.name} that matched the qualifier '" + qualifier + "'.");
    }
    return eoObject;
  }

  public static ${entity.classNameWithOptionalPackage} fetchRequired${entity.name}(EOEditingContext editingContext, String keyName, Object value) {
    return ${entity.prefixClassNameWithoutPackage}.fetchRequired${entity.name}(editingContext, new EOKeyValueQualifier(keyName, EOQualifier.QualifierOperatorEqual, value));
  }

  public static ${entity.classNameWithOptionalPackage} fetchRequired${entity.name}(EOEditingContext editingContext, EOQualifier qualifier) {
    ${entity.classNameWithOptionalPackage} eoObject = ${entity.prefixClassNameWithoutPackage}.fetch${entity.name}(editingContext, qualifier);
    if (eoObject == null) {
      throw new NoSuchElementException("There was no ${entity.name} that matched the qualifier '" + qualifier + "'.");
    }
    return eoObject;
  }

  public static ${entity.name} fetch${entity.name}ByPrimaryKey(EOEditingContext context, Object value) {
    EOEnterpriseObject eo = TBEOExternalPrimaryKeyHelper.objectWithPrimaryKeyValue(context, ENTITY_NAME, value);
    return (${entity.name}) eo;
  }

  public static ${entity.name} fetch${entity.name}ByEncryptedPrimaryKey(EOEditingContext context, String value) {
    return ${entity.name}.fetch${entity.name}ByPrimaryKey(context, TBFCrypto.crypterForAlgorithm(TBFCrypto.BLOWFISH).decrypt(value));
  }
  
  public static ${entity.classNameWithOptionalPackage} localInstanceIn(EOEditingContext editingContext, ${entity.classNameWithOptionalPackage} eo) {
    ${entity.classNameWithOptionalPackage} localInstance = (eo == null) ? null : (${entity.classNameWithOptionalPackage})EOUtilities.localInstanceOfObject(editingContext, eo);
    if (localInstance == null && eo != null) {
      throw new IllegalStateException("You attempted to localInstance " + eo + ", which has not yet committed.");
    }
    return localInstance;
  }
#end

  //********************************************************************
  //  Fetch specification : フェッチ・スペシフィケーション
  //********************************************************************

#foreach ($fetchSpecification in $entity.sortedFetchSpecs)
#if (true || $fetchSpecification.distinctBindings.size() > 0)
  public static NSArray#if ($fetchSpecification.fetchEnterpriseObjects)<${entity.className}>#else<NSDictionary>#end fetch${fetchSpecification.capitalizedName}(EOEditingContext editingContext, NSDictionary<String, Object> bindings) {
    EOFetchSpecification fetchSpec = EOFetchSpecification.fetchSpecificationNamed("${fetchSpecification.name}", "${entity.name}");
    fetchSpec = fetchSpec.fetchSpecificationWithQualifierBindings(bindings);
    return (NSArray#if ($fetchSpecification.fetchEnterpriseObjects)<${entity.className}>#else<NSDictionary>#end)editingContext.objectsWithFetchSpecification(fetchSpec);
  }
  
#end
  public static NSArray#if ($fetchSpecification.fetchEnterpriseObjects)<${entity.className}>#else<NSDictionary>#end fetch${fetchSpecification.capitalizedName}(EOEditingContext editingContext#foreach ($binding in $fetchSpecification.distinctBindings),
      ${binding.attributePath.childClassName} ${binding.name}Binding#end) {
    EOFetchSpecification fetchSpec = EOFetchSpecification.fetchSpecificationNamed("${fetchSpecification.name}", "${entity.name}");
#if ($fetchSpecification.distinctBindings.size() > 0)
      NSMutableDictionary<String, Object> bindings = new NSMutableDictionary<String, Object>();
#foreach ($binding in $fetchSpecification.distinctBindings)
      bindings.takeValueForKey(${binding.name}Binding, "${binding.name}");
#end
    fetchSpec = fetchSpec.fetchSpecificationWithQualifierBindings(bindings);
#end
      return (NSArray#if ($fetchSpecification.fetchEnterpriseObjects)<${entity.className}>#else<NSDictionary>#end)editingContext.objectsWithFetchSpecification(fetchSpec);
  }
  
#end
}
