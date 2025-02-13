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
    file://0001-Default-eigen_packet_wrapper-constructor.patch \
    file://0002-Remove-LGPL-Code-and-references.patch \
"

SRCREV = "3147391d946bb4b6c68edd901f2add6ac1f31f8c"

S = "${WORKDIR}/git"

inherit cmake

FILES:${PN}-dev += "${datadir}/eigen3/cmake"

# ${PN} is empty so we need to tweak -dev and -dbg package dependencies
RDEPENDS:${PN}-dev = ""
RRECOMMENDS:${PN}-dbg = "${PN}-dev (= ${EXTENDPKGV})"

BBCLASSEXTEND = "native nativesdk"
