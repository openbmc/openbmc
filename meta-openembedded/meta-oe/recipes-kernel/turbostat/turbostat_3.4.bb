#
# Copyright (C) 2013 Wind River Systems, Inc.
#
# This program is free software; you can redistribute it and/or modify
# it under the terms of the GNU General Public License version 2 as
# published by the Free Software Foundation.
#
SUMMARY = "Frequency and Idle power monitoring tools for Linux"

DESCRIPTION = "The turbostat tool allows you to determine the actual \
processor frequency and idle power saving state residency on supported \
processors."

LICENSE = "GPL-2.0-only"
LIC_FILES_CHKSUM = "file://../COPYING;md5=bbea815ee2795b2f4230826c0c6b8814"
COMPATIBLE_HOST = '(x86_64.*|i.86.*)-linux'
COMPATIBLE_HOST:libc-musl = "null"

SRC_URI += "\
            file://COPYING \
            "

PACKAGE_ARCH = "${MACHINE_ARCH}"
DEPENDS = "libcap"

# This looks in S, so we better make sure there's
# something in the directory.
#
do_populate_lic[depends] = "${PN}:do_configure"


EXTRA_OEMAKE = '\
                CC="${CC}" 'CFLAGS=-Wall ${LDFLAGS}' \
               '

# If we build under STAGING_KERNEL_DIR, source will not be put
# into the dbg rpm.  STAGING_KERNEL_DIR will exist by the time
# do_configure() is invoked so we can safely copy from it.
#
do_configure[depends] += "virtual/kernel:do_shared_workdir"
do_configure:prepend() {
	mkdir -p ${S}
	cp -r ${STAGING_KERNEL_DIR}/arch/x86/include/asm/msr-index.h ${S}
	cp -r ${STAGING_KERNEL_DIR}/arch/x86/include/asm/intel-family.h ${S}
	if [ -f "${STAGING_KERNEL_DIR}/include/vdso/bits.h" ]; then
		cp -r ${STAGING_KERNEL_DIR}/include/vdso/bits.h ${S}
		cp -r ${STAGING_KERNEL_DIR}/include/vdso/const.h ${S}
	else
		cp -r ${STAGING_KERNEL_DIR}/include/linux/bits.h ${S}
		cp -r ${STAGING_KERNEL_DIR}/include/linux/const.h ${S}
	fi
	cp -r ${STAGING_KERNEL_DIR}/tools/power/x86/turbostat/* ${S}
	cp -r ${WORKDIR}/COPYING ${S}
}


do_compile() {
	sed -i 's#<linux/bits.h>#"bits.h"#' msr-index.h
	'TMPCHECK='grep "<vdso/const.h>" bits.h'' || true
	if [ -n $TMPCHECK ]; then
		sed -i 's#<vdso/const.h>#"const.h"#' bits.h
		sed -i 's#<uapi/linux/const.h>#<linux/const.h>#' const.h
	else
		sed -i 's#<linux/const.h>#"const.h"#' bits.h
		sed -i -e 's#<uapi/linux/const.h>#<linux/const.h>#' -e 's#_LINUX_CONST_H#_LINUX_CONST_H_KERNEL#' const.h
	fi
	sed -i 's#MSRHEADER#"msr-index.h"#' turbostat.c
	sed -i 's#INTEL_FAMILY_HEADER#"intel-family.h"#' turbostat.c
	sed -i 's#\$(CC) \$(CFLAGS) \$< -o \$(BUILD_OUTPUT)/\$@#\$(CC) \$(CFLAGS) \$(LDFLAGS) \$< -o \$(BUILD_OUTPUT)/\$@#' Makefile
	oe_runmake STAGING_KERNEL_DIR=${STAGING_KERNEL_DIR}
}

do_install() {
	oe_runmake DESTDIR="${D}" install
}
