package de.tudresden.cib.vis.DSL

import de.tudresden.cib.vis.mapping.Configuration

class VisDSL {

    VisTechnique vt(Configuration config, Closure closure) {

        VisTechnique vt = new VisTechnique(config)
        vt.with(closure)
    }

}
