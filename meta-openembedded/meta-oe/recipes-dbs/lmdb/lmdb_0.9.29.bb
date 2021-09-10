# SPDX-FileCopyrightText: 2017-2019 Volker Krause <vkrause@kde.org>
# SPDX-FileCopyrightText: 2019 Hannah Kiekens <hannahkiekens@gmail.com>
#
# SPDX-License-Identifier: MIT

SUMMARY = "Lightning Memory-Mapped Database (LMDB)"
HOMEPAGE = "https://symas.com/lightning-memory-mapped-database/"
LICENSE = "OLDAP-2.8"
LIC_FILES_CHKSUM = "file://LICENSE;md5=153d07ef052c4a37a8fac23bc6031972"

SRC_URI = "git://github.com/LMDB/lmdb.git;nobranch=1 \
           file://run-ptest \
           file://0001-Makefile-use-libprefix-instead-of-libdir.patch \
           "

SRCREV = "LMDB_${PV}"

inherit base ptest

S = "${WORKDIR}/git/libraries/liblmdb"

LDFLAGS += "-Wl,-soname,lib${PN}.so.${PV}"

do_compile() {
    oe_runmake CC="${CC}" SOEXT=".so.${PV}" LDFLAGS="${LDFLAGS}"
}

do_install() {
    oe_runmake CC="${CC}" DESTDIR="${D}" prefix="${prefix}" libprefix="${libdir}" manprefix="${mandir}" SOEXT=".so.${PV}" LDFLAGS="${LDFLAGS}" install
    cd ${D}${libdir}
    ln -s liblmdb.so.${PV} liblmdb.so
    rm liblmdb.a
}

do_install_ptest() {
    install -d ${D}${PTEST_PATH}/tests
    for test in mtest mtest2 mtest3 mtest4 mtest5 mdb_stat; do
        install -m 755 ${S}/$test ${D}${PTEST_PATH}/tests
    done
}
