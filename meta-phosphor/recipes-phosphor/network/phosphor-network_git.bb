SUMMARY = "Network DBUS object"
DESCRIPTION = "Network DBUS object"
HOMEPAGE = "http://github.com/openbmc/phosphor-networkd"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=fa818a259cbed7ce8bc2a22d35a464fc"
DEPENDS += "systemd"
DEPENDS += "sdbusplus ${PYTHON_PN}-sdbus++-native"
DEPENDS += "sdeventplus"
DEPENDS += "phosphor-dbus-interfaces"
DEPENDS += "phosphor-logging"
DEPENDS += "libnl"
DEPENDS += "stdplus"
SRCREV = "4a27fdc5d1f2a496ea526d359f7ceaabfea56a2b"
RDEPENDS:${PN}:append:df-lldpd = " lldpd"
PACKAGECONFIG ??= "default-link-local-autoconf default-ipv6-accept-ra persist-mac"
PACKAGECONFIG[default-link-local-autoconf] = "-Ddefault-link-local-autoconf=true,-Ddefault-link-local-autoconf=false,,"
PACKAGECONFIG[default-ipv6-accept-ra] = "-Ddefault-ipv6-accept-ra=true,-Ddefault-ipv6-accept-ra=false,,"
PACKAGECONFIG[sync-mac] = "-Dsync-mac=true,-Dsync-mac=false,nlohmann-json,"
PACKAGECONFIG[hyp-nw-config] = "-Dhyp-nw-config=true, -Dhyp-nw-config=false,,"
PACKAGECONFIG[persist-mac] = "-Dpersist-mac=true, -persist-mac=false,,"
PV = "1.0+git${SRCPV}"
PR = "r1"

SRC_URI = "git://github.com/openbmc/phosphor-networkd;branch=master;protocol=https"

S = "${WORKDIR}/git"
UNPACKDIR = "${WORKDIR}/sources-unpack"
SYSTEMD_PACKAGES = "${PN}"
SYSTEMD_SERVICE:${PN} += "xyz.openbmc_project.Network.service"
SYSTEMD_SERVICE:${PN} += "${@bb.utils.contains('PACKAGECONFIG', 'hyp-nw-config', 'xyz.openbmc_project.Network.Hypervisor.service', '', d)}"

inherit meson pkgconfig
inherit python3native
inherit systemd

EXTRA_OEMESON:append = " -Dtests=disabled"

FILES:${PN} += "${datadir}/dbus-1/system.d"
FILES:${PN} += "${systemd_unitdir}/network/60-phosphor-networkd-default.network"

