SUMMARY = "A C++ static library offering a clean and simple interface to the 7-Zip shared libraries"
HOMEPAGE = "https://github.com/rikyoz/bit7z"
LICENSE = "MPL-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=48a3fe23ed1353e0995dadfda05ffdb6"

SRC_URI = " \
    git://github.com/rikyoz/bit7z.git;protocol=https;branch=master;tag=v${PV} \
    ${@bb.utils.contains('PTEST_ENABLED', '1', d.getVar('SRC_URI_PTEST'), 'file://0001-cmake-disable-dependency-inclusion.patch', d)} \
"

SRCREV = "82f359371fda5c16c037ac0659b969334816a9c4"

# ptest dependencies and their revisions
SRC_URI_PTEST = " \
    git://github.com/rikyoz/filesystem.git;protocol=https;branch=glibcxx_wchar_streams_workaround;name=filesystem;destsuffix=filesystem \
    git://github.com/rikyoz/bit7z-test-data.git;protocol=https;branch=main;name=testdata;destsuffix=testdata \
    git://github.com/rikyoz/Catch2.git;protocol=https;branch=single-header-v2.x;name=catch2;destsuffix=catch2 \
    https://github.com/cpm-cmake/CPM.cmake/releases/download/v${TAG_CPM}/CPM.cmake;downloadfilename=CPM_${TAG_CPM}.cmake \
    file://run-ptest \
    file://0001-cmake-disable-filesystem-gitclone.patch \
"
SRCREV_FORMAT = "${@bb.utils.contains('PTEST_ENABLED', '1', 'default_filesystem_testdata_catch2', 'default', d)}"
SRCREV_filesystem = "b99c2aebd5ddd6fb2f190731ba80b949fc3842b5"
SRCREV_testdata = "077e407b1c07b7443626b5902eeb4819388bf656"
SRCREV_catch2 = "27d8db1770dd5cd3688656095f242474431584a1"
SRCHASH_CPM = "2020b4fc42dba44817983e06342e682ecfc3d2f484a581f11cc5731fbe4dce8a"
TAG_CPM = "0.42.0"
SRC_URI[sha256sum] = "${SRCHASH_CPM}"


inherit cmake ptest

DEPENDS = "7zip"

EXTRA_OECMAKE += "-DBIT7Z_CUSTOM_7ZIP_PATH=${STAGING_INCDIR}/7zip"

PACKAGECONFIG ??= "${@bb.utils.contains('PTEST_ENABLED', '1', 'tests', '', d)}"
PACKAGECONFIG[tests] = " \
    -DBIT7Z_BUILD_TESTS=ON -DBIT7Z_DISABLE_USE_STD_FILESYSTEM=ON \
    -DBIT7Z_TESTS_USE_SYSTEM_7ZIP=OFF -DBIT7Z_TESTS_7Z_LIBRARY_PATH=${libdir}/lib7z.so \
    -DBIT7Z_TESTS_DATA_DIR_TARGET=${PTEST_PATH}/data \
    -DCPM_SOURCE_CACHE=${B}/cpm_cache -DFETCHCONTENT_SOURCE_DIR_BIT7Z-TEST-DATA=${UNPACKDIR}/testdata -DFETCHCONTENT_SOURCE_DIR_CATCH2=${B}/catch2 \
"

do_configure:prepend() {
    # verify that all dependencies have correct version
    grep -q ${SRCREV_filesystem} ${S}/cmake/Dependencies.cmake || bbfatal "ERROR: dependency version mismatch, please update 'SRCREV_filesystem'!"
    grep -q ${SRCREV_testdata} ${S}/tests/CMakeLists.txt || bbfatal "ERROR: dependency version mismatch, please update 'SRCREV_testdata'!"
    grep -q ${SRCREV_catch2} ${S}/tests/CMakeLists.txt || bbfatal "ERROR: dependency version mismatch, please update 'SRCREV_catch2'!"
    grep -q ${SRCHASH_CPM} ${S}/cmake/Dependencies.cmake || bbfatal "ERROR: dependency version mismatch, please update 'SRCHASH_CPM'!"

    if ${@bb.utils.contains('PTEST_ENABLED', '1', 'true', 'false', d)}; then
        # use cache instead of download for CPM (CMake's missing package manager)
        mkdir -p ${B}/cpm_cache/cpm/
        cp ${UNPACKDIR}/CPM_${TAG_CPM}.cmake ${B}/cpm_cache/cpm/
        cp -r ${UNPACKDIR}/filesystem ${B}/cpm_cache/ghc_filesystem
        # avoid buildpaths issue as unpackdir is not in prefix maps
        cp -r ${UNPACKDIR}/catch2 ${B}
    fi
}
do_configure[cleandirs] += "${B}"

do_install() {
    install -d ${D}${libdir}
    install -m 0644 ${S}/lib/*/*.a ${D}${libdir}

    install -d ${D}${includedir}/${BPN}
    install -m 0644 ${S}/include/${BPN}/*.hpp ${D}${includedir}/${BPN}
}

do_install_ptest() {
    install -m 0755 ${S}/bin/*/* ${D}${PTEST_PATH}
    install -d ${D}${PTEST_PATH}/data
    cp -r ${UNPACKDIR}/testdata/test_archives ${UNPACKDIR}/testdata/test_filesystem ${B}/tests/data/test_filesystem ${D}${PTEST_PATH}/data
}

# this package contains static library so main package is empty, but ptest package rdepends on it
ALLOW_EMPTY:${PN} = "1"
# these are loaded via dlopen, so need explicit rdepends
RDEPENDS:${PN}-ptest += "libstdc++ 7zip"
# test data contains various file types with different architectures
INSANE_SKIP:${PN}-ptest += "arch"
