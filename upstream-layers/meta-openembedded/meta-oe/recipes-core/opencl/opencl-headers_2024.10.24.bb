SUMMARY  = "OpenCL API Headers"
DESCRIPTION = "OpenCL compute API headers from Khronos Group"
LICENSE  = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=3b83ef96387f14655fc854ddc3c6bd57"
SECTION = "base"

SRCREV = "4ea6df132107e3b4b9407f903204b5522fdffcd6"
SRC_URI = "git://github.com/KhronosGroup/OpenCL-Headers.git;branch=main;protocol=https \
           file://0001-Command-buffer-supported-queue-properties-265.patch \
           "

inherit cmake

EXTRA_OECMAKE = "-DBUILD_TESTING=OFF"

ALLOW_EMPTY:${PN} = "1"

BBCLASSEXTEND = "native nativesdk"
