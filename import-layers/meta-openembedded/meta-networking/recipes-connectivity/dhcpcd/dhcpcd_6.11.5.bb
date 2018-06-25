SECTION = "console/network"
SUMMARY = "dhcpcd - a DHCP client"
DESCRIPTION = "dhcpcd runs on your machine and silently configures your computer to work on the attached networks without trouble and mostly without configuration."

HOMEPAGE = "http://roy.marples.name/projects/dhcpcd/"

LICENSE = "BSD-2-Clause"
LIC_FILES_CHKSUM = "file://dhcpcd.c;endline=26;md5=77c40d671aff804ca91ea99556da8e9b"

SRC_URI = "http://roy.marples.name/downloads/${BPN}/${BPN}-${PV}.tar.xz"

SRC_URI[md5sum] = "2465624b62c1154f0e89dc69c42c849b"
SRC_URI[sha256sum] = "6f9674dc7e27e936cc787175404a6171618675ecfb6903ab9887b1b66a87d69e"

inherit autotools-brokensep

PACKAGECONFIG ?= "udev ${@bb.utils.filter('DISTRO_FEATURES', 'ipv6', d)}"

PACKAGECONFIG[udev] = "--with-udev,--without-udev,udev,udev"
PACKAGECONFIG[ipv6] = "--enable-ipv6,--disable-ipv6"

EXTRA_OECONF = "--enable-ipv4"

FILES_${PN}-dbg += "${libdir}/dhcpcd/dev/.debug"
