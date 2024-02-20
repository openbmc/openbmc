SUMMARY = "Base64 Encoding/Decoding Routines"
DESCRIPTION = "base64 encoding/decoding library - runtime library \
libb64 is a library of ANSI C routines for fast encoding/decoding data into \
and from a base64-encoded format"
HOMEPAGE = "https://github.com/libb64"
LICENSE = "PD"
LIC_FILES_CHKSUM = "file://LICENSE.md;md5=81296a564fa0621472714aae7c763d96"

PV .= "+2.0.0.2+git"
SRCREV = "ce864b17ea0e24a91e77c7dd3eb2d1ac4175b3f0"

SRC_URI = "git://github.com/libb64/libb64;protocol=https;branch=master \
           file://0001-example-Do-not-run-the-tests.patch \
           file://0002-use-BUFSIZ-as-buffer-size.patch \
           file://0001-Makefile-fix-parallel-build-of-examples.patch \
           file://0001-examples-Use-proper-function-prototype-for-main.patch \
           "

S = "${WORKDIR}/git"

CFLAGS += "-fPIC"

do_configure () {
    :
}

do_compile () {
    oe_runmake
    ${CC} ${LDFLAGS} ${CFLAGS} -shared -Wl,-soname,${BPN}.so.0 src/*.o -o src/${BPN}.so.0
}

do_install () {
    install -d ${D}${includedir}/b64
    install -Dm 0644 ${B}/src/libb64.a ${D}${libdir}/libb64.a
    install -Dm 0644 ${B}/src/libb64.so.0 ${D}${libdir}/libb64.so.0
    ln -s libb64.so.0 ${D}${libdir}/libb64.so
    install -Dm 0644 ${S}/include/b64/*.h ${D}${includedir}/b64/
}
