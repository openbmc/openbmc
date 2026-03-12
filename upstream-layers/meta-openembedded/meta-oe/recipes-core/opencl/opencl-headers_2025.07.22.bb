SUMMARY  = "OpenCL API Headers"
DESCRIPTION = "OpenCL compute API headers from Khronos Group"
LICENSE  = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=3b83ef96387f14655fc854ddc3c6bd57"
SECTION = "base"

SRCREV = "8a97ebc88daa3495d6f57ec10bb515224400186f"
SRC_URI = "git://github.com/KhronosGroup/OpenCL-Headers.git;branch=main;protocol=https \
           "

inherit cmake

EXTRA_OECMAKE = "-DBUILD_TESTING=OFF"

ALLOW_EMPTY:${PN} = "1"

BBCLASSEXTEND = "native nativesdk"
