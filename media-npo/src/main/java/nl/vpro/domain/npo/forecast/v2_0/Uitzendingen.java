//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.8-b130911.1802 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2016.12.05 at 11:14:06 AM CET 
//


package nl.vpro.domain.npo.forecast.v2_0;

import java.util.ArrayList;
import java.util.List;
import javax.annotation.Generated;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for anonymous complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType>
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element ref="{}uitzending" maxOccurs="unbounded"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "uitzending"
})
@XmlRootElement(name = "uitzendingen")
@Generated(value = "com.sun.tools.internal.xjc.Driver", date = "2016-12-05T11:14:06+01:00", comments = "JAXB RI v2.2.8-b130911.1802")
public class Uitzendingen {

    @XmlElement(required = true)
    @Generated(value = "com.sun.tools.internal.xjc.Driver", date = "2016-12-05T11:14:06+01:00", comments = "JAXB RI v2.2.8-b130911.1802")
    protected List<Uitzending> uitzending;

    /**
     * Gets the value of the uitzending property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the uitzending property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getUitzending().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link Uitzending }
     * 
     * 
     */
    @Generated(value = "com.sun.tools.internal.xjc.Driver", date = "2016-12-05T11:14:06+01:00", comments = "JAXB RI v2.2.8-b130911.1802")
    public List<Uitzending> getUitzending() {
        if (uitzending == null) {
            uitzending = new ArrayList<Uitzending>();
        }
        return this.uitzending;
    }

}
