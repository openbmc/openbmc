SUMMARY  = "SPIRV-Cross is a tool designed for parsing and converting SPIR-V \
to other shader languages"
LICENSE  = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=3b83ef96387f14655fc854ddc3c6bd57"
SECTION = "graphics"

S = "${WORKDIR}/git"
SRCREV = "ed16b3e69985feaf565efbecea70a1cc2fca2a58"
SRC_URI = "git://github.com/KhronosGroup/SPIRV-Cross.git \
	file://0001-Add-install-PHONY-target-in-Makefile.patch \
"

EXTRA_OEMAKE += 'TARGET_DIR_LIB="${D}${libdir}"' 
EXTRA_OEMAKE += 'TARGET_DIR_BIN="${D}${bindir}"'

do_compile () {
	cd ${S} && oe_runmake
}

do_install () {
	cd ${S} && oe_runmake install
}
