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

LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://../COPYING;md5=bbea815ee2795b2f4230826c0c6b8814"
COMPATIBLE_HOST = '(x86_64.*|i.86.*)-linux'
COMPATIBLE_HOST_libc-musl = "null"

SRC_URI += "\
            file://COPYING \
            "

DEPENDS = "virtual/kernel"

# This looks in S, so we better make sure there's
# something in the directory.
#
do_populate_lic[depends] = "${PN}:do_configure"


EXTRA_OEMAKE = '\
                CC="${CC}" \
		'CFLAGS=-Wall -I${STAGING_KERNEL_DIR}/arch/x86/include/uapi ${LDFLAGS}' \
               '

# If we build under STAGING_KERNEL_DIR, source will not be put
# into the dbg rpm.  STAGING_KERNEL_DIR will exist by the time
# do_configure() is invoked so we can safely copy from it.
#
do_configure_prepend() {
	mkdir -p ${S}
	cp -r ${STAGING_KERNEL_DIR}/arch/x86/include/asm/msr-index.h ${S}
	cp -r ${STAGING_KERNEL_DIR}/arch/x86/include/asm/intel-family.h ${S}
	cp -r ${STAGING_KERNEL_DIR}/tools/power/x86/turbostat/* ${S}
	cp -r ${WORKDIR}/COPYING ${S}
}

do_compile() {
	sed -i 's#MSRHEADER#"msr-index.h"#' turbostat.c
	sed -i 's#INTEL_FAMILY_HEADER#"intel-family.h"#' turbostat.c
	sed -i 's#\$(CC) \$(CFLAGS) \$< -o \$(BUILD_OUTPUT)/\$@#\$(CC) \$(CFLAGS) \$(LDFLAGS) \$< -o \$(BUILD_OUTPUT)/\$@#' Makefile
	oe_runmake STAGING_KERNEL_DIR=${STAGING_KERNEL_DIR}
}

do_install() {
	oe_runmake DESTDIR="${D}" install
}
