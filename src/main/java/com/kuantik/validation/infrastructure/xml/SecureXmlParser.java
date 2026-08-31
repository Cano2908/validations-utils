package com.kuantik.validation.infrastructure.xml;

import com.kuantik.validation.domain.exception.XmlParseException;
import org.springframework.stereotype.Component;
import org.w3c.dom.Document;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;

@Component
public class SecureXmlParser {

    private final DocumentBuilderFactory factory;

    public SecureXmlParser() {
        try {
            DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
            documentBuilderFactory.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);
            documentBuilderFactory.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);
            documentBuilderFactory.setFeature("http://xml.org/sax/features/external-general-entities", false);
            documentBuilderFactory.setFeature("http://xml.org/sax/features/external-parameter-entities", false);
            documentBuilderFactory.setExpandEntityReferences(false);
            documentBuilderFactory.setNamespaceAware(true);
            this.factory = documentBuilderFactory;
        } catch (ParserConfigurationException e) {
            throw new IllegalStateException("No se pudo configurar DocumentBuilderFactory de forma segura", e);
        }
    }

    public Document parse(String xmlContent) {
        try {
            DocumentBuilder documentBuilder = this.factory.newDocumentBuilder();
            return documentBuilder.parse(new ByteArrayInputStream(xmlContent.getBytes(StandardCharsets.UTF_8)));
        } catch (Exception exception) {
            throw new XmlParseException("XML no parseable: " + exception.getMessage(), exception);
        }
    }
}
