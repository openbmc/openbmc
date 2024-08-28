# To add another RMCPP interface, add similar lines to the
# following lines in a bbappend:
#
# ALT_RMCPP_IFACE = "eth1"
# SYSTEMD_SERVICE:${PN} += " \
#     ${PN}@${ALT_RMCPP_IFACE}.service \
#     ${PN}@${ALT_RMCPP_IFACE}.socket \
#     "
# Also, be sure to enable a corresponding entry in the channel
# config file with the same 'name' as the interfaces above
# Override the default phosphor-ipmi-config.bb with a bbappend
SUMMARY = "Phosphor Network IPMI Daemon"
DESCRIPTION = "Daemon to support IPMI protocol over network"
HOMEPAGE = "https://github.com/openbmc/phosphor-net-ipmid"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=e3fc50a88d0a364313df4b21ef20c29e"
DEPENDS += "cli11"
DEPENDS += "systemd"
DEPENDS += "phosphor-ipmi-host"
SRCREV = "8425624a9046f5a853e8596cc74441e622028494"
PV = "1.0+git${SRCPV}"
PR = "r1"

SRC_URI = "git://github.com/openbmc/phosphor-net-ipmid;branch=master;protocol=https"

S = "${WORKDIR}/git"
# install parameterized service and socket files
SYSTEMD_SERVICE:${PN} = " \
        ${PN}@${RMCPP_IFACE}.service \
        ${PN}@${RMCPP_IFACE}.socket \
        "

inherit meson pkgconfig
inherit systemd

PACKAGECONFIG ??= "pam-authenticate rmcp-ping"
PACKAGECONFIG[rmcp-ping] = "-Drmcp_ping=enabled,-Drmcp_ping=disabled"
PACKAGECONFIG[pam-authenticate] = "-Dpam_authenticate=enabled,-Dpam_authenticate=disabled"

EXTRA_OEMESON = " \
        -Dtests=disabled \
        "

RDEPENDS:${PN} += "srvcfg-manager"
RRECOMMENDS:${PN} = "pam-ipmi"

FILES:${PN} += " \
        ${systemd_system_unitdir}/${PN}@.service \
        ${systemd_system_unitdir}/${PN}@.socket \
        "

# If RMCPP_IFACE is not set by bbappend, set it to default
DEFAULT_RMCPP_IFACE = "eth0"
RMCPP_IFACE ?= "${DEFAULT_RMCPP_IFACE}"
