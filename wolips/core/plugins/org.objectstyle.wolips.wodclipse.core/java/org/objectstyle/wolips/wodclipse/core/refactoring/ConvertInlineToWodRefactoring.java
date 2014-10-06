package org.objectstyle.wolips.wodclipse.core.refactoring;

import java.io.IOException;
import java.io.StringWriter;
import java.lang.reflect.InvocationTargetException;
import java.util.LinkedList;
import java.util.List;

import jp.aonir.fuzzyxml.FuzzyXMLDocument;
import jp.aonir.fuzzyxml.FuzzyXMLElement;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.text.edits.InsertEdit;
import org.eclipse.text.edits.MalformedTreeException;
import org.eclipse.text.edits.ReplaceEdit;
import org.eclipse.text.edits.TextEdit;
import org.objectstyle.wolips.bindings.wod.IWodElement;
import org.objectstyle.wolips.bindings.wod.IWodModel;
import org.objectstyle.wolips.bindings.wod.SimpleWodElement;
import org.objectstyle.wolips.variables.BuildProperties;
import org.objectstyle.wolips.wodclipse.core.completion.WodParserCache;
import org.objectstyle.wolips.wodclipse.core.util.FuzzyXMLWodElement;
import org.objectstyle.wolips.wodclipse.core.util.WodDocumentUtils;
import org.objectstyle.wolips.wodclipse.core.util.WodHtmlUtils;

public class ConvertInlineToWodRefactoring implements IRunnableWithProgress {
  public static void run(WodParserCache cache, int offset, BuildProperties buildProperties, IProgressMonitor progressMonitor) throws InvocationTargetException, InterruptedException, CoreException {
    TemplateRefactoring.processHtmlAndWod(new ConvertInlineToWodRefactoring(cache, offset, buildProperties), cache, progressMonitor);
  }

  private WodParserCache _cache;
  private int _offset;
  private BuildProperties _buildProperties;

  public ConvertInlineToWodRefactoring(WodParserCache cache, int offset, BuildProperties buildProperties) {
    _cache = cache;
    _offset = offset;
    _buildProperties = buildProperties;
  }

  public void run(IProgressMonitor monitor) throws InvocationTargetException {
    try {
      FuzzyXMLDocument htmlModel = _cache.getHtmlEntry().getModel();
      FuzzyXMLElement element = htmlModel.getElementByOffset(_offset);
      if (element != null) {
        IWodModel wodModel = _cache.getWodEntry().getModel();
        String tagName = element.getName();
        if (WodHtmlUtils.isInline(tagName)) {
          SimpleWodElement wodElement = new FuzzyXMLWodElement(element, _buildProperties);
          ElementRename elementRename = ElementRename.newUniqueName(wodModel, wodElement, true);
          wodElement.setElementName(elementRename.getNewName());

          List<ElementRename> elementRenames = new LinkedList<ElementRename>();
          elementRenames.add(elementRename);

          ConvertInlineToWodRefactoring.appendWodElement(_cache, wodElement);

          IDocument htmlDocument = _cache.getHtmlEntry().getDocument();
          List<TextEdit> edits = new LinkedList<TextEdit>();
          int openTagLength = element.getOpenTagLength();
          if (element.hasCloseTag()) {
            edits.add(new ReplaceEdit(element.getCloseTagOffset() + element.getCloseNameOffset() + 1, element.getCloseNameLength(), "webobject"));
          }
          else {
            openTagLength--;
          }
          edits.add(new ReplaceEdit(element.getOffset() + 1, openTagLength, "webobject name = \"" + elementRename.getNewName() + "\""));
          WodDocumentUtils.applyEdits(htmlDocument, edits);
        }
      }
    }
    catch (Exception e) {
      throw new InvocationTargetException(e);
    }
  }

  public static void appendWodElement(WodParserCache cache, IWodElement wodElement) throws IOException, MalformedTreeException, BadLocationException {
    ConvertInlineToWodRefactoring.insertWodElement(cache, wodElement, -1);
  }

  public static void insertWodElement(WodParserCache cache, IWodElement wodElement, int offset) throws IOException, MalformedTreeException, BadLocationException {
    IDocument wodDocument = cache.getWodEntry().getDocument();
    if (wodDocument != null) {
      int insertOffset = offset;
      if (insertOffset == -1) {
        insertOffset = wodDocument.getLength();
      }

      List<TextEdit> wodEdits = new LinkedList<TextEdit>();
      StringWriter wodWriter = new StringWriter();
      wodElement.writeWodFormat(wodWriter, false);
      String wodString = wodWriter.toString();
      if (insertOffset > 0) {
        wodString = "\n" + wodString;
      }
      wodEdits.add(new InsertEdit(insertOffset, wodString));
      WodDocumentUtils.applyEdits(wodDocument, wodEdits);
    }
  }
}
