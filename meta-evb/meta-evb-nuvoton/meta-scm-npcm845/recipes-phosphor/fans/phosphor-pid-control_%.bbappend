FILESEXTRAPATHS:prepend:scm-npcm845 := "${THISDIR}/${PN}:"
SRC_URI:append:scm-npcm845 = " file://config.json \
                       file://phosphor-pid-control.service \
                     "

FILES:${PN}:append:scm-npcm845 = " ${datadir}/swampd/config.json"

RDEPENDS:${PN} += "bash"

SYSTEMD_SERVICE:${PN}:append:scm-npcm845 = " phosphor-pid-control.service"

do_install:append:scm-npcm845() {
    install -d ${D}/${bindir}

    install -d ${D}${datadir}/swampd
    install -m 0644 -D ${WORKDIR}/config.json \
        ${D}${datadir}/swampd/

    install -d ${D}${systemd_system_unitdir}
    install -m 0644 ${WORKDIR}/phosphor-pid-control.service \
        ${D}${systemd_system_unitdir}
}
