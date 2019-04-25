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
    file://0001-glm-Remove-redundant-double-semi-colons.patch \
"
# v0.9.9.3
SRCREV = "9749727c2db4742369219e1d452f43e918734b4e"

S = "${WORKDIR}/git"

inherit cmake

RDEPENDS_${PN}-dev = ""

BBCLASSEXTEND = "native"
