SUMMARY = "A C++ static library offering a clean and simple interface to the 7-Zip shared libraries"
HOMEPAGE = "https://github.com/rikyoz/bit7z"
LICENSE = "MPL-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=48a3fe23ed1353e0995dadfda05ffdb6"

SRC_URI = " \
    git://github.com/rikyoz/bit7z.git;protocol=https;branch=master \
    ${@bb.utils.contains('PTEST_ENABLED', '1', d.getVar('SRC_URI_PTEST'), 'file://0001-cmake-disable-dependency-inclusion.patch', d)} \
    file://0001-Fix-reinterpret-cast-compiler-errors.patch \
    file://0001-Fix-int8_t-storage-in-BitPropVariant-on-Arm-architec.patch \
    file://0001-Allow-running-tests-on-target-when-cross-compiling.patch \
    file://0001-Allow-specifying-path-to-7z-library-in-tests.patch \
    file://0001-Fix-tests-with-musl.patch \
"

SRCREV = "386e00ad3286e7a10e5bb6d05a5b41b523fce623"

# ptest dependencies and their revisions
SRC_URI_PTEST = " \
    git://github.com/rikyoz/filesystem.git;protocol=https;branch=glibcxx_wchar_streams_workaround;name=filesystem;destsuffix=filesystem \
    git://github.com/rikyoz/bit7z-test-data.git;protocol=https;branch=main;name=testdata;destsuffix=testdata \
    git://github.com/catchorg/Catch2.git;protocol=https;branch=v2.x;name=catch2;destsuffix=catch2;tag=${TAG_catch2} \
    https://github.com/cpm-cmake/CPM.cmake/releases/download/v${TAG_CPM}/CPM.cmake;downloadfilename=CPM_${TAG_CPM}.cmake \
    file://run-ptest \
"
SRCREV_FORMAT = "${@bb.utils.contains('PTEST_ENABLED', '1', 'default_filesystem_testdata_catch2', 'default', d)}"
SRCREV_filesystem = "983650f374699e3979f9cdefe13ddff60bd4ac68"
SRCREV_testdata = "077e407b1c07b7443626b5902eeb4819388bf656"
SRCREV_catch2 = "182c910b4b63ff587a3440e08f84f70497e49a81"
TAG_catch2 = "v2.13.10"
SRCHASH_CPM = "c8cdc32c03816538ce22781ed72964dc864b2a34a310d3b7104812a5ca2d835d"
TAG_CPM = "0.40.2"
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
    grep -q ${TAG_catch2} ${S}/tests/cmake/Catch2.cmake || bbfatal "ERROR: dependency version mismatch, please update 'SRCREV_catch2'!"
    grep -q ${SRCHASH_CPM} ${S}/cmake/Dependencies.cmake || bbfatal "ERROR: dependency version mismatch, please update 'SRCHASH_CPM'!"

    if ${@bb.utils.contains('PTEST_ENABLED', '1', 'true', 'false', d)}; then
        # use cache instead of download for CPM (CMake's missing package manager)
        mkdir -p ${B}/cmake
        cp ${UNPACKDIR}/CPM_${TAG_CPM}.cmake ${B}/cmake
        mkdir -p ${B}/cpm_cache/ghc_filesystem
        cp -r ${UNPACKDIR}/filesystem ${B}/cpm_cache/ghc_filesystem/fbcc9a9e94e6365273cf51294173f21ff5efdb4f
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
