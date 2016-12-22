/*
 * Copyright (C) 2013 All rights reserved
 * VPRO The Netherlands
 */
package nl.vpro.domain.media.update;

import java.net.URI;
import java.net.URL;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Roelof Jan Koekoek
 * @since 2.1
 */
public class AssetLocationTest {

    @Test
    public void testResolveNonFileScheme() throws Exception {
        AssetLocation target = new AssetLocation("http://host/path/file.name");

        target.resolve("/base/path/");

        assertThat(target.getUrl()).isEqualTo("http://host/path/file.name");
    }

    @Test
    public void testResolveRelative() throws Exception {
        AssetLocation target = new AssetLocation("file.name");

        target.resolve("/base/path");

        assertThat(target.getUrl()).isEqualTo("file:/base/path/file.name");
    }

    @Test
    public void testResolveRelativeWithScheme() throws Exception {
        AssetLocation target = new AssetLocation("file:file.name");

        target.resolve("/base/path/");

        assertThat(target.getUrl()).isEqualTo("file:/base/path/file.name");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testResolveAbsolute() throws Exception {
        AssetLocation target = new AssetLocation("/file.name");

        target.resolve("/base/path/");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testResolveAbsoluteWithScheme() throws Exception {
        AssetLocation target = new AssetLocation("file:/file.name");

        target.resolve("/base/path/");
    }

    @Test(expected = SecurityException.class)
    public void testResolveWhenNavigatingUpPath() throws Exception {
        AssetLocation target = new AssetLocation("../../file.name");

        target.resolve("/base/path/");
    }
}
