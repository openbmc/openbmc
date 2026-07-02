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
SRC_URI[sha256sum] = "74a2b6e8cb4a0304e95b995496ea3ac644c29371649b892b856e22f12a0bdeed"

UPSTREAM_CHECK_URI = "https://github.com/libmtp/libmtp/releases"
UPSTREAM_CHECK_REGEX = "(?P<pver>\d+(\.\d+)+)"

inherit autotools pkgconfig lib_package

EXTRA_OECONF += " \
    --disable-rpath \
    --enable-largefile \
    --with-udev=${nonarch_base_libdir}/udev \
"

# Upstream 1.1.23 gained native crossbuild support: with this enabled the
# build uses the host (native) mtp-hotplug (HOST_MTP_HOTPLUG) instead of the
# just-built target binary to generate the udev rules/hwdb. libmtp-native
# provides mtp-hotplug on PATH.
EXTRA_OECONF:append:class-target = " --enable-crossbuilddir=${nonarch_base_libdir}/udev"

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
