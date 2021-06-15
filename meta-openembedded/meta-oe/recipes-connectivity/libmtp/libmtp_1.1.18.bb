# TODO: include debian's mtp-tools man page (needs xsltproc-native and
# docbook-xsl-native, or we pregenerate it), add support for doxygen
# generation fully with -natives
DESCRIPTION = "libmtp is an Initiator implementation of the Media Transfer \
Protocol (MTP) in the form of a library suitable primarily for POSIX \
compliant operating systems"
SUMMARY = "libmtp is an Initiator implementation of the Media Transfer Protocol (MTP)"
HOMEPAGE = "http://libmtp.sourceforge.net/"
LICENSE = "LGPL-2.1+"
LIC_FILES_CHKSUM = "\
    file://COPYING;md5=0448d3676bc0de00406af227d341a4d1 \
    file://src/ptp.c;beginline=3;endline=22;md5=303cc4422fd549ef8689aded20434a9a \
    file://examples/albums.c;beginline=5;endline=21;md5=84f4e55dfec49e898b7f68a828c15620 \
"

BBCLASSEXTEND = "native"

DEPENDS = "libusb1 gettext-native"
DEPENDS_append_class-target = " ${BPN}-native"

SRC_URI = "${SOURCEFORGE_MIRROR}/${BPN}/${BPN}-${PV}.tar.gz"
SRC_URI_append_class-target = " file://0001-Use-native-mtp-hotplug.patch"

SRC_URI[md5sum] = "7915496daa3f4ea3e095f0161f83d4d4"
SRC_URI[sha256sum] = "7280fe50c044c818a06667f45eabca884deab3193caa8682e0b581e847a281f0"

inherit autotools pkgconfig lib_package

EXTRA_OECONF += " \
    --disable-rpath \
    --with-udev=${nonarch_base_libdir}/udev \
"

PACKAGECONFIG ?= "${@bb.utils.filter('DISTRO_FEATURES', 'largefile', d)}"
PACKAGECONFIG[doxygen] = "--enable-doxygen,--disable-doxygen"
PACKAGECONFIG[largefile] = "--enable-largefile,--disable-largefile"
PACKAGECONFIG[mtpz] = "--enable-mtpz,--disable-mtpz,libgcrypt"

PACKAGES =+ "${BPN}-common ${BPN}-runtime"

RDEPENDS_${BPN} += "libmtp-common"
RRECOMMENDS_${BPN} += "libmtp-runtime ${PN}-bin"

FILES_${BPN}-common = "${nonarch_base_libdir}/udev/rules.d/*"
SUMMARY_${BPN}-common = "The udev rules file for MTP devices"

FILES_${BPN}-runtime = "${nonarch_base_libdir}/udev/mtp-probe"
RDEPENDS_${BPN}-runtime = "libmtp-common"
SUMMARY_${BPN}-runtime = "mtp-probe, used for the MTP udev rules"
DESCRIPTION_${BPN}-runtime = "This package provides mtp-probe, a program to probe newly connected device interfaces from userspace to determine if they are MTP devices, used for udev rules."
