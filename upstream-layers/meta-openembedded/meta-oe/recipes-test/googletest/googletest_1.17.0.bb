DESCRIPTION = "Google's framework for writing C++ tests"
HOMEPAGE = "https://github.com/google/googletest"
SECTION = "libs"
LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://LICENSE;md5=cbbd27594afd089daa160d3a16dd515a"

PROVIDES += "gmock gtest"

SRC_URI = "git://github.com/google/googletest.git;branch=v1.17.x;protocol=https \
           file://run-ptest \
          "
SRCREV = "52eb8108c5bdec04579160ae17225d66034bd723"

inherit cmake pkgconfig ptest

# allow for shared libraries, but do not default to them
#
PACKAGECONFIG[shared] = "-DBUILD_SHARED_LIBS=ON,-DBUILD_SHARED_LIBS=OFF,,"

EXTRA_OECMAKE += "${@bb.utils.contains('PTEST_ENABLED', '1', '-Dgtest_build_tests=ON -Dgmock_build_tests=ON', '', d)}"

DEPENDS += "${@bb.utils.contains('PTEST_ENABLED', '1', 'rsync-native', '', d)}"

CXXFLAGS:append = " -fPIC"

ALLOW_EMPTY:${PN} = "1"
ALLOW_EMPTY:${PN}-dbg = "1"

# -staticdev will not be implicitly put into an SDK, so we add an rdepend
# if we are not building shared libraries
#
RDEPENDS:${PN}-dev += "${@bb.utils.contains("PACKAGECONFIG","shared","","${PN}-staticdev",d)}"

BBCLASSEXTEND = "native nativesdk"

do_configure:prepend() {
    # explicitly use python3
    # the scripts are already python3 compatible since https://github.com/google/googletest/commit/d404af0d987a9c38cafce82a7e26ec8468c88361 and other fixes like this
    # but since this oe-core change http://git.openembedded.org/openembedded-core/commit/?id=5f8f16b17f66966ae91aeabc23e97de5ecd17447
    # there isn't python in HOSTTOOLS so "env python" fails
    sed -i 's@^#!/usr/bin/env python$@#!/usr/bin/env python3@g' ${S}/googlemock/test/*py ${S}/googletest/test/*py
}

do_install_ptest() {
    install -d ${D}${PTEST_PATH}/lib

    ptest_libs="libshared_gmock_main.so \
                libgtest_dll.so \
                libgmock_main_no_exception.so \
                libgtest_main_no_exception.so \
                libgtest_no_exception.so \
                libgtest_main_no_rtti.so \
               "
    for i in ${ptest_libs}; do
        [ -f ${B}/lib/${i} ] && install -m 0755 ${B}/lib/${i} ${D}${PTEST_PATH}/lib
    done

    rsync -a ${B}/googletest ${D}${PTEST_PATH} \
          --exclude CMakeFiles \
          --exclude cmake_install.cmake \
          --exclude Makefile \
          --exclude generated
    install -d ${D}${PTEST_PATH}/googletest/test
    cp ${S}/googletest/test/*.py ${D}${PTEST_PATH}/googletest/test
    cp ${S}/googletest/test/*.txt ${D}${PTEST_PATH}/googletest/test
    rsync -a ${B}/googlemock ${D}${PTEST_PATH} \
          --exclude CMakeFiles \
          --exclude cmake_install.cmake \
          --exclude Makefile \
          --exclude generated
    cp ${B}/CTestTestfile.cmake ${D}${PTEST_PATH}
    sed -i -e 's#${B}#${PTEST_PATH}#g' `find ${D}${PTEST_PATH} -name CTestTestfile.cmake`
    sed -i -e 's#${S}#${PTEST_PATH}#g' `find ${D}${PTEST_PATH} -name CTestTestfile.cmake`
    sed -i -e 's#${RECIPE_SYSROOT_NATIVE}##g' `find ${D}${PTEST_PATH} -name CTestTestfile.cmake`
    sed -i -e 's#${PYTHON}#/usr/bin/python3#g' `find ${D}${PTEST_PATH} -name CTestTestfile.cmake`
    sed -i -e 's#${WORKDIR}##g' `find ${D}${PTEST_PATH} -name CTestTestfile.cmake`
}

RDEPENDS:${PN}-ptest += "cmake sed python3-datetime python3-difflib python3-json python3-xml python3-misc python3-unittest"
