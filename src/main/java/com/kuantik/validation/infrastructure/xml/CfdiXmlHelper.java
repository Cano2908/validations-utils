package com.kuantik.validation.infrastructure.xml;

import com.kuantik.validation.infrastructure.sat_catalog.dto.CatalogoVigente;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Date;
import java.util.List;
import java.util.Optional;

/**
 * Utilidades estáticas para leer nodos y atributos de un CFDI ya parseado.
 * Centraliza los patrones repetidos: acceso seguro a atributos, lectura de nodos
 * con namespace, parseo de fecha en zona horaria de México y decodificación Base64.
 */
public final class CfdiXmlHelper {

    public static final String NS_CFDI = "http://www.sat.gob.mx/cfd/4";
    public static final String NS_TIMBRE = "http://www.sat.gob.mx/TimbreFiscalDigital";

    private static final ZoneId ZONA_MEXICO = ZoneId.of("America/Mexico_City");

    private CfdiXmlHelper() {
    }

    /**
     * Atributo del elemento raíz; vacío si el atributo no existe o está en blanco.
     */
    public static Optional<String> getRootAttribute(Document xml, String attrName) {
        String value = xml.getDocumentElement().getAttribute(attrName);
        return value.isBlank() ? Optional.empty() : Optional.of(value);
    }

    /**
     * Atributo de un elemento; vacío si no existe o está en blanco.
     */
    public static Optional<String> getAttribute(Element element, String attrName) {
        String value = element.getAttribute(attrName);
        return value.isBlank() ? Optional.empty() : Optional.of(value);
    }

    /**
     * Primer elemento que coincide con el namespace y localName dado.
     * Vacío si no existe ninguno.
     */
    public static Optional<Element> getFirstElement(Document xml, String ns, String localName) {
        NodeList nodes = xml.getElementsByTagNameNS(ns, localName);
        if (nodes.getLength() == 0) return Optional.empty();
        return Optional.of((Element) nodes.item(0));
    }

    /**
     * Primer elemento CFDI (namespace {@code http://www.sat.gob.mx/cfd/4}) con el localName dado.
     * Atajo de {@link #getFirstElement(Document, String, String)} con el namespace del CFDI 4.0.
     */
    public static Optional<Element> getCfdiElement(Document xml, String localName) {
        return getFirstElement(xml, NS_CFDI, localName);
    }

    /**
     * Hijo directo del elemento raíz ({@code cfdi:Comprobante}) con el localName CFDI dado.
     * A diferencia de {@link #getCfdiElement}, no desciende recursivamente — busca solo en
     * los hijos inmediatos. Necesario para distinguir {@code cfdi:Impuestos} a nivel documento
     * de los {@code cfdi:Impuestos} dentro de cada {@code cfdi:Concepto}.
     */
    public static Optional<Element> getDirectCfdiChild(Document xml, String localName) {
        NodeList children = xml.getDocumentElement().getChildNodes();
        for (int i = 0; i < children.getLength(); i++) {
            Node child = children.item(i);
            if (child instanceof Element e
                    && localName.equals(e.getLocalName())
                    && NS_CFDI.equals(e.getNamespaceURI())) {
                return Optional.of(e);
            }
        }
        return Optional.empty();
    }

    /**
     * Todos los nodos {@code cfdi:Concepto} del documento como lista tipada.
     */
    public static List<Element> getConceptos(Document xml) {
        NodeList nodes = xml.getElementsByTagNameNS(NS_CFDI, "Concepto");
        List<Element> result = new ArrayList<>(nodes.getLength());
        for (int i = 0; i < nodes.getLength(); i++) {
            result.add((Element) nodes.item(i));
        }
        return result;
    }

    /**
     * Parsea el atributo {@code Fecha} del CFDI (ISO-8601 local, ej. {@code 2024-01-15T10:30:00})
     * y lo convierte a {@link Date} en la zona horaria {@code America/Mexico_City}.
     * Retorna vacío si el valor es nulo, en blanco o tiene formato inválido.
     */
    public static Optional<Date> parseFechaEmision(String fechaStr) {
        if (fechaStr == null || fechaStr.isBlank()) return Optional.empty();
        try {
            LocalDateTime localFecha = LocalDateTime.parse(fechaStr);
            return Optional.of(Date.from(localFecha.atZone(ZONA_MEXICO).toInstant()));
        } catch (DateTimeParseException dateTimeParseException) {
            return Optional.empty();
        }
    }

    /**
     * Retorna {@code true} si la clave del catálogo SAT está vigente,
     * es decir, si {@code fechaFinVigencia} es nula o está en blanco.
     */
    public static boolean esVigente(String fechaFinVigencia) {
        return fechaFinVigencia == null || fechaFinVigencia.isBlank();
    }

    /**
     * Retorna {@code true} si existe en {@code catalogo} una entrada cuya clave coincide
     * con {@code clave} y cuya vigencia no ha finalizado ({@link #esVigente}).
     */
    public static boolean existeYVigente(List<? extends CatalogoVigente> catalogo, String clave) {
        return catalogo.stream().anyMatch(c -> clave.equals(c.clave()) && esVigente(c.fechaFinVigencia()));
    }

    /**
     * Elimina espacios en blanco y decodifica una cadena Base64.
     */
    public static byte[] decodeBase64(String base64) {
        return Base64.getDecoder().decode(base64.replaceAll("\\s+", ""));
    }
}
