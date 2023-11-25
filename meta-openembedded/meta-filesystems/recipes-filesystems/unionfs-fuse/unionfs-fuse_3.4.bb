SUMMARY = "A FUSE based implemention of unionfs"
HOMEPAGE = "https://github.com/rpodgorny/unionfs-fuse"
SECTION = "console/network"
LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://src/unionfs.c;beginline=3;endline=8;md5=30fa8de70fd8abab00b483a1b7943a32 \
                    file://LICENSE;md5=7e5a37fce17307066eec6b23546da3b3 \
"

SRC_URI = "git://github.com/rpodgorny/${BPN}.git;branch=master;protocol=https \
           file://0001-support-cross-compiling.patch \
           "
SRCREV = "773f1853b043eeb64b7459f903a2c65bd096f9d9"

DEPENDS = "fuse3"
RDEPENDS:${PN} = "bash"

S = "${WORKDIR}/git"

inherit cmake pkgconfig
