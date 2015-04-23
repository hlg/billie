package de.tudresden.cib.vis.DSL

class VisDSL {

    VisTechnique vt(Closure closure) {
        VisTechnique vt = new VisTechnique()
        closure.delegate = vt
        closure.resolveStrategy = Closure.DELEGATE_FIRST
        closure()
    }

}
