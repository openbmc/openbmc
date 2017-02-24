DESCRIPTION = "Eigen is a C++ template library for linear algebra: matrices, vectors, numerical solvers, and related algorithms."
AUTHOR = "Benoît Jacob and Gaël Guennebaud and others"
HOMEPAGE = "http://eigen.tuxfamily.org/"
LICENSE = "MPL-2.0"
LIC_FILES_CHKSUM = "file://COPYING.MPL2;md5=815ca599c9df247a0c7f619bab123dad"

SRC_URI = "http://bitbucket.org/eigen/eigen/get/${PV}.tar.bz2;downloadfilename=${BP}.tar.bz2 \
           file://0001-CMakeLists.txt-install-FindEigen3.cmake-script.patch"
SRC_URI[md5sum] = "9e3bfaaab3db18253cfd87ea697b3ab1"
SRC_URI[sha256sum] = "722a63d672b70f39c271c5e2a4a43ba14d12015674331790414fcb167c357e55"

S = "${WORKDIR}/eigen-eigen-07105f7124f9"

inherit cmake

FILES_${PN} = "${includedir} ${libdir}"
FILES_${PN}-dev = "${datadir}/cmake/Modules ${datadir}/pkgconfig"

# ${PN} is empty so we need to tweak -dev and -dbg package dependencies
RDEPENDS_${PN}-dev = ""
RRECOMMENDS_${PN}-dbg = "${PN}-dev (= ${EXTENDPKGV})"
