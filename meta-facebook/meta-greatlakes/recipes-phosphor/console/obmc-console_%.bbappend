FILESEXTRAPATHS:prepend := "${THISDIR}/${PN}:"

SRC_URI:append = " file://select-uart-mux"

do_install:append() {
        install -m 0744 ${UNPACKDIR}/select-uart-mux ${D}${bindir}
}
