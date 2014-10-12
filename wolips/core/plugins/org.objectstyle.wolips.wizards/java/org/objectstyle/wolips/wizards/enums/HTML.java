package org.objectstyle.wolips.wizards.enums;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * HTML Template Defaults.
 * 
 * @author ldeck
 */
public enum HTML {

	BLANK_CONTENT("Blank HTML Content", 0),
	HTML5("HTML 5", 8),
	HTML_UNSPECIFIED("HTML", 1),
	HTML401_STRICT("HTML 4.0.1 Strict", 2), 
	HTML401_TRANSITIONAL("HTML 4.0.1 Transitional", 3),
	XHTML10_FRAMESET("XHTML 1.0 Frameset", 4), 
	XHTML10_STRICT("XHTML 1.0 Strict", 5),
	XHTML10_TRANSITIONAL("XHTML 1.0 Transitional", 6),
	XHTML11("XHTML 1.1", 7);

	//********************************************************************
	//	Constructor : コンストラクタ
	//********************************************************************

	HTML(String display, int templateIndex) {
		this(display, templateIndex, null);
	}

	// template index is just to make things easier in velocity engine
	HTML(String display, int templateIndex, String html) {
		_displayString = display;
		_html = html;
		_templateIndex = templateIndex;
	}

	//********************************************************************
	//	Methods : メソッド
	//********************************************************************

	public int getTemplateIndex() {
		return _templateIndex;
	}
	private final int _templateIndex;

	public String getDisplayString() {
		return _displayString;
	}
	private final String _displayString;

	public String getHTML(String lineSeparator) {
		if (_html == null) {
			StringBuilder sb = new StringBuilder();

			if (!BLANK_CONTENT.equals(this)) {
				String dateString = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
				String userName = System.getProperty("user.name", "TBLips");
				boolean isXML = true;

				if (HTML_UNSPECIFIED.equals(this)) {
					isXML = false;
					sb.append("<html>");
				}
				else if (HTML5.equals(this)) {
					isXML = false;
					sb.append("<!DOCTYPE HTML>").append(lineSeparator);
					sb.append("<html>").append(lineSeparator);
				}
				else if (HTML401_STRICT.equals(this)) {
					isXML = false;
					sb.append("<!DOCTYPE html PUBLIC \"-//W3C//DTD HTML 4.01//EN\"").append(lineSeparator);
					sb.append("   \"http://www.w3.org/TR/html4/strict.dtd\">").append(lineSeparator);
					sb.append(lineSeparator);
					sb.append("<html lang=\"en\">").append(lineSeparator);
				}
				else if (HTML401_TRANSITIONAL.equals(this)) {
					isXML = false;
					sb.append("<!DOCTYPE html PUBLIC \"-//W3C//DTD HTML 4.01 Transitional//EN\"").append(lineSeparator);
					sb.append("   \"http://www.w3.org/TR/html4/loose.dtd\">").append(lineSeparator);
					sb.append(lineSeparator);
					sb.append("<html lang=\"en\">").append(lineSeparator);
				}
				else if (XHTML10_FRAMESET.equals(this)) {
					sb.append("<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Frameset//EN\"").append(lineSeparator);
					sb.append("	\"http://www.w3.org/TR/xhtml1/DTD/xhtml1-frameset.dtd\">").append(lineSeparator);
					sb.append(lineSeparator);
					sb.append("<html xmlns=\"http://www.w3.org/1999/xhtml\" xml:lang=\"en\" lang=\"en\">").append(lineSeparator);
				}
				else if (XHTML10_STRICT.equals(this)) {
					sb.append("<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Strict//EN\"").append(lineSeparator);
					sb.append("	\"http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd\">").append(lineSeparator);
					sb.append(lineSeparator);
					sb.append("<html xmlns=\"http://www.w3.org/1999/xhtml\" xml:lang=\"en\" lang=\"en\">").append(lineSeparator);
				}
				else if (XHTML10_TRANSITIONAL.equals(this)) {
					sb.append("<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Transitional//EN\"").append(lineSeparator);
					sb.append("	\"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd\">").append(lineSeparator);
					sb.append(lineSeparator);
					sb.append("<html xmlns=\"http://www.w3.org/1999/xhtml\" xml:lang=\"en\" lang=\"en\">").append(lineSeparator);
				}
				else if (XHTML11.equals(this)) {
					sb.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>").append(lineSeparator);
					sb.append("<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.1//EN\"").append(lineSeparator);
					sb.append("	\"http://www.w3.org/TR/xhtml11/DTD/xhtml11.dtd\">").append(lineSeparator);
					sb.append(lineSeparator);
					sb.append("<html xmlns=\"http://www.w3.org/1999/xhtml\" xml:lang=\"en\">").append(lineSeparator);
				}

				String closingTag = isXML ? "/>" : ">";

				sb.append(lineSeparator).append("<head>").append(lineSeparator);
				sb.append(lineSeparator).append("	<meta http-equiv=\"Content-Type\" content=\"text/html; charset=utf-8\"").append(closingTag);
				sb.append(lineSeparator).append("	<title>untitled</title>");
				sb.append(lineSeparator).append("	<meta name=\"generator\" content=\"TBLips http://wiki.objectstyle.org/confluence/display/WOL/Home\"").append(closingTag);
				sb.append(lineSeparator).append("	<meta name=\"author\" content=\"").append(userName).append('"').append(closingTag);
				sb.append(lineSeparator).append("	<!-- Date: ").append(dateString).append(" -->");
				sb.append(lineSeparator).append("</head>");
				sb.append(lineSeparator).append("<body>");
				sb.append(lineSeparator).append(lineSeparator);
				sb.append(lineSeparator).append("</body>");
				sb.append(lineSeparator).append("</html>");
			}

			_html = sb.toString();
		}
		return _html;
	}
	private String _html;

	public static HTML getDefaultHTML() {
		return BLANK_CONTENT;
	}

	public static HTML getValueForKey(String key) {
		for (HTML value : values()) {
			if (value.getDisplayString().equals(key)) {
				return value;
			}
		}
		return getDefaultHTML();
	}

}
