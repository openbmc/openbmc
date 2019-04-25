SUMMARY = "klibc, a small C library for use with initramfs"

do_install() {
    oe_runmake install
    # the crosscompiler is packaged by klcc-cross
    # remove klcc
    rm ${D}${bindir}/klcc
    # remove now empty dir
    rmdir ${D}${bindir}
    install -d ${D}${libdir}
    install -m 755 usr/klibc/klibc-*.so ${D}${libdir}
    (cd  ${D}${libdir}; ln -s klibc-*.so klibc.so)
    rm -rf ${D}${exec_prefix}/man
    rm -rf ${D}${libdir}/klibc/bin
}

PACKAGES = "libklibc libklibc-staticdev libklibc-dev"

FILES_libklibc = "${libdir}/klibc-*.so"
FILES_libklibc-staticdev = "${libdir}/klibc/lib/libc.a"
FILES_libklibc-dev = "${libdir}/klibc.so \
                      ${libdir}/klibc/lib/* \
                      ${libdir}/klibc/include/* \
                      "

require klibc.inc
