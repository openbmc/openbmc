SUMMARY = "Tool to access UFS (Universal Flash Storage) devices"
LICENSE = "GPL-2.0-only"
LIC_FILES_CHKSUM = "file://COPYING;md5=59530bdf33659b29e73d4adb9f9f6552"

BRANCH ?= "dev"

SRCREV = "dd2e655780424eadf7610de33e5966be19168a95"

SRC_URI = "git://github.com/westerndigitalcorporation/ufs-utils.git;protocol=https;branch=${BRANCH} \
          "

UPSTREAM_CHECK_COMMITS = "1"


EXTRA_OEMAKE = "CROSS_COMPILE=${TARGET_PREFIX} CC="${CC}" CFLAGS="${CFLAGS}""

CFLAGS:append = " -D_GNU_SOURCE"
CFLAGS:append:mipsarchn64 = " -D__SANE_USERSPACE_TYPES__"

do_configure() {
	sed -i -e "s|-static$||g" ${S}/Makefile
}

do_install() {
	install -D -m 755 ${S}/ufs-utils ${D}${bindir}/ufs-utils
}

PROVIDES += "ufs-tool"

RPROVIDES:${PN} += "ufs-tool"

