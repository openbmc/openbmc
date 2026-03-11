# SPDX-FileCopyrightText: 2024 Bosch Sicherheitssysteme GmbH
#
# SPDX-License-Identifier: MIT

SUMMARY = "A tool to manipulate and read memory mapped registers"
DESCRIPTION = "memtool is a program that allows to access memory mapped registers. This is useful \
to inspect and modify registers from the command line. memtool can also operate on plain files, \
and access PHY registers."
HOMEPAGE = "https://github.com/pengutronix/memtool"
BUGTRACKER = "https://github.com/pengutronix/memtool/issues"
SECTION = "devtool"

LICENSE = "GPL-2.0-only"
LIC_FILES_CHKSUM = "file://COPYING;md5=b234ee4d69f5fce4486a80fdaf4a4263"

SRC_URI = " \
    http://www.pengutronix.de/software/memtool/downloads/memtool-${PV}.tar.xz \
    file://run-ptest \
    file://test_read_write_plainfiles.sh \
"

SRC_URI[sha256sum] = "87cb7175266ff3a00a9c1f541c4c6c93693ffbe8dcc0d97a60d13c45ff860900"

inherit autotools ptest

do_install_ptest () {
    install -d ${D}${PTEST_PATH}/tests
    install -m 0755 ${UNPACKDIR}/test_* ${D}${PTEST_PATH}/tests
}

RDEPENDS:${PN}-ptest += "bash coreutils"
