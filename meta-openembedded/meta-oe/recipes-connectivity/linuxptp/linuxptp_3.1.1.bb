DESCRIPTION = "Precision Time Protocol (PTP) according to IEEE standard 1588 for Linux"
LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://COPYING;md5=b234ee4d69f5fce4486a80fdaf4a4263"

SRC_URI = "http://sourceforge.net/projects/linuxptp/files/v3.1/linuxptp-${PV}.tgz \
           file://build-Allow-CC-and-prefix-to-be-overriden.patch \
           file://Use-cross-cpp-in-incdefs.patch \
           "

SRC_URI[md5sum] = "3b79ab5e77c5b5cf06bc1c8350d405bb"
SRC_URI[sha256sum] = "94d6855f9b7f2d8e9b0ca6d384e3fae6226ce6fc012dbad02608bdef3be1c0d9"

EXTRA_OEMAKE = "ARCH=${TARGET_ARCH} EXTRA_CFLAGS='${CFLAGS}'"

export KBUILD_OUTPUT="${RECIPE_SYSROOT}"

do_install () {
    install -d ${D}/${bindir}
    install -p ${S}/ptp4l  ${D}/${bindir}
    install -p ${S}/pmc  ${D}/${bindir}
    install -p ${S}/phc2sys  ${D}/${bindir}
    install -p ${S}/hwstamp_ctl  ${D}/${bindir}
}
