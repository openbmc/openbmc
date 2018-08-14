DESCRIPTION = "Precision Time Protocol (PTP) according to IEEE standard 1588 for Linux"
LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://COPYING;md5=b234ee4d69f5fce4486a80fdaf4a4263"

SRC_URI = "http://sourceforge.net/projects/linuxptp/files/v${PV}/linuxptp-${PV}.tgz \
           file://build-Allow-CC-and-prefix-to-be-overriden.patch \
           file://no-incdefs-using-host-headers.patch \
           file://0003-include-missing-time.h-for-time_t.patch \
           file://0004-Adjust-include-header-sequence-to-avoid-duplicate-de.patch \
           "

SRC_URI[md5sum] = "5688cdfe57932273e1dbf35b3b97b9a0"
SRC_URI[sha256sum] = "fa8e00f6ec73cefa7bb313dce7f60dfe5eb9e2bde3353594e9ac18edc93e5165"

EXTRA_OEMAKE = "ARCH=${TARGET_ARCH} \
    EXTRA_CFLAGS='-D_GNU_SOURCE -DHAVE_CLOCK_ADJTIME -DHAVE_POSIX_SPAWN -DHAVE_ONESTEP_SYNC ${CFLAGS}'"

do_install () {
    install -d ${D}/${bindir}
    install -p ${S}/ptp4l  ${D}/${bindir}
    install -p ${S}/pmc  ${D}/${bindir}
    install -p ${S}/phc2sys  ${D}/${bindir}
    install -p ${S}/hwstamp_ctl  ${D}/${bindir}
}
