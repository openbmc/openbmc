FILESEXTRAPATHS:prepend:e3c256d4i := "${THISDIR}/${PN}:"

SRC_URI:append:e3c256d4i = " file://led-group-config.json"

do_install:append:e3c256d4i() {
        install -m 0644 ${UNPACKDIR}/led-group-config.json ${D}${datadir}/phosphor-led-manager/
}
