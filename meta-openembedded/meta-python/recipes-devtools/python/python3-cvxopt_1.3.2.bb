SUMMARY = "Convex optimization package"
HOMEPAGE = "http://cvxopt.org"
LICENSE = "GPL-3.0-only"
LIC_FILES_CHKSUM = "file://LICENSE;md5=66ec4f8e53d3e733a8c26d5ca3830fba"

SRC_URI = "git://github.com/cvxopt/cvxopt;protocol=https;branch=master"

SRCREV = "3b718ee560b3b97d6255c55f0ed7f64cb4b72082"

S = "${WORKDIR}/git"

RDEPENDS:${PN} += "lapack suitesparse"
DEPENDS += "lapack suitesparse"

inherit setuptools3

export CVXOPT_BLAS_LIB_DIR = "${STAGING_LIBDIR}"
export CVXOPT_SUITESPARSE_LIB_DIR = "${STAGING_LIBDIR}"
export CVXOPT_SUITESPARSE_INC_DIR = "${STAGING_INCDIR}"

EXCLUDE_FROM_WORLD = "1"
