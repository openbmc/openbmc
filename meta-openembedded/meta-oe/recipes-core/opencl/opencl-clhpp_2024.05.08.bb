SUMMARY  = "OpenCL API C++ bindings"
DESCRIPTION = "OpenCL API C++ bindings from Khronos"

LICENSE  = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE.txt;md5=3b83ef96387f14655fc854ddc3c6bd57"

DEPENDS = "opencl-headers"

SRCREV = "0bdbbfe5ecda42cff50c96cc5e33527f42fcbd45"

SRC_URI = "git://github.com/KhronosGroup/OpenCL-CLHPP.git;protocol=https;branch=main"

S = "${WORKDIR}/git"

inherit cmake

EXTRA_OECMAKE = " \
                  -DBUILD_DOCS=OFF \
                  -DBUILD_EXAMPLES=OFF \
                  -DBUILD_TESTING=OFF \
                  "

# Headers only so PN is empty
RDEPENDS:${PN}-dev = ""

BBCLASSEXTEND += "native nativesdk"
