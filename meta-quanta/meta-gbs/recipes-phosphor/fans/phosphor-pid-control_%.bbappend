FILESEXTRAPATHS:prepend:gbs := "${THISDIR}/${PN}:"
SRC_URI:append:gbs = " file://config.json.in \
                       file://fan-table-init.sh \
                       file://phosphor-pid-control.service \
                     "

FILES:${PN}:append:gbs = " ${datadir}/swampd/config.json.in"
FILES:${PN}:append:gbs = " ${bindir}/fan-table-init.sh"

RDEPENDS:${PN} += "bash"

SYSTEMD_SERVICE:${PN}:append:gbs = " phosphor-pid-control.service"

do_install:append:gbs() {
    install -d ${D}/${bindir}
    install -m 0755 ${UNPACKDIR}/fan-table-init.sh ${D}/${bindir}

    install -d ${D}${datadir}/swampd
    install -m 0644 -D ${UNPACKDIR}/config.json.in \
        ${D}${datadir}/swampd/

    install -d ${D}${systemd_system_unitdir}
    install -m 0644 ${UNPACKDIR}/phosphor-pid-control.service \
        ${D}${systemd_system_unitdir}
}
