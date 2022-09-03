SUMMARY = "Linear Algebra PACKage"
URL = "http://www.netlib.org/lapack"
LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://LICENSE;md5=a32c99f24d097c72d1857e533b55642b"

# Recipe needs FORTRAN support (copied from conf/local.conf.sample.extended)
# Enabling FORTRAN
# Note this is not officially supported and is just illustrated here to
# show an example of how it can be done
# You'll also need your fortran recipe to depend on libgfortran
#FORTRAN:forcevariable = ",fortran"
#RUNTIMETARGET:append:pn-gcc-runtime = " libquadmath"

DEPENDS = "libgfortran"

SRCREV = "32b062a33352e05771dcc01b981ebe961bf2e42f"
SRC_URI = "git://github.com/Reference-LAPACK/lapack.git;protocol=https;branch=master"
S = "${WORKDIR}/git"

PACKAGECONFIG ?= ""
PACKAGECONFIG[lapacke] = "-DLAPACKE=ON,-DLAPACKE=OFF"

EXTRA_OECMAKE = " -DBUILD_SHARED_LIBS=ON "
OECMAKE_GENERATOR = "Unix Makefiles"

inherit cmake pkgconfig
EXCLUDE_FROM_WORLD = "1"
