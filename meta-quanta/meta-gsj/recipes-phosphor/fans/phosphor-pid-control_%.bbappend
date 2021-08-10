FILESEXTRAPATHS:prepend:gsj := "${THISDIR}/${PN}:"
SRC_URI:append:gsj = " file://config-8ssd.json"
SRC_URI:append:gsj = " file://config-2ssd.json"
SRC_URI:append:gsj = " file://fan-control.sh"
SRC_URI:append:gsj = " file://fan-default-speed.sh"
SRC_URI:append:gsj = " file://phosphor-pid-control.service"
SRC_URI:append:gsj = " file://fan-reboot-control.service"
SRC_URI:append:gsj = " file://fan-boot-control.service"

FILES:${PN}:append:gsj = " ${datadir}/swampd/config-8ssd.json"
FILES:${PN}:append:gsj = " ${datadir}/swampd/config-2ssd.json"
FILES:${PN}:append:gsj = " ${bindir}/fan-control.sh"
FILES:${PN}:append:gsj = " ${bindir}/fan-default-speed.sh"

inherit systemd
RDEPENDS:${PN} += "bash"

SYSTEMD_SERVICE:${PN}:append:gsj = " phosphor-pid-control.service"
SYSTEMD_SERVICE:${PN}:append:gsj = " fan-reboot-control.service"
SYSTEMD_SERVICE:${PN}:append:gsj = " fan-boot-control.service"

do_install:append:gsj() {
    install -d ${D}/${bindir}
    install -m 0755 ${WORKDIR}/fan-control.sh ${D}/${bindir}
    install -m 0755 ${WORKDIR}/fan-default-speed.sh ${D}/${bindir}

    install -d ${D}${datadir}/swampd
    install -m 0644 -D ${WORKDIR}/config-8ssd.json \
        ${D}${datadir}/swampd/config-8ssd.json
    install -m 0644 -D ${WORKDIR}/config-2ssd.json \
        ${D}${datadir}/swampd/config-2ssd.json

    install -d ${D}${systemd_unitdir}/system/
    install -m 0644 ${WORKDIR}/phosphor-pid-control.service \
        ${D}${systemd_unitdir}/system
    install -m 0644 ${WORKDIR}/fan-reboot-control.service \
        ${D}${systemd_unitdir}/system
    install -m 0644 ${WORKDIR}/fan-boot-control.service \
        ${D}${systemd_unitdir}/system
}
