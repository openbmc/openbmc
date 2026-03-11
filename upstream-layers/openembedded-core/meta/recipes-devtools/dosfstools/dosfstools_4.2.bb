# dosfstools OE build file
# Copyright (C) 2004-2006, Advanced Micro Devices, Inc.  All Rights Reserved
# Copyright (C) 2015, SÃ¶ren Brinkmann <soeren.brinkmann@gmail>  All Rights Reserved
# Released under the MIT license (see packages/COPYING)
SUMMARY = "DOS FAT Filesystem Utilities"
HOMEPAGE = "https://github.com/dosfstools/dosfstools"

SECTION = "base"
LICENSE = "GPL-3.0-only"
LIC_FILES_CHKSUM = "file://COPYING;md5=d32239bcb673463ab874e80d47fae504"

SRC_URI = "${GITHUB_BASE_URI}/download/v${PV}/${BP}.tar.gz \
           file://run-ptest \
           file://source-date-epoch.patch \
           file://0001-fsck.fat-Adhere-to-the-fsck-exit-codes.patch \
           file://0002-manpages-Document-fsck.fat-new-exit-codes.patch \
           "
SRC_URI[sha256sum] = "64926eebf90092dca21b14259a5301b7b98e7b1943e8a201c7d726084809b527"

inherit autotools gettext pkgconfig ptest update-alternatives github-releases

EXTRA_OECONF = "--enable-compat-symlinks --without-iconv"

CFLAGS += "-D_GNU_SOURCE -D_LARGEFILE_SOURCE -D_FILE_OFFSET_BITS=64"

do_install_ptest () {
    install -d ${D}${PTEST_PATH}/tests
    install -m 0744 ${S}/tests/* ${D}${PTEST_PATH}/tests/
    install -m 0744 ${S}/test-driver ${D}${PTEST_PATH}/tests/

    install -d ${D}${PTEST_PATH}/src
    ln -sf ${sbindir}/mkfs.fat ${D}${PTEST_PATH}/src/mkfs.fat
    ln -sf ${sbindir}/fsck.fat ${D}${PTEST_PATH}/src/fsck.fat
    ln -sf ${sbindir}/fatlabel ${D}${PTEST_PATH}/src/fatlabel

    # dosfstools tests depends on variables defined in the tests/Makefile.
    # To run the tests, we need to modify the following:
    # - srcdir: used to find the test scripts for each dosfstools tools
    # - top_srcdir: used to find the test-driver script
    # - Makefile: originally used to recreate build rules when needed. They're not needed here
    #             because we only want to run the tests.
    # - XXD_FOUND: Always satisfied by RDEPENDS of ptest package
    sed \
        -e 's/^srcdir = ..*/srcdir = \./'         \
        -e 's/^top_srcdir = ..*/top_srcdir = \./' \
        -e 's/^Makefile: ..*/Makefile: /'         \
        -e 's/XXD_FOUND=/XXD_FOUND=yes/'          \
        ${B}/tests/Makefile > ${D}${PTEST_PATH}/tests/Makefile
}

RDEPENDS:${PN}-ptest += "bash diffutils gawk make xxd"

BBCLASSEXTEND = "native nativesdk"

ALTERNATIVE_PRIORITY = "100"
ALTERNATIVE:${PN} = "mkfs.vfat"
ALTERNATIVE_LINK_NAME[mkfs.vfat] = "${sbindir}/mkfs.vfat"
