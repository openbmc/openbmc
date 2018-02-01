LICENSE = "MIT"

INHIBIT_DEFAULT_DEPS = "1"

do_install_append() {
    install -d ${D}${bindir}
    touch ${D}${bindir}/theapp
}
