DESCRIPTION = "Precision Time Protocol (PTP) according to IEEE standard 1588 for Linux"
LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://COPYING;md5=b234ee4d69f5fce4486a80fdaf4a4263"

SRC_URI = "http://sourceforge.net/projects/linuxptp/files/v${PV}/linuxptp-${PV}.tgz \
           file://build-Allow-CC-and-prefix-to-be-overriden.patch \
           file://Use-cross-cpp-in-incdefs.patch \
           "

SRC_URI[md5sum] = "2264cb69c9af947028835c12c89a7572"
SRC_URI[sha256sum] = "f58f5b11cf14dc7c4f7c9efdfb27190e43d02cf20c3525f6639edac10528ce7d"

EXTRA_OEMAKE = "ARCH=${TARGET_ARCH} EXTRA_CFLAGS='${CFLAGS}'"

export KBUILD_OUTPUT="${RECIPE_SYSROOT}"

do_install () {
    install -d ${D}/${bindir}
    install -p ${S}/ptp4l  ${D}/${bindir}
    install -p ${S}/pmc  ${D}/${bindir}
    install -p ${S}/phc2sys  ${D}/${bindir}
    install -p ${S}/hwstamp_ctl  ${D}/${bindir}
}
