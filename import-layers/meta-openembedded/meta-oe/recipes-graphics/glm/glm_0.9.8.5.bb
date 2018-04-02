SUMMARY = "OpenGL Mathematics Library"
DESCRIPTION = "OpenGL Mathematics (GLM) is a header only C++ \
mathematics library for graphics software based on the OpenGL \
Shading Language (GLSL) specifications."
HOMEPAGE = "https://glm.g-truc.net"
BUGTRACKER = "https://github.com/g-truc/glm/issues"

SECTION = "libs"

LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://copying.txt;md5=4431606d144252143c9c3df384a74cad"

SRC_URI = "git://github.com/g-truc/glm;branch=0.9.8"
SRCREV = "6fa203eeb7fbcbb6f620501fad40359c8a456049"
S = "${WORKDIR}/git"

inherit cmake

FILES_${PN}-dev += "${libdir}/cmake"
RDEPENDS_${PN}-dev = ""

BBCLASSEXTEND = "native"
