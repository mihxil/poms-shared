/*
 * Copyright (C) 2012 All rights reserved
 * VPRO The Netherlands
 */
package nl.vpro.domain.media.update;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.time.Instant;
import java.util.*;
import java.util.stream.Stream;

import javax.activation.DataHandler;
import javax.annotation.Nonnull;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.ConstraintViolation;
import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.*;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import nl.vpro.domain.MutableEmbargo;
import nl.vpro.domain.EmbargoBuilder;
import nl.vpro.domain.Embargos;
import nl.vpro.domain.image.ImageMetadata;
import nl.vpro.domain.image.ImageType;
import nl.vpro.domain.image.Metadata;
import nl.vpro.domain.media.support.Image;
import nl.vpro.domain.media.support.OwnerType;
import nl.vpro.domain.support.License;
import nl.vpro.jackson2.StringInstantToJsonTimestamp;
import nl.vpro.jackson2.XMLDurationToJsonTimestamp;
import nl.vpro.validation.*;
import nl.vpro.xml.bind.DurationXmlAdapter;
import nl.vpro.xml.bind.InstantXmlAdapter;

import static nl.vpro.domain.media.update.MediaUpdate.VALIDATOR;


@XmlRootElement(name = "image")
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "imageUpdateType", propOrder = {
    "title",
    "description",
    "source",
    "sourceName",
    "license",
    "width",
    "height",
    "credits",
    "date",
    "offset",
    "image",
    "crids"
})

@Slf4j
@Data
public class ImageUpdate implements MutableEmbargo<ImageUpdate>, Metadata<ImageUpdate> {

    @XmlAttribute(required = true)
    @NotNull
    private ImageType type;

    /**
     * The URN of the image object in the media object. This is basicly the id prefixed with {@link Image#getUrnPrefix()}
     */
    @XmlAttribute(name = "urn")
    @Pattern(regexp = "^urn:vpro:media:image:[0-9]+$")
    private String urn;




    @XmlAttribute(name = "publishStart")
    @XmlJavaTypeAdapter(InstantXmlAdapter.class)
    @XmlSchemaType(name = "dateTime")
    @JsonDeserialize(using = StringInstantToJsonTimestamp.Deserializer.class)
    @JsonSerialize(using = StringInstantToJsonTimestamp.Serializer.class)
    private Instant publishStartInstant;

    @XmlAttribute(name = "publishStop")
    @XmlJavaTypeAdapter(InstantXmlAdapter.class)
    @XmlSchemaType(name = "dateTime")
    @JsonDeserialize(using = StringInstantToJsonTimestamp.Deserializer.class)
    @JsonSerialize(using = StringInstantToJsonTimestamp.Serializer.class)
    private Instant publishStopInstant;

    @XmlAttribute(required = true)
    @Getter @Setter
    Boolean highlighted = false;

    @XmlElement(required = true)
    @NotNull(message = "provide title for imageUpdate")
    @Size.List({@Size(max = 255), @Size(min = 1)})
    private String title;

    @XmlElement(required = false)
    private String description;

    @XmlElement
    @Min(1)
    private Integer height;

    @XmlElement
    @Min(1)
    private Integer width;


    @NoHtml
    @XmlElement
    @NotNull(groups = {WarningValidatorGroup.class})
    private String credits;

    @URI(mustHaveScheme = true, minHostParts = 2)
    @XmlElement
    @NotNull(groups = {WarningValidatorGroup.class})
    private String source;

    @XmlElement
    @Size.List({
        @Size(max = 255, message = "{nl.vpro.constraints.text.Size.max}")
    })
    @NotNull(groups = {WarningValidatorGroup.class})
    private String sourceName;

    @XmlElement
    @NotNull(groups = {WarningValidatorGroup.class})
    @Valid
    private License license;

    @ReleaseDate()
    @XmlElement
    private String date;

    @Temporal(TemporalType.TIME)
    @XmlJavaTypeAdapter(DurationXmlAdapter.class)
    @XmlElement
    @JsonSerialize(using = XMLDurationToJsonTimestamp.Serializer.class)
    @JsonDeserialize(using = XMLDurationToJsonTimestamp.DeserializerJavaDuration.class)
    @JsonInclude(JsonInclude.Include.NON_NULL)
    protected java.time.Duration offset;




    /**
     * <p>
     * Description of the image. If this describes an existing {@link Image} then the type of this
     * is a {@link String} for {@link Image#getUrn()}.
     * </p>
     * <p>
     * It can also be an {@link ImageData} or an {@link ImageLocation} in which case this object describes a <em>new</em> image.
     * </p>
     */
    @XmlElements(value = {
        @XmlElement(name = "imageData", type = ImageData.class),
        @XmlElement(name = "imageLocation", type = ImageLocation.class),
        @XmlElement(name = "urn", type = String.class)
    })
    @Valid
    private Object image;

    @XmlElement(name = "crid")
    private List<@CRID String> crids;


    public ImageUpdate() {
    }

