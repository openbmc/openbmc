SUMMARY = "Open Asset Import Library"
DESCRIPTION = "The Open Asset Import Library (assimp) is a portable Open Source \
library to import various well-known 3D model formats in a uniform manner."
HOMEPAGE = "https://github.com/assimp/assimp"
SECTION = "devel"

LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://LICENSE;md5=f2d38c9e0d63058b051065fb7b1102a8"

DEPENDS = "zlib"

SRC_URI = "git://github.com/assimp/assimp.git;protocol=https;branch=master"

SRCREV = "1962da5ac484434524e7a25d566046edd932f901"

PV = "5.x+git${SRCPV}"

inherit cmake

EXTRA_OECMAKE = "\
    -DASSIMP_BUILD_ASSIMP_TOOLS=OFF \
    -DASSIMP_BUILD_TESTS=OFF \
    -DASSIMP_WARNINGS_AS_ERRORS=OFF \
    -DASSIMP_LIB_INSTALL_DIR=${baselib} \
    -DCMAKE_INSTALL_PREFIX=${prefix} \
    -DCMAKE_INSTALL_LIBDIR=${baselib} \
    -DCMAKE_SKIP_RPATH=ON \
    -DCMAKE_BUILD_WITH_INSTALL_RPATH=OFF \
"

do_install:append() {

    sed -i \
        -e "s:${WORKDIR}.*:${prefix}:g" \
        -e "s:${TMPDIR}.*:${prefix}:g" \
        ${D}${libdir}/pkgconfig/assimp.pc

    find ${D} -name "*assimp*cmake*" -delete
    find ${D} -name "*.la" -delete
}
