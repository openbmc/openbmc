SUMMARY = "Snowball compiler and stemming algorithms"
HOMEPAGE = "https://snowballstem.org/"
LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://COPYING;md5=07c3b61d971c3df6e19ce439cfe1fb8c"

DEPENDS:append:class-target = " ${BPN}-native"

SRC_URI = "git://github.com/snowballstem/snowball.git;branch=main;protocol=https;tag=v${PV} \
           file://0001-Build-so-lib.patch \
           "
SRCREV = "cd195b51e948a902a4312f023f4a14392516a543"

LIBVER = "0.0.0"

# GNUmakefile hardcodes its own CFLAGS= assignment (patched to add -fPIC for
# the shared lib build), which as a plain makefile variable overrides the
# CFLAGS bitbake exports in the environment -- including the
# -ffile-prefix-map flags from DEBUG_PREFIX_MAP. Without those, -g debug
# info embeds the absolute build-tree path, tripping the buildpaths QA
# check. Pass CFLAGS on the make command line instead, which does take
# precedence over the makefile's own assignment.
EXTRA_OEMAKE += "'CFLAGS=${CFLAGS} -fPIC'"

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
