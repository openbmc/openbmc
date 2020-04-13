SUMMARY = "Tool to access UFS (Universal Flash Storage) devices"
LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://COPYING;md5=59530bdf33659b29e73d4adb9f9f6552"

PV = "1.6+git${SRCPV}"

BRANCH ?= "dev"

SRCREV = "a3cf93b66f4606a46354cf884d24aa966661f848"

SRC_URI = "git://github.com/westerndigitalcorporation/ufs-utils.git;protocol=git;branch=${BRANCH} \
           file://0001-Replace-u_intXX_t-with-kernel-typedefs.patch \
"

UPSTREAM_CHECK_COMMITS = "1"

S = "${WORKDIR}/git"

EXTRA_OEMAKE = "CROSS_COMPILE=${TARGET_PREFIX} CC="${CC}" CFLAGS="${CFLAGS}""

CFLAGS_append_mipsarchn64 = " -D__SANE_USERSPACE_TYPES__ -D_GNU_SOURCE"

do_configure() {
	sed -i -e "s|-static$||g" ${S}/Makefile
}

do_install() {
	install -D -m 755 ${S}/ufs-utils ${D}${bindir}/ufs-utils
}

PROVIDES += "ufs-tool"

RPROVIDES_${PN} += "ufs-tool"

