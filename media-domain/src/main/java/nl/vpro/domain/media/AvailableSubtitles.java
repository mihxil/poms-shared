package nl.vpro.domain.media;

import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.util.Locale;

import javax.persistence.Cacheable;
import javax.persistence.Embeddable;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import nl.vpro.domain.subtitles.SubtitlesType;
import nl.vpro.xml.bind.LocaleAdapter;

@Embeddable
@Cacheable
@XmlType(name="AvailableSubtitlesType")
@EqualsAndHashCode
@ToString

public class AvailableSubtitles {

	private Locale language;

	@Enumerated(EnumType.STRING)
	private SubtitlesType type;

	public AvailableSubtitles() {

	}

	@lombok.Builder
	public AvailableSubtitles(Locale language, SubtitlesType type) {
		this.language = language;
		this.type = type;

	}

	@XmlJavaTypeAdapter(LocaleAdapter.class)
	@XmlAttribute
	public Locale getLanguage() {
		return language;
	}

	public void setLanguage(Locale language) {
		this.language = language;
	}

	@XmlAttribute
	public SubtitlesType getType() {
		return type;
	}

	public void setType(SubtitlesType type) {
		this.type = type;
	}
}
