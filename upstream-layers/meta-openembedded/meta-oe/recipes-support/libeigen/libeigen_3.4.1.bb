DESCRIPTION = "Eigen is a C++ template library for linear algebra: matrices, vectors, numerical solvers, and related algorithms."
HOMEPAGE = "http://eigen.tuxfamily.org/"
LICENSE = "MPL-2.0 & Apache-2.0 & BSD-3-Clause & GPL-2.0-only & Minpack"
# The GPL code is only used for benchmark tests and does not affect what is installed.
LICENSE:${PN} = "MPL-2.0 & Apache-2.0 & BSD-3-Clause & Minpack"
LICENSE:${PN}-dbg = "MPL-2.0 & Apache-2.0 & BSD-3-Clause & Minpack"
LICENSE:${PN}-dev = "MPL-2.0 & Apache-2.0 & BSD-3-Clause & Minpack"
LIC_FILES_CHKSUM = "file://COPYING.MPL2;md5=815ca599c9df247a0c7f619bab123dad \
                    file://COPYING.APACHE;md5=8de23b8e93c63005353056b2475e9aa5 \
                    file://COPYING.BSD;md5=2dd0510ee95e59ca28834b875bc96596 \
                    file://COPYING.GPL;md5=d32239bcb673463ab874e80d47fae504 \
                    file://COPYING.MINPACK;md5=71d91b0f75ce79a75d3108a72bef8116 \
"

SRC_URI = "git://gitlab.com/libeigen/eigen.git;protocol=http;branch=3.4 \
    file://0001-Remove-LGPL-Code-and-references.patch \
    file://0001-Make-eigen_packet_wrapper-trivial-for-c-11.patch \
"

SRCREV = "d71c30c47858effcbd39967097a2d99ee48db464"

inherit cmake

PACKAGECONFIG ??= ""

PACKAGECONFIG[blas] = "-DEIGEN_BUILD_BLAS=ON,-DEIGEN_BUILD_BLAS=OFF,libgfortran"
PACKAGECONFIG[lapack] = "-DEIGEN_BUILD_LAPACK=ON,-DEIGEN_BUILD_LAPACK=OFF,libgfortran"

EXTRA_OECMAKE += " \
    -DEIGEN_BUILD_TESTING=OFF \
"

FILES:${PN}-dev += "${datadir}/eigen3/cmake"

# ${PN} is empty so we need to tweak -dev and -dbg package dependencies
RDEPENDS:${PN}-dev = ""
RRECOMMENDS:${PN}-dbg = "${PN}-dev (= ${EXTENDPKGV})"

BBCLASSEXTEND = "native nativesdk"
