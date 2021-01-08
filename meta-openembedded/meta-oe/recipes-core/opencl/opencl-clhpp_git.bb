SUMMARY  = "OpenCL API C++ bindings"
DESCRIPTION = "OpenCL API C++ bindings from Khronos"

SRC_URI = "git://github.com/KhronosGroup/OpenCL-CLHPP.git;protocol=https"

LICENSE  = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE.txt;md5=3b83ef96387f14655fc854ddc3c6bd57"

PV = "2.0.13+git${SRCPV}"
SRCREV = "b230a96b61e9e35b8da1565893d74380646e03c8"

S = "${WORKDIR}/git"

inherit cmake

EXTRA_OECMAKE = " \
                  -DBUILD_DOCS=OFF \
                  -DBUILD_EXAMPLES=OFF \
                  -DBUILD_TESTS=OFF \
                  "

# Headers only so PN is empty
RDEPENDS_${PN}-dev = ""
