SUMMARY  = "The SPIR-V Tools project provides an API and commands for \
processing SPIR-V modules"
DESCRIPTION = "The project includes an assembler, binary module parser, \
disassembler, validator, and optimizer for SPIR-V."
LICENSE  = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=3b83ef96387f14655fc854ddc3c6bd57"
SECTION = "graphics"

S = "${WORKDIR}/git"
DEST_DIR = "${S}/external" 
SRC_URI = "git://github.com/KhronosGroup/SPIRV-Tools.git;name=spirv-tools \
	file://0001-tools-lesspipe-Allow-generic-shell.patch \
	git://github.com/KhronosGroup/SPIRV-Headers.git;name=spirv-headers;destsuffix=${DEST_DIR}/spirv-headers \
	git://github.com/google/effcee.git;name=effcee;destsuffix=${DEST_DIR}/effcee \
	git://github.com/google/re2.git;name=re2;destsuffix=${DEST_DIR}/re2 \
	git://github.com/google/googletest.git;name=googletest;destsuffix=${DEST_DIR}/googletest \
"
SRCREV_spirv-tools = "167f1270a9ee641b17c016a545741e4aadfabe86"
SRCREV_spirv-headers = "4618b86e9e4b027a22040732dfee35e399cd2c47"
SRCREV_effcee = "8f0a61dc95e0df18c18e0ac56d83b3fa9d2fe90b"
SRCREV_re2 = "2cf86e5ab6dcfe045a1f510c2b9a8b012a4158cd"
SRCREV_googletest = "150613166524c474a8a97df4c01d46b72050c495"

inherit cmake python3native

do_install_append() {
	install -d ${D}/${includedir}/spirv
	install -m 0644 ${DEST_DIR}/spirv-headers/include/spirv/1.2/* ${D}/${includedir}/spirv	
}

FILES_SOLIBSDEV = ""
FILES_${PN} += "${libdir}/*.so"
