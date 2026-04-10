SUMMARY = "Isocline is a portable GNU readline alternative."
DESCRIPTION = "Isocline is a pure C library that can be used \
              as an alternative to the GNU readline library. \
              "
HOMEPAGE = "https://github.com/daanx/isocline"
BUGTRACKER = "https://github.com/daanx/isocline/issues"

LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=8d05469c537534c7405c82c81a526bcd"

SRC_URI = "git://github.com/daanx/isocline.git;protocol=https;branch=main;tag=v${PV}"
SRCREV = "74d34bc453f5b91f1f8d8ded2840e1553623d135"


inherit cmake

CFLAGS += "-fPIC"

do_install() {
    install -d ${D}${libdir}
    install -m 0644 ${B}/libisocline.a ${D}${libdir}/
    install -d ${D}${includedir}
    cp ${S}/include/isocline.h ${D}${includedir}/
}

FILES:${PN}-dev = "${libdir}/libisocline.a"
FILES:${PN}-dev = "${includedir}/isocline.h"
FILES:${PN}-dbg += "${libdir}/.debug/libisocline.a"
