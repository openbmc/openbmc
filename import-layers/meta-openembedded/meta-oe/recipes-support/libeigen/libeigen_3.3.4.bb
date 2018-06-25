DESCRIPTION = "Eigen is a C++ template library for linear algebra: matrices, vectors, numerical solvers, and related algorithms."
AUTHOR = "Benoît Jacob and Gaël Guennebaud and others"
HOMEPAGE = "http://eigen.tuxfamily.org/"
LICENSE = "MPL-2.0"
LIC_FILES_CHKSUM = "file://COPYING.MPL2;md5=815ca599c9df247a0c7f619bab123dad"

SRC_URI = "http://bitbucket.org/eigen/eigen/get/${PV}.tar.bz2;downloadfilename=${BP}.tar.bz2"
SRC_URI[md5sum] = "a7aab9f758249b86c93221ad417fbe18"
SRC_URI[sha256sum] = "dd254beb0bafc695d0f62ae1a222ff85b52dbaa3a16f76e781dce22d0d20a4a6"

S = "${WORKDIR}/eigen-eigen-5a0156e40feb"

inherit cmake

FILES_${PN} = "${includedir} ${libdir}"
FILES_${PN}-dev = "${datadir}/eigen3/cmake ${datadir}/cmake/Modules ${datadir}/pkgconfig"

# ${PN} is empty so we need to tweak -dev and -dbg package dependencies
RDEPENDS_${PN}-dev = ""
RRECOMMENDS_${PN}-dbg = "${PN}-dev (= ${EXTENDPKGV})"
