SUMMARY = "Zlib manipulation Library"
DESCRIPTION = "minizip-ng is a zip manipulation library written in C that is supported \
on Windows, macOS, and Linux."
HOMEPAGE = "https://github.com/zlib-ng/minizip-ng"
SECTION = "libs"
LICENSE = "Zlib"
LIC_FILES_CHKSUM = "file://LICENSE;md5=36964f044224efeedd694983c34e716f"

SRC_URI = "git://github.com/zlib-ng/minizip-ng.git;protocol=https;branch=master \
           file://run-ptest \
"

SRCREV = "7b2387161c542fa9f427352dcdef76097d0d692b"


RCONFLICTS:${PN} += "minizip"

DEPENDS = "xz openssl bzip2"

inherit cmake pkgconfig ptest

# MZ_PPMD (new default-on feature since 4.2.x) git-clones ip7z/7zip over the
# network at configure time via CMake FetchContent; disable it, there's no
# network access in the build sandbox and nothing in this recipe needs it.
EXTRA_OECMAKE = "-DMZ_FORCE_FETCH_LIBS=OFF \
                 -DBUILD_SHARED_LIBS=ON \
                 -DMZ_PPMD=OFF \
"

PACKAGECONFIG ??= "zlib ${@bb.utils.contains('DISTRO_FEATURES', 'ptest', 'test', '', d)}"
PACKAGECONFIG[zlib] = "-DMZ_ZLIB=ON,-DMZ_ZLIB=OFF, zlib"
PACKAGECONFIG[zlib-ng] = "-DMZ_ZLIB=ON,-DMZ_ZLIB=OFF, zlib-ng"
PACKAGECONFIG[test] = "-DMZ_BUILD_TESTS=ON -DMZ_BUILD_UNIT_TESTS=ON, -DMZ_BUILD_TESTS=OFF -DMZ_BUILD_UNIT_TESTS=OFF, googletest"


do_install:append () {
    # remove absolute paths (4.2.x renamed this generated file from
    # minizip.cmake to minizip-config.cmake)
    sed -i -e 's|${RECIPE_SYSROOT}||g' ${D}${libdir}/cmake/minizip/minizip-config.cmake
}

do_install_ptest() {
    install -d ${D}${PTEST_PATH}/tests
    cp ${B}/test/gtest_minizip ${D}${PTEST_PATH}/tests
    # encrypt and stream test need the LICENSE file
    cp ${S}/LICENSE ${D}${PTEST_PATH}/tests
}

