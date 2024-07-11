SUMMARY = "Userspace library to access USB (version 1.0)"
DESCRIPTION = "A cross-platform library to access USB devices from Linux, \
macOS, Windows, OpenBSD/NetBSD, Haiku and Solaris userspace."
HOMEPAGE = "https://libusb.info"
BUGTRACKER = "http://www.libusb.org/report"
SECTION = "libs"

LICENSE = "LGPL-2.1-or-later"
LIC_FILES_CHKSUM = "file://COPYING;md5=fbc093901857fcd118f065f900982c24"

CVE_PRODUCT = "libusb"

BBCLASSEXTEND = "native nativesdk"

SRC_URI = "${GITHUB_BASE_URI}/download/v${PV}/libusb-${PV}.tar.bz2 \
           file://run-ptest \
           "

GITHUB_BASE_URI = "https://github.com/libusb/libusb/releases"
UPSTREAM_CHECK_REGEX = "releases/tag/v?(?P<pver>\d+(\.\d+)+)$"

SRC_URI[sha256sum] = "ffaa41d741a8a3bee244ac8e54a72ea05bf2879663c098c82fc5757853441575"

S = "${WORKDIR}/libusb-${PV}"

inherit autotools pkgconfig ptest github-releases

PACKAGECONFIG:class-target ??= "udev"
PACKAGECONFIG[udev] = "--enable-udev,--disable-udev,udev"

EXTRA_OECONF = "--libdir=${base_libdir}"

do_install:append() {
	install -d ${D}${libdir}
	if [ ! ${D}${libdir} -ef ${D}${base_libdir} ]; then
		mv ${D}${base_libdir}/pkgconfig ${D}${libdir}
	fi
}

do_compile_ptest() {
    oe_runmake -C tests stress
}

do_install_ptest() {
    install -m 755 ${B}/tests/stress ${D}${PTEST_PATH}
}

FILES:${PN} += "${base_libdir}/*.so.*"

FILES:${PN}-dev += "${base_libdir}/*.so ${base_libdir}/*.la"
