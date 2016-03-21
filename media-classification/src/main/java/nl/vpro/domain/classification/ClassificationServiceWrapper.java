package nl.vpro.domain.classification;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.time.Instant;
import java.util.Collection;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Wraps another ClassifcationService. The goal is that this can implicitely user an {@link URLClassificationServiceImpl} (using cache headers), or a simple {@link ClassificationServiceImpl}.
 * @author Michiel Meeuwissen
 * @since 3.2
 */
public class ClassificationServiceWrapper implements ClassificationService {

    private static Logger LOG = LoggerFactory.getLogger(ClassificationServiceWrapper.class);

    private final ClassificationService service;

    public ClassificationServiceWrapper(ClassificationService wrapped) {
        this.service = wrapped;
    }

    public ClassificationServiceWrapper(URI url) {
        this.service = new URLClassificationServiceImpl(url);
    }


    public ClassificationServiceWrapper(String url) throws MalformedURLException {
        this.service = getService(url);
    }

    private static ClassificationService getService(String url) {
        if (url.startsWith("http")) {
            return new CachedURLClassificationServiceImpl(URI.create(url));
        } else {
            try {
                return new ClassificationServiceImpl(url);
            } catch (MalformedURLException | URISyntaxException e) {
                throw new IllegalArgumentException(e);
            }
        }

    }



    @Override
    public Term getTerm(String termId) throws TermNotFoundException {
        return service.getTerm(termId);

    }

    @Override
    public List<Term> getTermsByReference(String reference) {
        return service.getTermsByReference(reference);

    }

    @Override
    public boolean hasTerm(String termId) {
        return service.hasTerm(termId);

    }

    @Override
    public Collection<Term> values() {
        return service.values();

    }

    @Override
    public Collection<Term> valuesOf(String termId) {
        return service.valuesOf(termId);

    }

    @Override
    public ClassificationScheme getClassificationScheme() {
        return service.getClassificationScheme();

    }

    @Override
    public Instant getLastModified() {
        return service.getLastModified();
    }

    @Override
    public String toString() {
        return "" + service;
    }
}
