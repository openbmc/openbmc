FILESEXTRAPATHS:prepend := "${THISDIR}/${BPN}:"

SRC_URI += "file://config.json"

do_install:append() {
    install -d ${D}${sysconfdir}/phosphor-fan-presence/monitor
    install -m 0644 ${UNPACKDIR}/config.json ${D}${sysconfdir}/phosphor-fan-presence/monitor/config.json
}
