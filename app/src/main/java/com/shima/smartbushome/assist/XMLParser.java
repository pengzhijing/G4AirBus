package com.shima.smartbushome.assist;

import java.io.IOException;
import java.io.StringReader;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import android.util.Log;

public class XMLParser {

	
	/**
	 * Getting XML DOM element
	 * 
	 * @param XML
	 *            string
	 * */
	public Document getDomElement(String xml) {
		Document doc = null;
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		try {

			DocumentBuilder db = dbf.newDocumentBuilder();

			InputSource is = new InputSource();
			is.setCharacterStream(new StringReader(xml));
			doc = db.parse(is);

		} catch (ParserConfigurationException e) {
			Log.e("Error: ", e.getMessage());
			return null;
		} catch (SAXException e) {
			Log.e("Error: ", e.getMessage());
			return null;
		} catch (IOException e) {
			Log.e("Error: ", e.getMessage());
			return null;
		}

		return doc;
	}

	/**
	 * Getting node value
	 * 
	 * @param elem
	 *            element
	 */
	public final String getElementValue(Node elem) {

		StringBuilder data = new StringBuilder();
		if (elem.hasChildNodes()) {
			for (Node child = elem.getFirstChild(); child != null; child = child
					.getNextSibling()) {
				// if (child.getNodeType() == Node.TEXT_NODE) {
				// Log.d("test", child.getNodeValue());
				data.append(child.getNodeValue());
				// }
			}
		}

		String stringData = data.toString();
		//
		if (stringData == null || stringData.length() == 0) {
			// stringData =
			// elem.getElementsByTagName(KEY_Article_Details).item(0)
			// .getFirstChild().getNodeValue();
			return "";
		}

		return stringData;

		// Node child;
		// if (elem != null) {
		// if (elem.hasChildNodes()) {
		// for (child = elem.getFirstChild(); child != null; child = child
		// .getNextSibling()) {
		// if (child.getNodeType() == Node.TEXT_NODE) {
		// return child.getNodeValue();
		// }
		// }
		// }
		// }
		// return "";
	}

	/**
	 * Getting node value
	 * 
	 * @param Element
	 *            node
	 * @param key
	 *            string
	 * */
	public String getValue(Element item, String str) {
		NodeList n = item.getElementsByTagName(str);
		if (n.getLength() <= 0) {
			return "";
		}
		return this.getElementValue(n.item(0));
	}

}
