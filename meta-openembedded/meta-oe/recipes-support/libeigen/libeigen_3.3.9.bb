DESCRIPTION = "Eigen is a C++ template library for linear algebra: matrices, vectors, numerical solvers, and related algorithms."
AUTHOR = "Benoît Jacob and Gaël Guennebaud and others"
HOMEPAGE = "http://eigen.tuxfamily.org/"
LICENSE = "MPL-2.0 & Apache-2.0 & BSD-3-Clause & GPLv3 & LGPLv2.1 & MINPACK"
LIC_FILES_CHKSUM = "file://COPYING.MPL2;md5=815ca599c9df247a0c7f619bab123dad \
                    file://COPYING.BSD;md5=543367b8e11f07d353ef894f71b574a0 \
                    file://COPYING.GPL;md5=d32239bcb673463ab874e80d47fae504 \
                    file://COPYING.LGPL;md5=4fbd65380cdd255951079008b364516c \
                    file://COPYING.MINPACK;md5=5fe4603e80ef7390306f51ef74449bbd \
"

SRC_URI = "git://gitlab.com/libeigen/eigen.git;protocol=http;nobranch=1"

SRCREV = "0fd6b4f71dd85b2009ee4d1aeb296e2c11fc9d68"

S = "${WORKDIR}/git"

inherit cmake

FILES:${PN}-dev += "${datadir}/eigen3/cmake"

# ${PN} is empty so we need to tweak -dev and -dbg package dependencies
RDEPENDS:${PN}-dev = ""
RRECOMMENDS:${PN}-dbg = "${PN}-dev (= ${EXTENDPKGV})"

BBCLASSEXTEND = "native nativesdk"
