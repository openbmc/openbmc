SUMMARY = "Double conversion libraries"
DESCRIPTION = "This provides binary-decimal and decimal-binary routines for IEEE doubles."
HOMEPAGE = "https://github.com/google/double-conversion.git"

LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://LICENSE;md5=1ea35644f0ec0d9767897115667e901f"


S = "${WORKDIR}/git"

SRC_URI = " \
        git://github.com/google/double-conversion.git;protocol=https;branch=master \
        file://run-ptest \
"
SRCREV = "4f7a25d8ced8c7cf6eee6fd09d6788eaa23c9afe"

inherit cmake ptest

EXTRA_OECMAKE = "-DBUILD_SHARED_LIBS=ON"

# These ptest use ctest (provided by cmake)
RDEPENDS:${PN}-ptest += "cmake"
# Build tests only if ptest is enabled
EXTRA_OECMAKE += "${@bb.utils.contains('PTEST_ENABLED', '1', '-DBUILD_TESTING=ON', '', d)}"

do_install_ptest() {
    install -d ${D}${PTEST_PATH}/test
    cp -rf ${B}/test ${D}${PTEST_PATH}
    install -m 0644 ${B}/CTestTestfile.cmake ${D}${PTEST_PATH}/
    files="
        CTestTestfile.cmake
        test/CTestTestfile.cmake
        test/cmake_install.cmake
        test/cctest/CTestTestfile.cmake
        test/cctest/cmake_install.cmake
    "
    for file in $files; do
        sed -i -e "s|${B}|${PTEST_PATH}|g" -e "s|${S}|${PTEST_PATH}|g" -e "s|${WORKDIR}/recipe-sysroot-native||g" "${D}${PTEST_PATH}/${file}"
    done

}
