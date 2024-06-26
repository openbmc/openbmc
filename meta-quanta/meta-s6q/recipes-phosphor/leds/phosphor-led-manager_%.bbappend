FILESEXTRAPATHS:prepend:s6q := "${THISDIR}/${PN}:"

SRC_URI:append:s6q = " file://led-group-config.json"

PACKAGECONFIG:append:s6q = " use-lamp-test"

do_install:append:s6q() {
        install -m 0644 ${UNPACKDIR}/led-group-config.json ${D}${datadir}/phosphor-led-manager/
}
