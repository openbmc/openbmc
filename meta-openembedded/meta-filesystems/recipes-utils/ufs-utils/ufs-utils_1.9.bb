SUMMARY = "Tool to access UFS (Universal Flash Storage) devices"
LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://COPYING;md5=59530bdf33659b29e73d4adb9f9f6552"

BRANCH ?= "dev"

SRCREV = "517c0b01e47d4441cc45be351509fb4c96843d5a"

SRC_URI = "git://github.com/westerndigitalcorporation/ufs-utils.git;protocol=https;branch=${BRANCH} \
           file://0001-Use-asm-type.h-for-kernel-types.patch \
"

UPSTREAM_CHECK_COMMITS = "1"

S = "${WORKDIR}/git"

EXTRA_OEMAKE = "CROSS_COMPILE=${TARGET_PREFIX} CC="${CC}" CFLAGS="${CFLAGS}""

CFLAGS:append:mipsarchn64 = " -D__SANE_USERSPACE_TYPES__ -D_GNU_SOURCE"

do_configure() {
	sed -i -e "s|-static$||g" ${S}/Makefile
}

do_install() {
	install -D -m 755 ${S}/ufs-utils ${D}${bindir}/ufs-utils
}

PROVIDES += "ufs-tool"

RPROVIDES:${PN} += "ufs-tool"

