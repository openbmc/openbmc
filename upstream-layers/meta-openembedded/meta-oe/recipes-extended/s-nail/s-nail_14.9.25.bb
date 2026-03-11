SUMMARY = "Feature-rich BSD mail(1)"
HOMEPAGE = "https://www.sdaoden.eu/code.html#s-mailx"
SECTION = "console/network"

LICENSE = "ISC & BSD-3-Clause & BSD-4-Clause"
LIC_FILES_CHKSUM = "file://COPYING;md5=87266591c81260f10221f1f38872d023"

SRC_URI = "https://ftp.sdaoden.eu/${BP}.tar.xz \
           file://make-errors.patch \
           file://0001-make.rc-set-VAL_MTA.patch \
           file://0001-mk-make-config.sh-not-reveal-the-build-env.patch \
"
SRC_URI[sha256sum] = "20ff055be9829b69d46ebc400dfe516a40d287d7ce810c74355d6bdc1a28d8a9"

DEPENDS = "coreutils-native"

B = "${WORKDIR}/build"

inherit update-alternatives

EXTRA_OEMAKE = "VERBOSE=yes \
                CONFIG=minimal \
                OPT_AUTOCC=no \
                OPT_CROSS_BUILD=yes \
                OBJDIR=${B} \
                strip=true \
                VAL_PREFIX=${prefix} \
                VAL_BINDIR=${bindir} \
                VAL_LIBEXECDIR=${libexecdir} \
                VAL_MANDIR=${mandir} \
                VAL_SYSCONFDIR=${sysconfdir}"

do_configure[cleandirs] += "${B}"
do_configure() {
    oe_runmake -C ${S} config
}

do_compile() {
    oe_runmake -C ${S} build
}

do_install() {
    oe_runmake -C ${S} install DESTDIR=${D}
}

ALTERNATIVE:${PN} = "mailx"
ALTERNATIVE_TARGET[mailx] = "${bindir}/s-nail"
