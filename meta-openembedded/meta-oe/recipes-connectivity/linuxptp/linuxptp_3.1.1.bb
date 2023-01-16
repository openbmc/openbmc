DESCRIPTION = "Precision Time Protocol (PTP) according to IEEE standard 1588 for Linux"
HOMEPAGE = "http://linuxptp.sourceforge.net/"
LICENSE = "GPL-2.0-only"
LIC_FILES_CHKSUM = "file://COPYING;md5=b234ee4d69f5fce4486a80fdaf4a4263"

SRC_URI = "http://sourceforge.net/projects/linuxptp/files/v3.1/linuxptp-${PV}.tgz \
           file://build-Allow-CC-and-prefix-to-be-overriden.patch \
           file://Use-cross-cpp-in-incdefs.patch \
           file://0001-include-string.h-for-strncpy.patch \
           file://0001-makefile-use-conditional-assignment-for-KBUILD_OUTPU.patch \
           "

UPSTREAM_CHECK_URI = "https://sourceforge.net/projects/linuxptp/files/"
UPSTREAM_CHECK_REGEX = "(?P<pver>\d+(\.\d+)+)/"

SRC_URI[sha256sum] = "94d6855f9b7f2d8e9b0ca6d384e3fae6226ce6fc012dbad02608bdef3be1c0d9"

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
