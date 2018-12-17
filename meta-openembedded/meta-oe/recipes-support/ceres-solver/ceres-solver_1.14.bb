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

# Only a static library and headers are created
RDEPENDS_${PN}-dev = ""
RRECOMMENDS_${PN}-dev = "${PN}-staticdev"
RRECOMMENDS_${PN}-dbg = "${PN}-dev (= ${EXTENDPKGV})"
