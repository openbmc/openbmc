SUMMARY = "OpenGL Mathematics Library"
DESCRIPTION = "OpenGL Mathematics (GLM) is a header only C++ \
mathematics library for graphics software based on the OpenGL \
Shading Language (GLSL) specifications."
HOMEPAGE = "https://glm.g-truc.net"
BUGTRACKER = "https://github.com/g-truc/glm/issues"
SECTION = "libs"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://copying.txt;md5=462e4b97f73ef12f8171c3c546ce4e8d"

SRC_URI = " \
    git://github.com/g-truc/glm;branch=master;protocol=https \
    file://0001-Silence-clang-warnings.patch \
    file://0001-Do-not-use-Werror-with-clang.patch \
    file://glmConfig.cmake.in \
    file://glmConfigVersion.cmake.in \
    file://glm.pc.in \
    file://glmTargets.cmake \
"
SRCREV = "efec5db081e3aad807d0731e172ac597f6a39447"
PV .= "+0.9.9.9+git"

S = "${WORKDIR}/git"

inherit cmake

do_install() {
    install -d ${D}${includedir} ${D}${docdir}/glm ${D}${libdir}/pkgconfig ${D}${libdir}/cmake/glm
    cp -R --no-dereference --preserve=mode,links ${S}/glm ${D}${includedir}
    cp -R --no-dereference --preserve=mode,links ${S}/doc ${D}${docdir}/glm
    rm ${D}${includedir}/glm/CMakeLists.txt
    sed "s/@VERSION@/${PV}/" ${UNPACKDIR}/glmConfigVersion.cmake.in > ${D}${libdir}/cmake/glm/glmConfigVersion.cmake
    sed "s/@VERSION@/${PV}/" ${UNPACKDIR}/glmConfig.cmake.in > ${D}${libdir}/cmake/glm/glmConfig.cmake
    sed "s/@VERSION@/${PV}/" ${UNPACKDIR}/glm.pc.in > ${D}${libdir}/pkgconfig/glm.pc
    install -Dm644 ${UNPACKDIR}/glmTargets.cmake ${D}${libdir}/cmake/glm/glmTargets.cmake

}

RDEPENDS:${PN}-dev = ""

BBCLASSEXTEND = "native"
