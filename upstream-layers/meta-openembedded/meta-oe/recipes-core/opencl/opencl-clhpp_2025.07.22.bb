SUMMARY  = "OpenCL API C++ bindings"
DESCRIPTION = "OpenCL API C++ bindings from Khronos"

LICENSE  = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE.txt;md5=3b83ef96387f14655fc854ddc3c6bd57"

DEPENDS = "opencl-headers"

SRCREV = "2a608428f725cad7903ef55e1ce5b995895838f5"

SRC_URI = "git://github.com/KhronosGroup/OpenCL-CLHPP.git;protocol=https;branch=main"


inherit cmake

EXTRA_OECMAKE = " \
                  -DBUILD_DOCS=OFF \
                  -DBUILD_EXAMPLES=OFF \
                  -DBUILD_TESTING=OFF \
                  "

# Headers only so PN is empty
RDEPENDS:${PN}-dev = ""

BBCLASSEXTEND += "native nativesdk"
