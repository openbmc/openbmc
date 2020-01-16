SUMMARY = "OpenGL Mathematics Library"
DESCRIPTION = "OpenGL Mathematics (GLM) is a header only C++ \
mathematics library for graphics software based on the OpenGL \
Shading Language (GLSL) specifications."
HOMEPAGE = "https://glm.g-truc.net"
BUGTRACKER = "https://github.com/g-truc/glm/issues"
SECTION = "libs"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://copying.txt;md5=4a735e33f271f57404fda17e80085411"

SRC_URI = " \
    git://github.com/g-truc/glm;branch=master \
    file://0001-Fix-Wimplicit-int-float-conversion-warnings-with-cla.patch \
    file://glmConfig.cmake.in \
    file://glmConfigVersion.cmake.in \
    file://glm.pc.in \
    file://glmTargets.cmake \
"
SRCREV = "4db8f89aace8f04c839b606e15b39fb8383ec732"

S = "${WORKDIR}/git"

inherit cmake

do_install() {
    install -d ${D}${includedir} ${D}${docdir}/glm ${D}${libdir}/pkgconfig ${D}${libdir}/cmake/glm
    cp -R --no-dereference --preserve=mode,links ${S}/glm ${D}${includedir}
    cp -R --no-dereference --preserve=mode,links ${S}/doc ${D}${docdir}/glm
    rm ${D}${includedir}/glm/CMakeLists.txt
    sed "s/@VERSION@/${PV}/" ${WORKDIR}/glmConfigVersion.cmake.in > ${D}${libdir}/cmake/glm/glmConfigVersion.cmake
    sed "s/@VERSION@/${PV}/" ${WORKDIR}/glmConfig.cmake.in > ${D}${libdir}/cmake/glm/glmConfig.cmake
    sed "s/@VERSION@/${PV}/" ${WORKDIR}/glm.pc.in > ${D}${libdir}/pkgconfig/glm.pc
    install -Dm644 ${WORKDIR}/glmTargets.cmake ${D}${libdir}/cmake/glm/glmTargets.cmake

}

RDEPENDS_${PN}-dev = ""

BBCLASSEXTEND = "native"
