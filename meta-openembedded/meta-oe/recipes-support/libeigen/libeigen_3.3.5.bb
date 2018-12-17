DESCRIPTION = "Eigen is a C++ template library for linear algebra: matrices, vectors, numerical solvers, and related algorithms."
AUTHOR = "Benoît Jacob and Gaël Guennebaud and others"
HOMEPAGE = "http://eigen.tuxfamily.org/"
LICENSE = "MPL-2.0"
LIC_FILES_CHKSUM = "file://COPYING.MPL2;md5=815ca599c9df247a0c7f619bab123dad"

SRC_URI = "http://bitbucket.org/eigen/eigen/get/${PV}.tar.bz2;downloadfilename=${BP}.tar.bz2"
SRC_URI[md5sum] = "e83549a79d1b721da0f8899ab34edf95"
SRC_URI[sha256sum] = "7352bff3ea299e4c7d7fbe31c504f8eb9149d7e685dec5a12fbaa26379f603e2"

S = "${WORKDIR}/eigen-eigen-b3f3d4950030"

inherit cmake

FILES_${PN} = "${libdir}"
FILES_${PN}-dev = "${includedir} ${datadir}/eigen3/cmake ${datadir}/cmake/Modules ${datadir}/pkgconfig"

# ${PN} is empty so we need to tweak -dev and -dbg package dependencies
RDEPENDS_${PN}-dev = ""
RRECOMMENDS_${PN}-dbg = "${PN}-dev (= ${EXTENDPKGV})"
