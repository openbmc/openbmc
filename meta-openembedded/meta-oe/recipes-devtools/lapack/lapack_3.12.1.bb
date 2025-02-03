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

SRCREV = "5ebe92156143a341ab7b14bf76560d30093cfc54"
SRC_URI = "git://github.com/Reference-LAPACK/lapack.git;protocol=https;branch=master \
           ${@bb.utils.contains('PTEST_ENABLED', '1', 'file://run-ptest', '', d)} \
          "
S = "${WORKDIR}/git"

PACKAGECONFIG ?= ""
PACKAGECONFIG[lapacke] = "-DLAPACKE=ON,-DLAPACKE=OFF"
PACKAGECONFIG[cblas] = "-DCBLAS=ON,-DCBLAS=OFF"

EXTRA_OECMAKE = " -DBUILD_SHARED_LIBS=ON \
                  ${@bb.utils.contains('PTEST_ENABLED', '1', ' -DBUILD_TESTING=ON', '', d)} \
                "
OECMAKE_GENERATOR = "Unix Makefiles"

inherit cmake pkgconfig ptest
EXCLUDE_FROM_WORLD = "1"

# The `xerbla.o` file contains an absolute path in `xerbla.f.o`, but the options
# `-fdebug-prefix-map` and `-ffile-prefix-map` cannot be used because gfortran does
# not support them. And we cannot easily change CMake to use relative paths, because
# it will convert them to absolute paths when generating Unix Makefiles or Ninja:
# https://gitlab.kitware.com/cmake/community/-/wikis/FAQ#why-does-cmake-use-full-paths-or-can-i-copy-my-build-tree
# https://gitlab.kitware.com/cmake/cmake/-/issues/13894
#
# To address this issue, we manually replace the absolute path with a relative path
# in the generated `build.make` file.
#
# An issue has been reported: https://github.com/Reference-LAPACK/lapack/issues/1087,
# requesting a fix in the source code.
#
# This workaround resolves the TMPDIR [buildpaths] issue by converting the absolute path
# of `xerbla.f` to a relative path. The steps are as follows:
#
# 1. Locate all `build.make` files after the `do_configure` step is completed.
# 2. Compute the relative path for various `*.f` files based on the current build directory.
# 3. Replace the absolute path with the calculated relative path in the `build.make` files
#
# Additionally, when ptests are enabled, apply a simpler workaround for ptest code:
# - Replace occurrences of `${WORKDIR}` in all `build.make` files under the TESTING directory, excluding
#   the MATGEN subdirectory, with a relative path prefix of `"../../.."`.
do_configure:append(){
    for file in `find ${B} -name build.make`; do
        # Replacing all .f files found with:
        # for f in $(find ${S} -name \*.f -printf " %f" | sort -u); do
        # would be more reliable with other optional PACKAGECONFIGs, but also very slow as there are
        # ~ 3500 of them and this loop takes around 20 minutes
        for f in xerbla.f c_cblat1.f c_cblat2.f c_cblat3.f c_dblat1.f c_dblat2.f c_dblat3.f c_sblat1.f c_sblat2.f c_sblat3.f c_zblat1.f c_zblat2.f c_zblat3.f cchkdmd.f90 dchkdmd.f90 schkdmd.f90 zchkdmd.f90; do
            sed -i -e "s#\(.*-c \).*\(/$f \)#\1$(grep "\-c .*$f" $file | awk -F'cd ' '{print $2}'| \
                awk "{src=\$1; sub(/.*-c /, \"\"); sub(/$f.*/, \"\"); obj=\$0; print src, obj}" | \
                while read src obj; do echo "$(realpath --relative-to="$src" "$obj")"; done)\2#g" $file
         done
    done
    if ${@bb.utils.contains('PTEST_ENABLED', '1', 'true', 'false', d)} ; then
        for file in `find . -name build.make -path '*TESTING*' -not -path '*MATGEN*'`; do
            sed -i -e "s#\(.*-c \)\(${WORKDIR}\)\(.*.[f|F] \)#\1../../..\3#g" $file
        done
    fi
}

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
