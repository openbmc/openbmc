FILESEXTRAPATHS:prepend := "${THISDIR}/${PN}:"

SRC_URI += "file://led-group-config.json"

PACKAGECONFIG:append = " use-json use-lamp-test"

do_install:append() {
        install -m 0644 ${WORKDIR}/led-group-config.json ${D}${datadir}/phosphor-led-manager/
}
