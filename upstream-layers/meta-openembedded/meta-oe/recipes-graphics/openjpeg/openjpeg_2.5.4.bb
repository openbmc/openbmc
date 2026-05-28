DESCRIPTION = "OpenJPEG library is an open-source JPEG 2000 codec"
HOMEPAGE = "http://www.openjpeg.org"
LICENSE = "BSD-2-Clause"
LIC_FILES_CHKSUM = "file://LICENSE;md5=c648878b4840d7babaade1303e7f108c"

DEPENDS = "libpng tiff lcms zlib"

SRC_URI = "git://github.com/uclouvain/openjpeg.git;branch=master;protocol=https \
           file://0001-Do-not-ask-cmake-to-export-binaries-they-don-t-make-.patch \
           file://CVE-2023-39327.patch \
           file://CVE-2026-6192.patch \
           file://run-ptest \
           "
SRCREV = "6c4a29b00211eb0430fa0e5e890f1ce5c80f409f"

inherit cmake ptest

# for multilib
EXTRA_OECMAKE += "-DOPENJPEG_INSTALL_LIB_DIR=${@d.getVar('baselib').replace('/', '')}"

EXTRA_OECMAKE += "${@bb.utils.contains('PTEST_ENABLED', '1', '-DBUILD_TESTING=ON', '', d)}"

FILES:${PN} += "${libdir}/openjpeg*"

do_install_ptest() {
    install -d ${D}${PTEST_PATH}

    # Install test binaries only
    if [ -d ${B}/bin ]; then
        install -d ${D}${PTEST_PATH}/bin
        find ${B}/bin -maxdepth 1 -type f -executable ! -name "*.so*" \
            -exec install -m 0755 {} ${D}${PTEST_PATH}/bin/ \;
    fi

    # Install CTest configuration files with proper structure
    if [ -d ${B}/tests ]; then
        cp -r ${B}/tests ${D}${PTEST_PATH}/
    fi
    if [ -f ${B}/CTestTestfile.cmake ]; then
        install -m 0644 ${B}/CTestTestfile.cmake ${D}${PTEST_PATH}/
    fi

    # Fix all build paths in cmake files to point to ptest installation
    find ${D}${PTEST_PATH} \( -name "*.cmake" -o -name "*.ctest" \) -type f -exec sed -i \
        -e "s#${B}/bin#${PTEST_PATH}/bin#g" \
        -e "s#${B}#${PTEST_PATH}#g" \
        -e "s#${S}#${PTEST_PATH}#g" \
        -e "s#${TMPDIR}#/tmp#g" \
        -e "s#${STAGING_DIR_NATIVE}##g" \
        -e "s#/tmp/work/[^/]*/[^/]*/[^/]*/recipe-sysroot-native/usr/bin/cmake#/usr/bin/cmake#g" \
        {} +
}

RDEPENDS:${PN}-ptest += "${PN} bash cmake"

BBCLASSEXTEND = "native nativesdk"
