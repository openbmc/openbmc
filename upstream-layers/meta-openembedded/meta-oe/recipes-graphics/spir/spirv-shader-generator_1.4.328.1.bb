SUMMARY  = "SPIRV-Cross is a tool designed for parsing and converting SPIR-V \
to other shader languages"
LICENSE  = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=3b83ef96387f14655fc854ddc3c6bd57"
SECTION = "graphics"

SRCREV = "7affe74d77f93a622bb5002789d5332d32e512ee"
SRC_URI = "git://github.com/KhronosGroup/SPIRV-Cross.git;branch=main;protocol=https;tag=vulkan-sdk-${PV}"

inherit cmake pkgconfig

EXTRA_OECMAKE = "-DSPIRV_CROSS_SHARED=ON"

FILES:${PN} += "${datadir}"
