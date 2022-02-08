SUMMARY = "Linear Algebra PACKage"
URL = "http://www.netlib.org/lapack"
LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://LICENSE;md5=930f8aa500a47c7dab0f8efb5a1c9a40"

# Recipe needs FORTRAN support (copied from conf/local.conf.sample.extended)
# Enabling FORTRAN
# Note this is not officially supported and is just illustrated here to
# show an example of how it can be done
# You'll also need your fortran recipe to depend on libgfortran
#FORTRAN_forcevariable = ",fortran"
#RUNTIMETARGET_append_pn-gcc-runtime = " libquadmath"

DEPENDS = "libgfortran"

SRCREV = "6acc99d5f39130be7cec00fb835606042101a970"
SRC_URI = "git://github.com/Reference-LAPACK/lapack.git;protocol=https;branch=master"
S = "${WORKDIR}/git"

EXTRA_OECMAKE = " -DBUILD_SHARED_LIBS=ON "
OECMAKE_GENERATOR = "Unix Makefiles"

inherit cmake pkgconfig
EXCLUDE_FROM_WORLD = "1"
