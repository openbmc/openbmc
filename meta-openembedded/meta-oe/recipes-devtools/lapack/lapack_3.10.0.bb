SUMMARY = "Linear Algebra PACKage"
URL = "http://www.netlib.org/lapack"
LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://LICENSE;md5=39902829ba0c2cbac1b0debfb75a416b"

# Recipe needs FORTRAN support (copied from conf/local.conf.sample.extended)
# Enabling FORTRAN
# Note this is not officially supported and is just illustrated here to
# show an example of how it can be done
# You'll also need your fortran recipe to depend on libgfortran
#FORTRAN:forcevariable = ",fortran"
#RUNTIMETARGET:append:pn-gcc-runtime = " libquadmath"

DEPENDS = "libgfortran"

SRCREV = "aa631b4b4bd13f6ae2dbab9ae9da209e1e05b0fc"
SRC_URI = "git://github.com/Reference-LAPACK/lapack.git;protocol=https;branch=master"
S = "${WORKDIR}/git"

EXTRA_OECMAKE = " -DBUILD_SHARED_LIBS=ON "
OECMAKE_GENERATOR = "Unix Makefiles"

inherit cmake pkgconfig
EXCLUDE_FROM_WORLD = "1"
