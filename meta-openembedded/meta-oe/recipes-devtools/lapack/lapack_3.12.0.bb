SUMMARY = "Linear Algebra PACKage"
URL = "http://www.netlib.org/lapack"
LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://LICENSE;md5=d0e7a458f9fcbf0a3ba97cef3128b85d"

# Recipe needs FORTRAN support (copied from conf/local.conf.sample.extended)
# Enabling FORTRAN
# Note this is not officially supported and is just illustrated here to
# show an example of how it can be done
# You'll also need your fortran recipe to depend on libgfortran
#FORTRAN:forcevariable = ",fortran"
#RUNTIMETARGET:append:pn-gcc-runtime = " libquadmath"

DEPENDS = "libgfortran \
           ${@bb.utils.contains('PTEST_ENABLED', '1', 'rsync-native', '', d)} \
          "
RDEPENDS:${PN}-ptest += "cmake"

SRCREV = "04b044e020a3560ccfa9988c8a80a1fb7083fc2e"
SRC_URI = "git://github.com/Reference-LAPACK/lapack.git;protocol=https;branch=master \
           ${@bb.utils.contains('PTEST_ENABLED', '1', 'file://run-ptest', '', d)} \
          "
S = "${WORKDIR}/git"

PACKAGECONFIG ?= ""
PACKAGECONFIG[lapacke] = "-DLAPACKE=ON,-DLAPACKE=OFF"

EXTRA_OECMAKE = " -DBUILD_SHARED_LIBS=ON \
                  ${@bb.utils.contains('PTEST_ENABLED', '1', ' -DBUILD_TESTING=ON', '', d)} \
                "
OECMAKE_GENERATOR = "Unix Makefiles"

inherit cmake pkgconfig ptest
EXCLUDE_FROM_WORLD = "1"

do_install_ptest () {
    rsync -a ${B}/TESTING ${D}${PTEST_PATH} \
          --exclude CMakeFiles \
          --exclude cmake_install.cmake \
          --exclude Makefile
    rsync -a ${B}/BLAS ${D}${PTEST_PATH} \
          --exclude CMakeFiles \
          --exclude cmake_install.cmake \
          --exclude Makefile
    rsync -a ${B}/LAPACKE ${D}${PTEST_PATH} \
          --exclude CMakeFiles \
          --exclude cmake_install.cmake \
          --exclude Makefile
    cp -r ${B}/bin ${D}${PTEST_PATH}
    cp -r ${B}/lapack_testing.py ${D}${PTEST_PATH}
    cp ${B}/CTestTestfile.cmake ${D}${PTEST_PATH}
    cp ${S}/TESTING/*.in ${S}/TESTING/runtest.cmake ${D}${PTEST_PATH}/TESTING
    cp ${S}/BLAS/TESTING/*.in ${D}${PTEST_PATH}/BLAS/TESTING
    sed -i -e 's#${B}#${PTEST_PATH}#g' `find ${D}${PTEST_PATH} -name CTestTestfile.cmake`
    sed -i -e 's#${S}#${PTEST_PATH}#g' `find ${D}${PTEST_PATH} -name CTestTestfile.cmake`
    sed -i -e 's#${RECIPE_SYSROOT_NATIVE}##g' `find ${D}${PTEST_PATH} -name CTestTestfile.cmake`
    sed -i -e 's#${PYTHON}#/usr/bin/python3#g' `find ${D}${PTEST_PATH} -name CTestTestfile.cmake`
    sed -i -e 's#${WORKDIR}##g' `find ${D}${PTEST_PATH} -name CTestTestfile.cmake`
}