    public ImageUpdate(ImageType type, String title, String description, ImageData image) {
        this.description = description;
        this.title = title;
        this.type = type;
        this.image = image;
    }

    public ImageUpdate(ImageType type, String title, String description, ImageLocation image) {
        this.description = description;
        this.title = title;
        this.type = type;
        this.image = image;
    }

    /**
     */
    @Override
    public String getImageUri() {
        if (image instanceof String) {
            return (String) image;
        }
        return null;

    }

    public static class Builder implements EmbargoBuilder<Builder> {

        public Builder imageUrl(String imageLocation) {
            return imageLocation(new ImageLocation(imageLocation));
        }

        public Builder imageUrl(String mimeType, String imageLocation) {
            return imageLocation(ImageLocation.builder().mimeType(mimeType).url(imageLocation).build());
        }

        public Builder imageDataHandler(DataHandler dataHandler) {
            return imageData(new ImageData(dataHandler));
        }

    }

    @lombok.Builder(builderClassName = "Builder")
    private ImageUpdate(
        ImageType type,
        String title,
        String description,
        ImageLocation imageLocation,
        ImageData imageData,
        String imageUrn,
        License license,
        String source,
        String sourceName,
        String credits,
        Instant publishStart,
        Instant publishStop,
        List<String> crids
        ) {
        this.description = description;
        this.title = title;
        this.type = type;
        Stream.of(imageLocation, imageData, imageUrn).filter(Objects::nonNull).forEach(o -> {
            if (this.image != null) {
                throw new IllegalStateException("Can specify only on of imageLocation, imageData or imageUrn");
            }
            this.image = o;
            }
        );
        this.license = license;
        this.sourceName = sourceName;
        this.source = source;
        this.credits = credits;
        this.publishStartInstant = publishStart;
        this.publishStopInstant = publishStop;
        this.crids = crids == null ? null: new ArrayList<>(crids);
    }


    public ImageUpdate(Image image) {
        copyFrom(image);
        highlighted = image.isHighlighted();
        String uri = image.getImageUri();
        if (uri != null) {
            if (uri
                .replace('.', ':') // See MSE-865
                .startsWith(nl.vpro.domain.image.Image.BASE_URN)) {
                this.image = uri;
            } else if (uri.startsWith("urn:")) {
                log.warn("Uri starts with a non image urn: {}. Not taking it as an url, because that won't work either", uri);
                this.image = uri;
            } else {
                this.image = new ImageLocation(uri);
            }
        }

        date = image.getDate();
        offset = image.getOffset();
        urn = image.getUrn();
        crids = image.getCrids();
    }

    public Image toImage() {
        return toImage(OwnerType.BROADCASTER);
    }

    public Image toImage(OwnerType owner) {
        Image result = new Image(owner);
        result.setCreationInstant(null); // not supported by update format. will be set by persistence layer
        result.copyFrom(this);
        result.setHighlighted(highlighted);
        result.setDate(date);
        result.setOffset(offset);
        result.setUrn(urn);
        if (image instanceof String) {
            result.setImageUri((String) image);
        } else if (image instanceof ImageLocation) {
            //result.setImageUri(((ImageLocation) image).getUrl());
        }
        result.setCrids(crids);
        Embargos.copy(this, result);
        return result;
    }


    /**
     *
     * @param metadata Incoming metadata from the image server
     */
    public Image toImage(ImageMetadata<?> metadata) {
        Image result = toImage();
        result.setImageUri(metadata.getImageUri());
        result.copyFromIfSourceSet(metadata);
        result.copyFromIfSourceSet(this);
        return result;
    }



    public Long getId() {
        return Image.idFromUrn(getUrn());
    }


    public void setId(Long id) {
        urn = id == null ? null : Image.BASE_URN + id;
    }

    @Nonnull
    @Override
    public ImageUpdate setPublishStartInstant(Instant publishStart) {
        this.publishStartInstant = publishStart;
        return this;
    }

    @Override
    public Instant getPublishStopInstant() {
        return publishStopInstant;
    }

    @Nonnull
    @Override
    public ImageUpdate setPublishStopInstant(Instant publishStop) {
        this.publishStopInstant = publishStop;
        return this;
    }

    /**
     * Sets the image as an {@link ImageData} object. I.e. the actual blob
     */
    public void setImage(ImageData image) {
        this.image = image;
    }


    /**
     * Sets the image as an {@link ImageLocation} object. I.e. a reference to some remote url.
     */
    public void setImage(ImageLocation image) {
        this.image = image;
    }


    /**
     * Sets the image as an urn, i.e. a reference to the image database
     */
    public void setImage(String urn) {
        this.image = urn;
    }

    @Override
    public String toString() {
        return "ImageUpdate{" +
            "image=" + image +
            ", type=" + type +
            ", title=" + title +
            '}';
    }

    public Set<ConstraintViolation<ImageUpdate>> violations(Class<?>... groups) {
        if (VALIDATOR != null) {
            return VALIDATOR.validate(this, groups);
        } else {
            log.warn("Cannot validate since no validator available");
            return Collections.emptySet();
        }
    }
}
