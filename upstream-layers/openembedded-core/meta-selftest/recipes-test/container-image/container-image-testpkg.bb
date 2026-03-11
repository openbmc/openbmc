LICENSE = "MIT"

INHIBIT_DEFAULT_DEPS = "1"

do_install:append() {
    install -d ${D}${bindir}
    touch ${D}${bindir}/theapp
}
