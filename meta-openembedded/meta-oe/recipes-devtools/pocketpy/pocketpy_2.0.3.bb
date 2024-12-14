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
LIC_FILES_CHKSUM = "file://LICENSE;md5=8cdfa87bc5e09bc07f8cf64135026d91"

SRC_URI = "git://github.com/pocketpy/pocketpy.git;protocol=https;branch=main"
SRCREV = "3be557f95dbc706716b9a9947e41c1f2db92e251"

S = "${WORKDIR}/git"

inherit cmake

do_install() {
    install -d ${D}${libdir}
    install -m 0644 ${B}/libpocketpy.so ${D}${libdir}/
    install -d ${D}${includedir}/pocketpy
    cp -r ${S}/include/* ${D}${includedir}/pocketpy/
}

FILES:${PN} = "${libdir}/libpocketpy.so"
FILES:${PN}-dev = "${includedir}/pocketpy"
FILES:${PN}-dbg += "${libdir}/.debug/libpocketpy.so"
