FILESEXTRAPATHS:prepend:scm-npcm845 := "${THISDIR}/${PN}:"

SRC_URI:append:scm-npcm845 = " \
    file://fan-default-speed.sh \
    file://config-scm-npcm845.json \
    file://fan-reboot-control.service \
    file://fan-boot-control.service \
    file://phosphor-pid-control.service \
    "

FILES:${PN}:append:scm-npcm845 = " ${bindir}/fan-default-speed.sh"
FILES:${PN}:append:scm-npcm845 = " ${datadir}/swampd/config.json"

RDEPENDS:${PN} += "bash"

SYSTEMD_SERVICE:${PN}:append:scm-npcm845 = " fan-reboot-control.service"
SYSTEMD_SERVICE:${PN}:append:scm-npcm845 = " fan-boot-control.service"
SYSTEMD_SERVICE:${PN}:append:scm-npcm845 = " phosphor-pid-control.service"

do_install:append:scm-npcm845() {
    install -d ${D}/${bindir}
    install -m 0755 ${WORKDIR}/fan-default-speed.sh ${D}/${bindir}

    install -d ${D}${datadir}/swampd
    install -m 0644 -D ${WORKDIR}/config-scm-npcm845.json \
        ${D}${datadir}/swampd/config.json

    install -d ${D}${systemd_system_unitdir}
    install -m 0644 ${WORKDIR}/phosphor-pid-control.service \
        ${D}${systemd_system_unitdir}
    install -m 0644 ${WORKDIR}/fan-reboot-control.service \
        ${D}${systemd_system_unitdir}
    install -m 0644 ${WORKDIR}/fan-boot-control.service \
        ${D}${systemd_system_unitdir}
}

