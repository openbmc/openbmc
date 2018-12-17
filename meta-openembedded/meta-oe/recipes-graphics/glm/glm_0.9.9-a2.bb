SUMMARY = "OpenGL Mathematics Library"
DESCRIPTION = "OpenGL Mathematics (GLM) is a header only C++ \
mathematics library for graphics software based on the OpenGL \
Shading Language (GLSL) specifications."
HOMEPAGE = "https://glm.g-truc.net"
BUGTRACKER = "https://github.com/g-truc/glm/issues"
SECTION = "libs"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://readme.md;beginline=21;endline=22;md5=3075b5727d36f29edccf97b93e72b790"

SRC_URI = " \
    git://github.com/g-truc/glm;branch=master \
    file://0001-Make-GLM_ENABLE_EXPERIMENTAL-a-configurable-option.patch \
    file://0002-glm-install-headers-only.patch \
"
SRCREV = "fcbedf5058ef8613dd02aac62ef00d55dcfeadd7"

S = "${WORKDIR}/git"

inherit cmake

EXTRA_OECMAKE = "-DGLM_ENABLE_EXPERIMENTAL=ON"

RDEPENDS_${PN}-dev = ""

BBCLASSEXTEND = "native"
