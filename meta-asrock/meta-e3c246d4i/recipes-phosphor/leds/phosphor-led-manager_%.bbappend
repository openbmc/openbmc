FILESEXTRAPATHS:prepend:e3c246d4i := "${THISDIR}/${PN}:"

SRC_URI:append:e3c246d4i = " file://led-group-config.json"

do_install:append:e3c246d4i() {
    install -m 0644 ${UNPACKDIR}/led-group-config.json ${D}${datadir}/phosphor-led-manager/
}
