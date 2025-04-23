package common;

import org.w3c.dom.*;
import javax.xml.parsers.*;
import java.io.*;

public class XmlMessageReader {

    public static Document parseXml(String xml) throws Exception {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setNamespaceAware(true); // importante para usar namespaces
        DocumentBuilder builder = factory.newDocumentBuilder();
        return builder.parse(new ByteArrayInputStream(xml.getBytes()));
    }

    public static String getRootElement(Document doc) {
        return doc.getDocumentElement().getLocalName(); // ex: "loginRequest"
    }

    public static String getTextValue(Document doc, String tagName) {
        NodeList list = doc.getElementsByTagNameNS("*", tagName);
        if (list.getLength() > 0) {
            return list.item(0).getTextContent();
        }
        return null;
    }

    public static int getIntValue(Document doc, String tagName) {
        String value = getTextValue(doc, tagName);
        return value != null ? Integer.parseInt(value) : -1;
    }

    public static boolean getBooleanValue(Document doc, String tagName) {
        String value = getTextValue(doc, tagName);
        return value != null && Boolean.parseBoolean(value);
    }
}
