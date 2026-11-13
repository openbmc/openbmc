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

SRC_URI = "git://github.com/pocketpy/pocketpy.git;protocol=https;branch=main;tag=v${PV} \
           file://0001-time-include-threads.h-for-c11_thrd__yield.patch \
           "
SRCREV = "6cbece9ebc98c19fa2c189dc6963b421c22c40ae"


inherit cmake

EXTRA_OECMAKE = "\
    -DPK_ENABLE_OS=ON \
    -DPK_BUILD_WITH_UNITY=OFF \
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
