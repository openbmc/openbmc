DESCRIPTION = "Ceres Solver is an open source C++ library for modeling and solving large, complicated optimization problems."
AUTHOR = "Sameer Agarwal and Keir Mierle and Others"
HOMEPAGE = "http://ceres-solver.org/"
LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://LICENSE;md5=35e00f0c4c96a0820a03e0b31e6416be"

DEPENDS = "libeigen glog"

SRC_URI = "git://github.com/ceres-solver/ceres-solver.git"
SRCREV = "facb199f3eda902360f9e1d5271372b7e54febe1"

S = "${WORKDIR}/git"

inherit cmake

# We don't want path to eigen3 in ceres-solver RSS to be
# used by components which use CeresConfig.cmake from their
# own RSS
# ceres-solver/1.14-r0/packages-split/ceres-solver-dev/usr/lib/cmake/Ceres/CeresConfig.cmake:  set(Eigen3_DIR ceres-solver/1.14-r0/recipe-sysroot/usr/share/eigen3/cmake)
# ceres-solver/1.14-r0/packages-split/ceres-solver-dev/usr/lib/cmake/Ceres/CeresConfig.cmake:  list(APPEND EIGEN_INCLUDE_DIR_HINTS ceres-solver/1.14-r0/recipe-sysroot/usr/include/eigen3)
# ceres-solver/1.14-r0/packages-split/ceres-solver-dev/usr/lib/cmake/Ceres/CeresConfig.cmake:    set(glog_DIR ceres-solver/1.14-r0/recipe-sysroot/usr/lib/cmake/glog)
SSTATE_SCAN_FILES += "*.cmake"

PACKAGECONFIG ??= ""

# suitesparse* recipes will be in meta-ros layer
PACKAGECONFIG[suitesparse] = "-DSUITESPARSE=ON,-DSUITESPARSE=OFF,suitesparse-config suitesparse-amd suitesparse-camd suitesparse-colamd suitesparse-ccolamd suitesparse-cholmod suitesparse-metis suitesparse-spqr"
PACKAGECONFIG[cxsparse] = "-DCXSPARSE=ON,-DCXSPARSE=OFF,suitesparse-cxsparse"
PACKAGECONFIG[lapack] = "-DLAPACK=ON,-DLAPACK=OFF,lapack"

# Only a static library and headers are created
RDEPENDS_${PN}-dev = ""
RRECOMMENDS_${PN}-dev = "${PN}-staticdev"
RRECOMMENDS_${PN}-dbg = "${PN}-dev (= ${EXTENDPKGV})"
