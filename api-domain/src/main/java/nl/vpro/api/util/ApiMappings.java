package nl.vpro.api.util;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.net.URI;

import javax.inject.Inject;
import javax.inject.Named;

import nl.vpro.domain.Mappings;
import nl.vpro.domain.Xmlns;
import nl.vpro.domain.api.media.MediaSearchResult;
import nl.vpro.domain.api.media.MediaSearchResults;
import nl.vpro.domain.api.media.RedirectList;
import nl.vpro.domain.api.media.ScheduleForm;
import nl.vpro.domain.api.page.PageForm;
import nl.vpro.domain.api.page.PageSearchResult;
import nl.vpro.domain.api.page.PageSearchResults;
import nl.vpro.domain.api.profile.Profile;
import nl.vpro.domain.api.subtitles.SubtitlesForm;
import nl.vpro.domain.image.ImageType;
import nl.vpro.domain.page.Page;
import nl.vpro.domain.page.update.PageUpdate;
import nl.vpro.domain.secondscreen.Screen;
import nl.vpro.domain.subtitles.Subtitles;
import nl.vpro.domain.subtitles.SubtitlesType;

import static nl.vpro.domain.Xmlns.*;

/**
 * @author Michiel Meeuwissen
 * @since 5.3
 */
@Slf4j
public class ApiMappings extends Mappings {

    @Getter
    final URI pomsLocation;

    @Inject
    public ApiMappings(@Named("${poms.baseUrl}") String pomsLocation) {
        this.pomsLocation = pomsLocation == null ? URI.create("https://poms.omroep.nl/") : URI.create(pomsLocation);
        String scheme = this.pomsLocation.getScheme();
        generateDocumentation = true;
        log.info("Using poms location {}", this.pomsLocation);
    }


    @Override
    protected void fillMappings() {
        MAPPING.put(PROFILE_NAMESPACE, new Class[]{Profile.class});
        MAPPING.put(API_NAMESPACE, new Class[]{PageForm.class, ScheduleForm.class, SubtitlesForm.class, RedirectList.class, MediaSearchResults.class, PageSearchResults.class, MediaSearchResult.class, PageSearchResult.class});
        MAPPING.put(PAGE_NAMESPACE, new Class[]{Page.class});
        MAPPING.put(PAGEUPDATE_NAMESPACE, new Class[]{PageUpdate.class, ImageType.class});
        MAPPING.put(SECOND_SCREEN_NAMESPACE, new Class[]{Screen.class});
        MAPPING.put(Xmlns.MEDIA_CONSTRAINT_NAMESPACE, new Class[]{nl.vpro.domain.constraint.media.Filter.class});
        MAPPING.put(Xmlns.PAGE_CONSTRAINT_NAMESPACE, new Class[]{nl.vpro.domain.constraint.page.Filter.class});
        MAPPING.put(Xmlns.CONSTRAINT_NAMESPACE, new Class[]{nl.vpro.domain.constraint.Operator.class});
        MAPPING.put(Xmlns.MEDIA_SUBTITLES_NAMESPACE, new Class[]{Subtitles.class, SubtitlesType.class});

        Xmlns.fillLocationsAtPoms(KNOWN_LOCATIONS, pomsLocation.toString());
    }


}
