/*
 * Copyright (C) 2014 All rights reserved
 * VPRO The Netherlands
 */
package nl.vpro.domain.user;

import java.time.Instant;

/**
 * @author Roelof Jan Koekoek
 * @since 2.3
 */
public class TestEditors {

    public static Editor vproEditor() {
        return new Editor("editor@vpro.nl", "Editor", "editor@vpro.nl", new Broadcaster("VPRO", "VPRO"), "Test", "Editor", Instant.now());
    }

}
