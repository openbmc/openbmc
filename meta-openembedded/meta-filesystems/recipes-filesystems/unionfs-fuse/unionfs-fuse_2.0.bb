SUMMARY = "A FUSE based implemention of unionfs"
HOMEPAGE = "https://github.com/rpodgorny/unionfs-fuse"
SECTION = "console/network"
LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://src/unionfs.c;beginline=3;endline=8;md5=30fa8de70fd8abab00b483a1b7943a32 \
                    file://LICENSE;md5=7e5a37fce17307066eec6b23546da3b3 \
"

SRC_URI = "git://github.com/rpodgorny/${BPN}.git;branch=master \
           file://0001-support-cross-compiling.patch \
           file://0001-unionfs-Define-IOCPARM_LEN-if-undefined.patch \
           "
SRCREV = "c8d23a9a75f1a62988593671839e8a168a79f3e5"

DEPENDS = "fuse"

S = "${WORKDIR}/git"

inherit cmake pkgconfig
