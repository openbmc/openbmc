PACKAGECONFIG:append = " nuvoton-p2a-mbox net-bridge"
IPMI_FLASH_BMC_ADDRESS = "0xF0848000"

FILESEXTRAPATHS:prepend := "${THISDIR}/${PN}/bmc-update:${THISDIR}/${PN}/bios-update:${THISDIR}/${PN}/cpld-update:"

SRC_URI:append = " file://config-bmc.json \
                   file://config-bios.json \
                   file://config-bmccpld.json \
                   file://config-mbcpld.json \
                 "

SYSTEMD_SERVICE:${PN}:append = " phosphor-ipmi-flash-bios-update.service \
                                 phosphor-ipmi-flash-bmccpld-update.service \
                                 phosphor-ipmi-flash-mbcpld-update.service \
                               "

inherit obmc-phosphor-systemd

do_install:append() {
    install -d ${D}/${datadir}/phosphor-ipmi-flash
    install -m 0644 ${UNPACKDIR}/config-bmc.json \
        ${D}${datadir}/phosphor-ipmi-flash/
    install -m 0644 ${UNPACKDIR}/config-bios.json \
        ${D}${datadir}/phosphor-ipmi-flash/
    install -m 0644 ${UNPACKDIR}/config-bmccpld.json \
        ${D}${datadir}/phosphor-ipmi-flash/
    install -m 0644 ${UNPACKDIR}/config-mbcpld.json \
        ${D}${datadir}/phosphor-ipmi-flash/
}
