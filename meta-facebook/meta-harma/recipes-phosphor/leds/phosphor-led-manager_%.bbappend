FILESEXTRAPATHS:prepend:harma := "${THISDIR}/${PN}:"

SRC_URI:append:harma = " file://led-group-config.json"

do_install:append:harma() {
        install -m 0644 ${WORKDIR}/led-group-config.json ${D}${datadir}/phosphor-led-manager/
}
