SUMMARY = "Tool to access UFS (Universal Flash Storage) devices"
LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://COPYING;md5=59530bdf33659b29e73d4adb9f9f6552"

PV = "1.3+git${SRCPV}"

BRANCH ?= "dev"

SRCREV = "8b00e33003b57d7071943f63863fcf8cc48d5be9"

SRC_URI = "git://github.com/westerndigitalcorporation/ufs-tool.git;protocol=git;branch=${BRANCH}"

S = "${WORKDIR}/git"

EXTRA_OEMAKE = "CROSS_COMPILE=${TARGET_PREFIX} CC="${CC}" CFLAGS="${CFLAGS}""

do_configure() {
	sed -i -e "s|-static$||g" ${S}/Makefile
}
do_install() {
	install -d ${D}${bindir}/
	install -c -m 755 ${S}/ufs-tool ${D}${bindir}/
}
