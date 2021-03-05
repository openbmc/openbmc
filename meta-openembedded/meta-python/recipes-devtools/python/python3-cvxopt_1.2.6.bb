SUMMARY = "Convex optimization package"
HOMEPAGE = "http://cvxopt.org"
LICENSE = "GPL-3.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=ba1a8a73d8ebea5c47a1173aaf476ddd"

SRC_URI = "git://github.com/cvxopt/cvxopt;protocol=https"

SRCREV = "60fdb838e0bb2d8f32ba51129552c83b55acd2a7"

S = "${WORKDIR}/git"

RDEPENDS_${PN} += "lapack suitesparse"
DEPENDS += "lapack suitesparse"

inherit setuptools3

export CVXOPT_BLAS_LIB_DIR = "${STAGING_LIBDIR}"
export CVXOPT_SUITESPARSE_LIB_DIR = "${STAGING_LIBDIR}"
export CVXOPT_SUITESPARSE_INC_DIR = "${STAGING_INCDIR}"

EXCLUDE_FROM_WORLD = "1"
