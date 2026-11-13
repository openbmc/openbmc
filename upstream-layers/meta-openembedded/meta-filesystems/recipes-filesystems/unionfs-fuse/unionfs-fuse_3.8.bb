SUMMARY = "A FUSE based implemention of unionfs"
HOMEPAGE = "https://github.com/rpodgorny/unionfs-fuse"
SECTION = "console/network"
LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://src/unionfs.c;beginline=3;endline=8;md5=30fa8de70fd8abab00b483a1b7943a32 \
                    file://LICENSE;md5=22a665a5eef3c10268acdee50e62d876 \
"

SRC_URI = "git://github.com/rpodgorny/${BPN}.git;branch=master;protocol=https;tag=v${PV} \
           file://run-ptest \
           file://0001-adapt-tests-to-ptest.patch \
"

SRCREV = "e3e8b289f19047d7c6309eff3dc7a2c505229b73"

DEPENDS = "fuse3"
RDEPENDS:${PN} = "bash"
RDEPENDS:${PN}-ptest += "python3-core python3-unittest python3-unittest-automake-output"

inherit cmake pkgconfig ptest

do_install_ptest(){
    install ${S}/test_all.py ${D}${PTEST_PATH}
}
