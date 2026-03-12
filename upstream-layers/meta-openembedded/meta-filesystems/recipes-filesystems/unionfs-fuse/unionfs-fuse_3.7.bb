SUMMARY = "A FUSE based implemention of unionfs"
HOMEPAGE = "https://github.com/rpodgorny/unionfs-fuse"
SECTION = "console/network"
LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://src/unionfs.c;beginline=3;endline=8;md5=30fa8de70fd8abab00b483a1b7943a32 \
                    file://LICENSE;md5=0e75c95b3e0e1c01489b39e7fadd3e2d \
"

SRC_URI = "git://github.com/rpodgorny/${BPN}.git;branch=master;protocol=https;tag=v${PV} \
           file://run-ptest \
           file://0001-fix-debug-ioctl-call.patch \
           file://0001-adapt-tests-to-ptest.patch \
"

SRCREV = "3fcbd11f78b9a9e02ea0e861d741840fe45dc9c8"

DEPENDS = "fuse3"
RDEPENDS:${PN} = "bash"
RDEPENDS:${PN}-ptest += "python3-core python3-unittest python3-unittest-automake-output"

inherit cmake pkgconfig ptest

do_install_ptest(){
    install ${S}/test_all.py ${D}${PTEST_PATH}
}
