FILESEXTRAPATHS:prepend:evb-npcm845 := "${THISDIR}/${PN}:"

SRC_URI:append:evb-npcm845 = " \
    file://config-evb-npcm845.json \
    file://phosphor-pid-control.service \
    "
FILES:${PN}:append:evb-npcm845 = " ${datadir}/swampd/config.json"
RDEPENDS:${PN} += "bash"

SYSTEMD_SERVICE:${PN}:append:evb-npcm845 = " phosphor-pid-control.service"

do_install:append:evb-npcm845() {
    install -d ${D}${datadir}/swampd
    install -m 0644 -D ${WORKDIR}/config-evb-npcm845.json \
        ${D}${datadir}/swampd/config.json

    install -d ${D}${systemd_system_unitdir}
    install -m 0644 ${WORKDIR}/phosphor-pid-control.service \
        ${D}${systemd_system_unitdir}
}

