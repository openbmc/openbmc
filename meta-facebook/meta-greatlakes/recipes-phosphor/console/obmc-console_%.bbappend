FILESEXTRAPATHS:prepend := "${THISDIR}/${PN}:"

SRC_URI:append:greatlakes = " file://select-uart-mux"

do_install:append:greatlakes() {
        install -m 0744 ${WORKDIR}/select-uart-mux ${D}${bindir}
}
