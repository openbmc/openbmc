SECTION = "console/network"
SUMMARY = "dhcpcd - a DHCP client"
DESCRIPTION = "dhcpcd runs on your machine and silently configures your computer to work on the attached networks without trouble and mostly without configuration."

HOMEPAGE = "http://roy.marples.name/projects/dhcpcd/"

LICENSE = "BSD-2-Clause"
LIC_FILES_CHKSUM = "file://LICENSE;md5=0531457992a97ecebc6975914a874a9d"

UPSTREAM_CHECK_URI = "https://roy.marples.name/downloads/dhcpcd/"

SRC_URI = "http://roy.marples.name/downloads/${BPN}/${BPN}-${PV}.tar.xz \
           file://0001-remove-INCLUDEDIR-to-prevent-build-issues.patch"
SRC_URI[md5sum] = "c0375a1f725f1c191b43af60e4f7175b"
SRC_URI[sha256sum] = "33a26ad561546cd2cfe1e6de6352a85df72b41c37def8c7eb00e90e57c627a5c"

inherit pkgconfig autotools-brokensep

PACKAGECONFIG ?= "udev ${@bb.utils.filter('DISTRO_FEATURES', 'ipv6', d)}"

PACKAGECONFIG[udev] = "--with-udev,--without-udev,udev,udev"
PACKAGECONFIG[ipv6] = "--enable-ipv6,--disable-ipv6"

EXTRA_OECONF = "--enable-ipv4"

FILES_${PN}-dbg += "${libdir}/dhcpcd/dev/.debug"
