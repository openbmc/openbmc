DESCRIPTION = "Eigen is a C++ template library for linear algebra: matrices, vectors, numerical solvers, and related algorithms."
AUTHOR = "Benoît Jacob and Gaël Guennebaud and others"
HOMEPAGE = "http://eigen.tuxfamily.org/"
LICENSE = "MPL-2.0"
LIC_FILES_CHKSUM = "file://COPYING.MPL2;md5=815ca599c9df247a0c7f619bab123dad"

SRC_URI = "http://bitbucket.org/eigen/eigen/get/${PV}.tar.bz2 \
           file://0001-CMakeLists.txt-install-FindEigen3.cmake-script.patch"
SRC_URI[md5sum] = "87274966745d2d3e7964fcc654d0a24b"
SRC_URI[sha256sum] = "8a3352f9a5361fe90e451a7305fb1896fc7f771dc16cc0edd8e6b157f52c343e"

S = "${WORKDIR}/eigen-eigen-c58038c56923"

inherit cmake

EXTRA_OECMAKE += "-Dpkg_config_libdir=${libdir}"

FILES_${PN} = "${includedir} ${libdir}"
FILES_${PN}-dev = "${datadir}/cmake/Modules"

# ${PN} is empty so we need to tweak -dev and -dbg package dependencies
RDEPENDS_${PN}-dev = ""
RRECOMMENDS_${PN}-dbg = "${PN}-dev (= ${EXTENDPKGV})"
