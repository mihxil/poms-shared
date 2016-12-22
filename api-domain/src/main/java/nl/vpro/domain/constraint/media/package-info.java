@XmlSchema(
        namespace = Xmlns.MEDIA_CONSTRAINT_NAMESPACE,
        xmlns = {
            @XmlNs(prefix = "media", namespaceURI = Xmlns.MEDIA_CONSTRAINT_NAMESPACE),
            @XmlNs(prefix = "constraint", namespaceURI = "urn:vpro:api:constraint")
        },
        elementFormDefault = javax.xml.bind.annotation.XmlNsForm.QUALIFIED,
        attributeFormDefault = javax.xml.bind.annotation.XmlNsForm.UNQUALIFIED
)
package nl.vpro.domain.constraint.media;

import javax.xml.bind.annotation.XmlNs;
import javax.xml.bind.annotation.XmlSchema;

import nl.vpro.domain.Xmlns;
