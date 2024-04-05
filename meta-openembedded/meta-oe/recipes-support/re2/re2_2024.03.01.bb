DESCRIPTION = "A regular expression library"
HOMEPAGE = "https://github.com/google/re2/"
LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://LICENSE;md5=3b5c31eb512bdf3cb11ffd5713963760"

# tag 2024-03-01
SRCREV = "2d866a3d0753f4f4fce93cccc6c59c4b052d7db4"

SRC_URI = "git://github.com/google/re2.git;branch=main;protocol=https \
           ${@bb.utils.contains('PTEST_ENABLED', '1', 'file://run-ptest', '', d)} \
          "

S = "${WORKDIR}/git"

DEPENDS = "abseil-cpp ${@bb.utils.contains('PTEST_ENABLED', '1', 'gtest googlebenchmark', '', d)}"

inherit cmake ptest
RDEPENDS:${PN}-ptest += "cmake sed"
RDEPENDS:${PN} += "abseil-cpp-dev"

INSANE_SKIP:${PN} += "dev-deps"

EXTRA_OECMAKE += " \
	-DBUILD_SHARED_LIBS=ON \
	${@bb.utils.contains('PTEST_ENABLED', '1', '-DRE2_BUILD_TESTING=ON', '-DRE2_BUILD_TESTING=OFF', d)} \
"

do_install_ptest () {
    cp -r ${B}/*_test ${D}${PTEST_PATH}
    cp -r ${B}/CTestTestfile.cmake ${D}${PTEST_PATH}
    sed -i -e 's#${B}#${PTEST_PATH}#g' `find ${D}${PTEST_PATH} -name CTestTestfile.cmake`
    sed -i -e 's#${S}#${PTEST_PATH}#g' `find ${D}${PTEST_PATH} -name CTestTestfile.cmake`
    # ERROR: re2-2024.03.01-r0 do_package_qa: QA Issue: /usr/lib64/re2/ptest/string_generator_test contained in package re2-ptest requires libtesting.so()(64bit), but no providers found in RDEPENDS:re2-ptest? [file-rdeps]
    cp -r ${B}/libtesting.so ${D}${PTEST_PATH}
}

# ignore .so in /usr/lib64
FILES:${PN} = "${libdir}"
INSANE_SKIP:${PN} += "dev-so"

# Don't include so files in dev package
FILES:${PN}-dev = "${includedir} ${libdir}/cmake"

BBCLASSEXTEND = "native nativesdk"
