

ALT_RMCPP_IFACE:p10bmc = "eth1"
SYSTEMD_SERVICE:${PN}:append:p10bmc = " \
    ${PN}@${ALT_RMCPP_IFACE}.service \
    ${PN}@${ALT_RMCPP_IFACE}.socket \
    "
ALT_RMCPP_IFACE:system1 = "eth1"
SYSTEMD_SERVICE:${PN}:append:system1 = " \
    ${PN}@${ALT_RMCPP_IFACE}.service \
    ${PN}@${ALT_RMCPP_IFACE}.socket \
    "

ALT_RMCPP_IFACE:sbp1 = "eth1"
SYSTEMD_SERVICE:${PN}:append:sbp1 = " \
    ${PN}@${ALT_RMCPP_IFACE}.service \
    ${PN}@${ALT_RMCPP_IFACE}.socket \
    "

FILESEXTRAPATHS:prepend:p10bmc := "${THISDIR}/${PN}:"
SRC_URI:append:p10bmc = " file://first-boot-disable-ipmi-net.service"
SYSTEMD_SERVICE:${PN}:append:p10bmc = " first-boot-disable-ipmi-net.service"

do_install:append:p10bmc() {
    install -d ${D}${systemd_system_unitdir}
    install -m 0644 ${UNPACKDIR}/first-boot-disable-ipmi-net.service ${D}${systemd_system_unitdir}/
}
