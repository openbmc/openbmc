SUMMARY = "Snowball compiler and stemming algorithms"
HOMEPAGE = "https://snowballstem.org/"
LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://COPYING;md5=19139aaf3c8c8fa1ca6edd59c072fb9f"

DEPENDS_class-target = "${BPN}-native"

SRC_URI = "\
    git://github.com/snowballstem/snowball.git \
    file://0001-Build-so-lib.patch \
"
SRCREV = "4764395431c8f2a0b4fe18b816ab1fc966a45837"
S = "${WORKDIR}/git"
PV = "2.1.0"
LIBVER = "0.0.0"

inherit lib_package

BBCLASSEXTEND = "native"

do_compile_prepend_class-target() {
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
