SUMMARY  = "OpenCL API Headers"
DESCRIPTION = "OpenCL compute API headers from Khronos Group"
LICENSE  = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=3b83ef96387f14655fc854ddc3c6bd57"
SECTION = "base"

SRCREV = "6fe718c31a45fe25151362a72ef041c3a1047cbd"
SRC_URI = "git://github.com/KhronosGroup/OpenCL-Headers.git;branch=main;protocol=https;tag=v${PV} \
           "

inherit cmake

EXTRA_OECMAKE = "-DBUILD_TESTING=OFF"

ALLOW_EMPTY:${PN} = "1"

BBCLASSEXTEND = "native nativesdk"
