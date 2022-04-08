SUMMARY = "Network DBUS object"
DESCRIPTION = "Network DBUS object"
HOMEPAGE = "http://github.com/openbmc/phosphor-networkd"
PR = "r1"
PV = "1.0+git${SRCPV}"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=fa818a259cbed7ce8bc2a22d35a464fc"

inherit meson pkgconfig
inherit python3native
inherit systemd

SRC_URI += "git://github.com/openbmc/phosphor-networkd;branch=master;protocol=https"
SRCREV = "c2e061f2ce0f5ece2deb7a8c4bbd1e4c06e2cfcb"

DEPENDS += "systemd"
DEPENDS += "sdbusplus ${PYTHON_PN}-sdbus++-native"
DEPENDS += "sdeventplus"
DEPENDS += "phosphor-dbus-interfaces"
DEPENDS += "phosphor-logging"
DEPENDS += "libnl"
DEPENDS += "stdplus"

PACKAGECONFIG ??= "uboot-env default-link-local-autoconf default-ipv6-accept-ra"

UBOOT_ENV_RDEPENDS = "${@d.getVar('PREFERRED_PROVIDER_u-boot-fw-utils', True) or 'u-boot-fw-utils'}"
PACKAGECONFIG[uboot-env] = "-Duboot-env=true,-Duboot-env=false,,${UBOOT_ENV_RDEPENDS}"
PACKAGECONFIG[default-link-local-autoconf] = "-Ddefault-link-local-autoconf=true,-Ddefault-link-local-autoconf=false,,"
PACKAGECONFIG[default-ipv6-accept-ra] = "-Ddefault-ipv6-accept-ra=true,-Ddefault-ipv6-accept-ra=false,,"
PACKAGECONFIG[nic-ethtool] = "-Dnic-ethtool=true,-Dnic-ethtool=false,,"
PACKAGECONFIG[sync-mac] = "-Dsync-mac=true,-Dsync-mac=false,nlohmann-json,"
PACKAGECONFIG[hyp-nw-config] = "-Dhyp-nw-config=true, -Dhyp-nw-config=false,,"

S = "${WORKDIR}/git"

FILES:${PN} += "${datadir}/dbus-1/system.d"

SYSTEMD_PACKAGES = "${PN}"
SYSTEMD_SERVICE:${PN} += "xyz.openbmc_project.Network.service"
SYSTEMD_SERVICE:${PN} += "${@bb.utils.contains('PACKAGECONFIG', 'hyp-nw-config', 'xyz.openbmc_project.Network.Hypervisor.service', '', d)}"

EXTRA_OEMESON:append = " -Dtests=disabled"
