@javax.xml.bind.annotation.XmlSchema(namespace = Namespaces.DC_TERMS,
    elementFormDefault = XmlNsForm.QUALIFIED,
    attributeFormDefault = XmlNsForm.QUALIFIED,
    xmlns = {
        @XmlNs(prefix = "rdf", namespaceURI = Namespaces.RDF),
        @XmlNs(prefix = "", namespaceURI = Namespaces.OAI),
        @XmlNs(prefix = "skos", namespaceURI = Namespaces.SKOS),
        @XmlNs(prefix = "skosxl", namespaceURI = Namespaces.SKOS_XL),
        @XmlNs(prefix = "openskos", namespaceURI = Namespaces.OPEN_SKOS),
        @XmlNs(prefix = "dcterms", namespaceURI = Namespaces.DC_TERMS)

    }
) package nl.vpro.dublincore.terms;

import javax.xml.bind.annotation.XmlNs;
import javax.xml.bind.annotation.XmlNsForm;

import nl.vpro.domain.media.gtaa.Namespaces;


