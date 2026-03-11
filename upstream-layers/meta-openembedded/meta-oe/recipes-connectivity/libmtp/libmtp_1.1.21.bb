# TODO: include debian's mtp-tools man page (needs xsltproc-native and
# docbook-xsl-native, or we pregenerate it), add support for doxygen
# generation fully with -natives
DESCRIPTION = "libmtp is an Initiator implementation of the Media Transfer \
Protocol (MTP) in the form of a library suitable primarily for POSIX \
compliant operating systems"
SUMMARY = "libmtp is an Initiator implementation of the Media Transfer Protocol (MTP)"
HOMEPAGE = "http://libmtp.sourceforge.net/"
LICENSE = "LGPL-2.1-or-later"
LIC_FILES_CHKSUM = "file://COPYING;md5=0448d3676bc0de00406af227d341a4d1 \
                    file://src/ptp.c;beginline=3;endline=22;md5=80fd2d5904c4c1f5455d8f4bf515292f \
                    file://examples/albums.c;beginline=5;endline=21;md5=84f4e55dfec49e898b7f68a828c15620 \
                    "

BBCLASSEXTEND = "native"

DEPENDS = "libusb1 gettext-native"
DEPENDS:append:class-target = " ${BPN}-native"

SRC_URI = "https://github.com/${BPN}/${BPN}/releases/download/v${PV}/${BP}.tar.gz"
SRC_URI:append:class-target = " file://0001-Use-native-mtp-hotplug.patch \
                                file://0002-util-mtp-hotplug.c-Enable-stack-memory-protection.patch"
SRC_URI[sha256sum] = "f4c1ceb3df020a6cb851110f620c14fe399518c494ed252039cbfb4e34335135"

UPSTREAM_CHECK_URI = "https://github.com/libmtp/libmtp/releases"
UPSTREAM_CHECK_REGEX = "(?P<pver>\d+(\.\d+)+)"

inherit autotools pkgconfig lib_package

EXTRA_OECONF += " \
    --disable-rpath \
    --enable-largefile \
    --with-udev=${nonarch_base_libdir}/udev \
"

PACKAGECONFIG ?= ""
PACKAGECONFIG[doxygen] = "--enable-doxygen,--disable-doxygen,doxygen-native"
PACKAGECONFIG[mtpz] = "--enable-mtpz,--disable-mtpz,libgcrypt"

PACKAGES =+ "${BPN}-common ${BPN}-runtime"

RDEPENDS:${BPN} += "libmtp-common"
RRECOMMENDS:${BPN} += "libmtp-runtime ${PN}-bin"

FILES:${BPN}-common = "${nonarch_base_libdir}/udev/rules.d/*"
SUMMARY:${BPN}-common = "The udev rules file for MTP devices"

FILES:${BPN}-runtime = "${nonarch_base_libdir}/udev/mtp-probe"
RDEPENDS:${BPN}-runtime = "libmtp-common"
SUMMARY:${BPN}-runtime = "mtp-probe, used for the MTP udev rules"
DESCRIPTION:${BPN}-runtime = "This package provides mtp-probe, a program to probe newly connected device interfaces from userspace to determine if they are MTP devices, used for udev rules."
