SUMMARY  = "SPIRV-Cross is a tool designed for parsing and converting SPIR-V \
to other shader languages"
LICENSE  = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=3b83ef96387f14655fc854ddc3c6bd57"
SECTION = "graphics"

S = "${WORKDIR}/git"
SRCREV = "f09ba2777714871bddb70d049878af34b94fa54d"
SRC_URI = "git://github.com/KhronosGroup/SPIRV-Cross.git;branch=master;protocol=https"
inherit cmake pkgconfig

EXTRA_OECMAKE = "-DSPIRV_CROSS_SHARED=ON"

FILES:${PN} += "${datadir}"
