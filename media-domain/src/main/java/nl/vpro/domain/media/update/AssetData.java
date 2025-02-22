/*
 * Copyright (C) 2013 All rights reserved
 * VPRO The Netherlands
 */
package nl.vpro.domain.media.update;

import java.io.IOException;
import java.io.InputStream;

import javax.activation.DataHandler;
import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.*;

import nl.vpro.domain.Xmlns;

/**
 * @author Roelof Jan Koekoek
 * @since 2.1
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "assetDataType", propOrder = {
    "data"
})
public class AssetData implements AssetSource {

    @XmlElement
    @XmlMimeType("application/octet-stream")
    @NotNull(message = "provide asset data")
    private DataHandler data;

    public DataHandler getData() {
        return data;
    }

    public void setData(DataHandler data) {
        this.data = data;
    }

    @Override
    public InputStream getInputStream() {
        try {
            return data.getInputStream();
        } catch(IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append("AssetData");
        sb.append("{}");
        return sb.toString();
    }
}
