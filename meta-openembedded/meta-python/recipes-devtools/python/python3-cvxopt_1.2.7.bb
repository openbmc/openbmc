SUMMARY = "Convex optimization package"
HOMEPAGE = "http://cvxopt.org"
LICENSE = "GPL-3.0-only"
LIC_FILES_CHKSUM = "file://LICENSE;md5=ba1a8a73d8ebea5c47a1173aaf476ddd"

SRC_URI = "git://github.com/cvxopt/cvxopt;protocol=https;branch=master"

SRCREV = "d5a21cf1da62e4269176384b1ff62edac5579f94"

S = "${WORKDIR}/git"

RDEPENDS:${PN} += "lapack suitesparse"
DEPENDS += "lapack suitesparse"

inherit setuptools3

export CVXOPT_BLAS_LIB_DIR = "${STAGING_LIBDIR}"
export CVXOPT_SUITESPARSE_LIB_DIR = "${STAGING_LIBDIR}"
export CVXOPT_SUITESPARSE_INC_DIR = "${STAGING_INCDIR}"

EXCLUDE_FROM_WORLD = "1"
