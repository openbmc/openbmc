SUMMARY = "Base64 Encoding/Decoding Routines"
DESCRIPTION = "base64 encoding/decoding library - runtime library \
libb64 is a library of ANSI C routines for fast encoding/decoding data into \
and from a base64-encoded format"
HOMEPAGE = "http://libb64.sourceforge.net/"
LICENSE = "PD"
LIC_FILES_CHKSUM = "file://LICENSE;md5=ce551aad762074c7ab618a0e07a8dca3"

SRC_URI = "${SOURCEFORGE_MIRROR}/${BPN}/${BPN}/${BP}.zip \
           file://0001-example-Do-not-run-the-tests.patch \
           file://0002-use-BUFSIZ-as-buffer-size.patch \
           file://0003-fix-integer-overflows.patch \
           file://0004-Fix-off-by-one-error.patch \
           file://0005-make-overriding-CFLAGS-possible.patch \
           file://0006-do-not-export-the-CHARS_PER_LINE-variable.patch \
           file://0007-initialize-encoder-decoder-state-in-the-constructors.patch \
           "
SRC_URI[sha256sum] = "20106f0ba95cfd9c35a13c71206643e3fb3e46512df3e2efb2fdbf87116314b2"

PARALLEL_MAKE = ""

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
