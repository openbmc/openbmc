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
    file://src/ptp.c;beginline=3;endline=22;md5=dafe6cfd1782f56471bb94ab06624c1f \
    file://examples/albums.c;beginline=5;endline=21;md5=84f4e55dfec49e898b7f68a828c15620 \
"

DEPENDS += "libusb1 gettext-native"

SCM_URI = "git://git.code.sf.net/p/libmtp/code"
SRC_URI = "\
    ${SOURCEFORGE_MIRROR}/${BPN}/${BPN}-${PV}.tar.gz \
    file://69-libmtp.rules \
    file://glibc-2.20.patch \
"
SRC_URI[md5sum] = "f80e45c0e6e5798c434bb1c26a7b602d"
SRC_URI[sha256sum] = "787679171baf8b3cf2fcc03196c705ab4d7cbc969bd71f9d3696be1ce7f1c63a"

# Currently we use a pregenerated rules file produced by mtp-hotplug, rather
# than having to depend upon libmtp-native or run mtp-hotplug in a postinst.
do_unpack[vardeps] += "skip_udev_rules_generation"
do_unpack[postfuncs] += "skip_udev_rules_generation"

skip_udev_rules_generation () {
    sed -i -e '/^noinst_DATA=/,/util\/mtp-hotplug -H/d' ${S}/Makefile.am
    cp ${WORKDIR}/69-libmtp.rules ${S}/
}

inherit autotools pkgconfig lib_package

EXTRA_OECONF += "--disable-rpath"

PACKAGECONFIG ?= "\
    ${@bb.utils.contains('DISTRO_FEATURES', 'largefile$', 'largefile', '', d)} \
"
PACKAGECONFIG[doxygen] = "--enable-doxygen,--disable-doxygen"
PACKAGECONFIG[largefile] = "--enable-largefile,--disable-largefile"
PACKAGECONFIG[mtpz] = "--enable-mtpz,--disable-mtpz,libgcrypt"

PACKAGES =+ "libmtp-common libmtp-runtime mtp-tools"

RDEPENDS_${PN} += "libmtp-common"
RRECOMMENDS_${PN} += "libmtp-runtime mtp-tools"
FILES_${PN}-dbg += "${nonarch_base_libdir}/udev/.debug/*"
PKG_${PN}-bin = "mtp-tools"
SUMMARY_${PN}-bin = "Tools for communicating with MTP devices"
DESCRIPTION_${PN}-bin = "${DESCRIPTION}\nThis package contains tools for communicating with MTP devices."
FILES_libmtp-common = "${nonarch_base_libdir}/udev/rules.d/*"
SUMMARY_libmtp-common = "The udev rules file for MTP devices"
FILES_libmtp-runtime = "${nonarch_base_libdir}/udev/mtp-probe"
DEPENDS_libmtp-runtime = "libmtp-common"
SUMMARY_libmtp-runtime = "mtp-probe, used for the MTP udev rules"
DESCRIPTION_libmtp-runtime = "This package provides mtp-probe, a program to probe newly connected device interfaces from userspace to determine if they are MTP devices, used for udev rules."
