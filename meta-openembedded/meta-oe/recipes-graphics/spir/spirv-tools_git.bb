SUMMARY  = "The SPIR-V Tools project provides an API and commands for \
processing SPIR-V modules"
DESCRIPTION = "The project includes an assembler, binary module parser, \
disassembler, validator, and optimizer for SPIR-V."
LICENSE  = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=3b83ef96387f14655fc854ddc3c6bd57"
SECTION = "graphics"

S = "${WORKDIR}/git"
DEST_DIR = "${S}/external" 
SRC_URI = "git://github.com/KhronosGroup/SPIRV-Tools.git;name=spirv-tools;branch=master;protocol=https \
	git://github.com/KhronosGroup/SPIRV-Headers.git;name=spirv-headers;destsuffix=${DEST_DIR}/spirv-headers;branch=master;protocol=https \
	git://github.com/google/effcee.git;name=effcee;destsuffix=${DEST_DIR}/effcee;branch=master;protocol=https \
	git://github.com/google/re2.git;name=re2;destsuffix=${DEST_DIR}/re2;branch=master;protocol=https \
	git://github.com/google/googletest.git;name=googletest;destsuffix=${DEST_DIR}/googletest;branch=master;protocol=https \
        file://0001-Respect-CMAKE_INSTALL_LIBDIR-in-installed-CMake-file.patch \
        file://0001-Avoid-pessimizing-std-move-3124.patch \
"
SRCREV_spirv-tools = "c413b982c316b14e784f50d941814fc737b55b4a"
SRCREV_spirv-headers = "af64a9e826bf5bb5fcd2434dd71be1e41e922563"
SRCREV_effcee = "cd25ec17e9382f99a895b9ef53ff3c277464d07d"
SRCREV_re2 = "5bd613749fd530b576b890283bfb6bc6ea6246cb"
SRCREV_googletest = "f2fb48c3b3d79a75a88a99fba6576b25d42ec528"
SRCREV_FORMAT = "spirv-ttols_spirv-headers_effcee_re2_googletest"

inherit cmake python3native

EXTRA_OECMAKE += "-DSPIRV_WERROR=OFF"

do_install_append() {
	install -d ${D}/${includedir}/spirv
	install -m 0644 ${DEST_DIR}/spirv-headers/include/spirv/1.2/* ${D}/${includedir}/spirv	
}

FILES_SOLIBSDEV = ""
FILES_${PN} += "${libdir}/*.so"
