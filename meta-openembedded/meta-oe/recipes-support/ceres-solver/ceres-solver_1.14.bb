DESCRIPTION = "Ceres Solver is an open source C++ library for modeling and solving large, complicated optimization problems."
AUTHOR = "Sameer Agarwal and Keir Mierle and Others"
HOMEPAGE = "http://ceres-solver.org/"

LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://LICENSE;md5=35e00f0c4c96a0820a03e0b31e6416be"

SRC_URI = "git://github.com/ceres-solver/ceres-solver.git"
SRCREV = "facb199f3eda902360f9e1d5271372b7e54febe1"

S = "${WORKDIR}/git"

DEPENDS = "libeigen glog"

inherit cmake

# Only a static library and headers are created
ALLOW_EMPTY_${PN} = "1"

FILES_${PN}-dev += "${libdir}/cmake/*"
