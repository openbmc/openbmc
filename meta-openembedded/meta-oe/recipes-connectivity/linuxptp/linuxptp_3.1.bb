DESCRIPTION = "Precision Time Protocol (PTP) according to IEEE standard 1588 for Linux"
HOMEPAGE = "http://linuxptp.sourceforge.net/"
LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://COPYING;md5=b234ee4d69f5fce4486a80fdaf4a4263"

SRC_URI = "http://sourceforge.net/projects/linuxptp/files/v${PV}/linuxptp-${PV}.tgz \
           file://build-Allow-CC-and-prefix-to-be-overriden.patch \
           file://Use-cross-cpp-in-incdefs.patch \
           "

UPSTREAM_CHECK_URI = "https://sourceforge.net/projects/linuxptp/files/"
UPSTREAM_CHECK_REGEX = "(?P<pver>\d+(\.\d+)+)/"

SRC_URI[md5sum] = "2264cb69c9af947028835c12c89a7572"
SRC_URI[sha256sum] = "f58f5b11cf14dc7c4f7c9efdfb27190e43d02cf20c3525f6639edac10528ce7d"

EXTRA_OEMAKE = "ARCH=${TARGET_ARCH} EXTRA_CFLAGS='${CFLAGS}' mandir=${mandir}"

export KBUILD_OUTPUT="${RECIPE_SYSROOT}"

do_install() {
    oe_runmake install DESTDIR=${D} prefix=${prefix}

    # Install example configs from source tree
    install -d ${D}${docdir}/${PN}
    cp -R --no-dereference --preserve=mode,links ${S}/configs ${D}${docdir}/${PN}
}

PACKAGES =+ "${PN}-configs"

FILES:${PN}-configs = "${docdir}"
FILES:${PN}-doc = "${mandir}"
