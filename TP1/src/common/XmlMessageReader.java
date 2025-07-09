package common;

import org.w3c.dom.*;
import org.xml.sax.InputSource;

import javax.xml.parsers.*;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;
import java.io.*;

public class XmlMessageReader {

    public static Document parseXml(String xml) throws Exception {
        DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        InputSource is = new InputSource(new StringReader(xml));
        return builder.parse(is);
    }

    // Obtém o elemento filho principal da mensagem (payload)
    public static Element getPayloadElement(Document doc) {
        NodeList children = doc.getDocumentElement().getChildNodes();
        for (int i = 0; i < children.getLength(); i++) {
            Node node = children.item(i);
            if (node.getNodeType() == Node.ELEMENT_NODE) {
                return (Element) node;
            }
        }
        return null;
    }

    // Devolve o nome do payload (ex: loginRequest, gameStart, etc)
    public static String getPayloadType(Document doc) {
        Element payload = getPayloadElement(doc);
        return payload != null ? payload.getNodeName() : null;
    }

    // Obtém o valor de texto de uma tag dentro do payload (sem prefixos)
    public static String getTextValue(Element element, String tagName) {
        NodeList list = element.getChildNodes();
        for (int i = 0; i < list.getLength(); i++) {
            Node node = list.item(i);
            if (node.getNodeType() == Node.ELEMENT_NODE) {
                Element el = (Element) node;
                if (tagName.equals(el.getNodeName())) {
                    return el.getTextContent();
                }
            }
        }
        return null;
    }

    // Métodos auxiliares para obter valores a partir do payload
    public static int getIntValue(Element element, String tagName) {
        String value = getTextValue(element, tagName);
        return value != null ? Integer.parseInt(value) : -1;
    }

    public static boolean getBooleanValue(Element element, String tagName) {
        String value = getTextValue(element, tagName);
        return Boolean.parseBoolean(value);
    }

    // Valida um XML recebido contra um XSD
    public static boolean validateXml(String xml, String xsdPath) {
        try {
            SchemaFactory factory = SchemaFactory.newInstance("http://www.w3.org/2001/XMLSchema");
            Schema schema = factory.newSchema(new File(xsdPath));
            Validator validator = schema.newValidator();
            validator.validate(new StreamSource(new StringReader(xml)));
            return true;
        } catch (Exception e) {
            System.err.println("Erro de validação XML: " + e.getMessage());
            return false;
        }
    }
}
