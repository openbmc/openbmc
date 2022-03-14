SUMMARY  = "OpenCL API C++ bindings"
DESCRIPTION = "OpenCL API C++ bindings from Khronos"

SRC_URI = "git://github.com/KhronosGroup/OpenCL-CLHPP.git;protocol=https;branch=master"

LICENSE  = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE.txt;md5=3b83ef96387f14655fc854ddc3c6bd57"

PV = "2.0.15+git${SRCPV}"
SRCREV = "f7237f3799009d856935e1eecfd7c9301fe522b4"

S = "${WORKDIR}/git"

inherit cmake

EXTRA_OECMAKE = " \
                  -DBUILD_DOCS=OFF \
                  -DBUILD_EXAMPLES=OFF \
                  -DBUILD_TESTS=OFF \
                  "

# Headers only so PN is empty
RDEPENDS:${PN}-dev = ""
