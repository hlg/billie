package de.tudresden.cib.vis.DSL

import de.tudresden.cib.vis.mapping.Mapper

class VisDSL {

    VisTechnique vt(def mapper, Closure closure) {

        VisTechnique vt = new VisTechnique(mapper)
        vt.with(closure)
    }

}
