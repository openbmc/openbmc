SUMMARY = "A Portable Python 3.x Interpreter in Modern C."
DESCRIPTION = "pkpy is a lightweight(~15K LOC) Python 3.x \
              interpreter for game scripting, written in C11. \
              It aims to be an alternative to lua for game \
              scripting, with elegant syntax, powerful features \
              and competitive performance.  pkpy is extremely \
              easy to embed via a single header file pocketpy.h, \
              without external dependencies. \
              "
HOMEPAGE = "https://pocketpy.dev/"
BUGTRACKER = "https://github.com/pocketpy/pocketpy/issues"

LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=224889df377610675315e194ac59276c"

SRC_URI = "git://github.com/pocketpy/pocketpy.git;protocol=https;branch=main;tag=v${PV}"
SRCREV = "a2f16e5f1f5fcc3b3d2cc1bdacfc3a027dfe2d76"


inherit cmake

EXTRA_OECMAKE = "\
    -DPK_ENABLE_OS=ON \
"

CFLAGS += "-fPIC"

do_install() {
    install -d ${D}${libdir}
    install -m 0644 ${B}/libpocketpy.so ${D}${libdir}/
    install -d ${D}${includedir}/pocketpy
    cp -r ${S}/include/* ${D}${includedir}/pocketpy/
}

FILES:${PN} = "${libdir}/libpocketpy.so"
FILES:${PN}-dev = "${includedir}/pocketpy"
FILES:${PN}-dbg += "${libdir}/.debug/libpocketpy.so"
