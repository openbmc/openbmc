FILESEXTRAPATHS:prepend:evb-npcm750 := "${THISDIR}/${PN}:"

SRC_URI:append:evb-npcm750 = " \
    file://config-evb-nuvoton.json \
    file://fan-default-speed.sh \
    file://fan-reboot-control.service \
    file://fan-boot-control.service \
    file://phosphor-pid-control_evb.service \
    "

FILES:${PN}:append:evb-npcm750 = " \
    ${bindir}/fan-default-speed.sh \
    ${datadir}/swampd/config.json \
    "

RDEPENDS:${PN} += "bash"

SYSTEMD_SERVICE:${PN}:append:evb-npcm750 = " \
    fan-reboot-control.service \
    fan-boot-control.service \
    "

# default recipe already include phosphor-pid-control.service

do_install:append:evb-npcm750() {
    install -d ${D}/${bindir}
    install -m 0755 ${WORKDIR}/fan-default-speed.sh ${D}/${bindir}

    install -d ${D}${datadir}/swampd
    install -m 0644 -D ${WORKDIR}/config-evb-nuvoton.json \
        ${D}${datadir}/swampd/config.json

    install -d ${D}${systemd_unitdir}/system/
    install -m 0644 ${WORKDIR}/phosphor-pid-control_evb.service \
        ${D}${systemd_unitdir}/system/phosphor-pid-control.service
    install -m 0644 ${WORKDIR}/fan-reboot-control.service \
        ${D}${systemd_unitdir}/system
    install -m 0644 ${WORKDIR}/fan-boot-control.service \
        ${D}${systemd_unitdir}/system
}

EXTRA_OECONF:append:evb-npcm750 = " --enable-configure-dbus=yes"
