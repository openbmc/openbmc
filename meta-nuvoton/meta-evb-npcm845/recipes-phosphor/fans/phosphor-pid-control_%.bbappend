FILESEXTRAPATHS:prepend := "${THISDIR}/${PN}:"

SRC_URI:append = " \
    file://config-evb-npcm845.json \
    file://phosphor-pid-control.service \
    "

FILES:${PN}:append = " ${datadir}/swampd/config.json"
RDEPENDS:${PN} += "bash"

SYSTEMD_SERVICE:${PN}:append = " phosphor-pid-control.service"

do_install:append() {
    install -d ${D}${datadir}/swampd
    install -m 0644 -D ${UNPACKDIR}/config-evb-npcm845.json \
        ${D}${datadir}/swampd/config.json

    install -d ${D}${systemd_system_unitdir}
    install -m 0644 ${UNPACKDIR}/phosphor-pid-control.service \
        ${D}${systemd_system_unitdir}
}
