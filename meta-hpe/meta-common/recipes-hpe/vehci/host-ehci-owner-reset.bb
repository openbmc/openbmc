SUMMARY = "GXP EHCI Owner Reset"
PR = "r1"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://${HPEBASE}/COPYING.apache-2.0;md5=34400b68072d710fecd0a2940a0d1658"

inherit obmc-phosphor-systemd

DEPENDS += "phosphor-gpio-monitor"
RDEPENDS:${PN} += "phosphor-gpio-monitor-monitor"

SYSTEMD_ENVIRONMENT_FILE:${PN} += "obmc/gpio/port_owner_udc0"
SYSTEMD_ENVIRONMENT_FILE:${PN} += "obmc/gpio/port_owner_udc1"
SYSTEMD_ENVIRONMENT_FILE:${PN} += "obmc/gpio/port_owner_udc2"

UDC0_GPIO = "port_owner_udc0"
UDC1_GPIO = "port_owner_udc1"
UDC2_GPIO = "port_owner_udc2"
TMPL_GPIO = "phosphor-gpio-monitor@.service"
INSTFMT_GPIO = "phosphor-gpio-monitor@{0}.service"
TGT_GPIO = "multi-user.target.requires"
FMT_GPIO = "../${TMPL_GPIO}:${TGT_GPIO}/${INSTFMT_GPIO}"
SYSTEMD_LINK:${PN} += "${@compose_list(d, 'FMT_GPIO', 'UDC0_GPIO')}"
SYSTEMD_LINK:${PN} += "${@compose_list(d, 'FMT_GPIO', 'UDC1_GPIO')}"
SYSTEMD_LINK:${PN} += "${@compose_list(d, 'FMT_GPIO', 'UDC2_GPIO')}"

UDC0_VEHCI = "udc0"
UDC1_VEHCI = "udc1"
UDC2_VEHCI = "udc2"
TMPL_VEHCI = "host-ehci-owner-reset@.service"
INSTFMT_VEHCI = "host-ehci-owner-reset@{0}.service"
FMT_VEHCI = "${TMPL_VEHCI}:${INSTFMT_VEHCI}"
SYSTEMD_LINK:${PN} += "${@compose_list(d, 'FMT_VEHCI', 'UDC0_VEHCI')}"
SYSTEMD_LINK:${PN} += "${@compose_list(d, 'FMT_VEHCI', 'UDC1_VEHCI')}"
SYSTEMD_LINK:${PN} += "${@compose_list(d, 'FMT_VEHCI', 'UDC2_VEHCI')}"

SYSTEMD_SERVICE:${PN} += "host-ehci-owner-reset@.service"

SRC_URI += "file://udc-reconnect.sh"
SRC_URI += "file://host-ehci-owner-reset@.service"

do_install() {
	install -d ${D}${bindir}
	install -m 755 ${UNPACKDIR}/udc-reconnect.sh ${D}${bindir}
}
