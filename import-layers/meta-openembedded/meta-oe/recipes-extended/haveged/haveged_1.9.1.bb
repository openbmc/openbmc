SUMMARY = "haveged - A simple entropy daemon"
DESCRIPTION = "The haveged project is an attempt to provide an easy-to-use, unpredictable random number generator based upon an adaptation of the HAVEGE algorithm. Haveged was created to remedy low-entropy conditions in the Linux random device that can occur under some workloads, especially on headless servers."
AUTHOR = "Gary Wuertz"
HOMEPAGE = "http://www.issihosts.com/haveged/index.html"
LICENSE = "GPLv3"
LIC_FILES_CHKSUM="file://COPYING;md5=d32239bcb673463ab874e80d47fae504"

SRC_URI = "http://www.issihosts.com/haveged/haveged-${PV}.tar.gz \
    file://remove-systemd-unit-503.patch \
"

SRC_URI[md5sum] = "015ff58cd10607db0e0de60aeca2f5f8"
SRC_URI[sha256sum] = "9c2363ed9542a6784ff08e247182137e71f2ddb79e8e6c1ac4ad50d21ced3715"

inherit autotools systemd

EXTRA_OECONF = "\
    --enable-init=service.redhat \
    --enable-nistest=yes \
    --enable-olt=yes \
    --enable-threads=no \
"

SYSTEMD_PACKAGES = "${PN}"
SYSTEMD_SERVICE_${PN} = "haveged.service"

do_install_append() {
    mkdir -p ${D}${systemd_unitdir}/system
    install -p -m644 ${B}/init.d/haveged.service ${D}${systemd_unitdir}/system
}

MIPS_INSTRUCTION_SET = "mips"
