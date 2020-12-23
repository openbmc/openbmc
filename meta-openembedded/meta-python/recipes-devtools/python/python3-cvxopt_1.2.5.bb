SUMMARY = "Convex optimization package"
HOMEPAGE = "http://cvxopt.org"
LICENSE = "GPL-3.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=4905701d14049b3f8e4774c290aeee21"

SRC_URI = "git://github.com/cvxopt/cvxopt;protocol=https"

SRCREV = "5b59d87007bf6fdea464e14875543b35255b84b2"

S = "${WORKDIR}/git"

RDEPENDS_${PN} += "lapack suitesparse"
DEPENDS += "lapack suitesparse"

inherit setuptools3

export CVXOPT_BLAS_LIB_DIR = "${STAGING_LIBDIR}"
export CVXOPT_SUITESPARSE_LIB_DIR = "${STAGING_LIBDIR}"
export CVXOPT_SUITESPARSE_INC_DIR = "${STAGING_INCDIR}"

EXCLUDE_FROM_WORLD = "1"
