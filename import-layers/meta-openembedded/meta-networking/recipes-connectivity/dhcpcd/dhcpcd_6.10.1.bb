SECTION = "console/network"
SUMMARY = "dhcpcd - a DHCP client"
DESCRIPTION = "dhcpcd runs on your machine and silently configures your computer to work on the attached networks without trouble and mostly without configuration."

HOMEPAGE = "http://roy.marples.name/projects/dhcpcd/"

LICENSE = "BSD-2-Clause"
LIC_FILES_CHKSUM = "file://dhcpcd.c;endline=26;md5=77c40d671aff804ca91ea99556da8e9b"

SRC_URI = "http://roy.marples.name/downloads/${BPN}/${BPN}-${PV}.tar.xz"

SRC_URI[md5sum] = "a7b83c57f47b62f48373905d3b4f7978"
SRC_URI[sha256sum] = "284abf8c3be0580bbac5eaca95359346ab0d78d4072317b6ce87cc68f2e8ae7b"

inherit autotools-brokensep

PACKAGECONFIG ?= "udev ${@bb.utils.contains("DISTRO_FEATURES", "ipv6", "ipv6", "", d)}"

PACKAGECONFIG[udev] = "--with-udev,--without-udev,udev,udev"
PACKAGECONFIG[ipv6] = "--enable-ipv6,--disable-ipv6"

EXTRA_OECONF = "--enable-ipv4"

FILES_${PN}-dbg += "${libdir}/dhcpcd/dev/.debug"
