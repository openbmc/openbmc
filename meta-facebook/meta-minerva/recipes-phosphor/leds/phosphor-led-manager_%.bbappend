FILESEXTRAPATHS:prepend:minerva := "${THISDIR}/${PN}:"

SRC_URI:append:minerva = " file://led-group-config.json"

do_install:append:minerva() {
        install -m 0644 ${WORKDIR}/led-group-config.json ${D}${datadir}/phosphor-led-manager/
}
