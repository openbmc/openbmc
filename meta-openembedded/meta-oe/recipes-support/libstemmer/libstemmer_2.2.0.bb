SUMMARY = "Snowball compiler and stemming algorithms"
HOMEPAGE = "https://snowballstem.org/"
LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://COPYING;md5=19139aaf3c8c8fa1ca6edd59c072fb9f"

DEPENDS:append:class-target = " ${BPN}-native"

SRC_URI = "git://github.com/snowballstem/snowball.git;branch=master;protocol=https \
           file://0001-Build-so-lib.patch \
           "
SRCREV = "48a67a2831005f49c48ec29a5837640e23e54e6b"
S = "${WORKDIR}/git"

LIBVER = "0.0.0"

inherit lib_package

do_compile:prepend:class-target() {
    # use native tools
    sed -i 's:./snowball :snowball :g' ${S}/GNUmakefile
}

do_install() {
    install -d ${D}${bindir}
    install -m 755 ${S}/snowball ${D}${bindir}
    install -m 755 ${S}/stemwords ${D}${bindir}

    install -d ${D}${libdir}
    install -m 755 ${S}/libstemmer.so.${LIBVER} ${D}${libdir}/
    ln -s libstemmer.so.${LIBVER} ${D}${libdir}/libstemmer.so.0
    ln -s libstemmer.so.${LIBVER} ${D}${libdir}/libstemmer.so

    install -d ${D}${includedir}
    install -m 644 ${S}/include/*.h ${D}${includedir}
}

BBCLASSEXTEND = "native"
