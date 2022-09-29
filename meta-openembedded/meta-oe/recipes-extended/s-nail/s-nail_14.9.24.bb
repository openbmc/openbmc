SUMMARY = "Feature-rich BSD mail(1)"
HOMEPAGE = "https://www.sdaoden.eu/code.html#s-mailx"
SECTION = "console/network"

LICENSE = "ISC & BSD-3-Clause & BSD-4-Clause"
LIC_FILES_CHKSUM = "file://COPYING;md5=87266591c81260f10221f1f38872d023"

SRC_URI = "https://ftp.sdaoden.eu/${BP}.tar.xz \
           file://make-errors.patch \
           file://0001-make.rc-set-VAL_MTA.patch \
           file://0001-su_INLINE-gcc-only-GNU-specifics-after-Og.patch \
           file://0001-su_INLINE-eh-no-give-up-share-detection.patch \
           file://0001-mk-make-config.sh-not-reveal-the-build-env.patch \
"
SRC_URI[sha256sum] = "2714d6b8fb2af3b363fc7c79b76d058753716345d1b6ebcd8870ecd0e4f7ef8c"

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
