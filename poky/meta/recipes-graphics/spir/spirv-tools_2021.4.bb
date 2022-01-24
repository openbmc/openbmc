SUMMARY  = "The SPIR-V Tools project provides an API and commands for \
processing SPIR-V modules"
DESCRIPTION = "The project includes an assembler, binary module parser, \
disassembler, validator, and optimizer for SPIR-V."
HOMEPAGE = "https://github.com/KhronosGroup/SPIRV-Tools"
SECTION = "graphics"
LICENSE  = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=3b83ef96387f14655fc854ddc3c6bd57"

SRCREV = "ab8eb607750208066e2d57eff6a34dbaf05f5ada"
SRC_URI = "git://github.com/KhronosGroup/SPIRV-Tools.git;branch=master;protocol=https"
UPSTREAM_CHECK_GITTAGREGEX = "^v(?P<pver>\d+(\.\d+)+)$"
S = "${WORKDIR}/git"

inherit cmake python3native

DEPENDS = "spirv-headers"

EXTRA_OECMAKE += "\
    -DSPIRV-Headers_SOURCE_DIR=${STAGING_EXECPREFIXDIR} \
    -DSPIRV_TOOLS_BUILD_STATIC=OFF \
    -DBUILD_SHARED_LIBS=ON \
    -DSPIRV_SKIP_TESTS=ON \
"

do_install:append:class-target() {
    # reproducibility: remove build host path
    sed -i ${D}${libdir}/cmake/SPIRV-Tools/SPIRV-ToolsTarget.cmake \
        -e 's:${STAGING_DIR_HOST}::g'
}

# all the libraries are unversioned, so don't pack it on PN-dev
SOLIBS = ".so"
FILES_SOLIBSDEV = ""

PACKAGES =+ "${PN}-lesspipe"
FILES:${PN}-lesspipe = "${base_bindir}/spirv-lesspipe.sh"
RDEPENDS:${PN}-lesspipe += "${PN} bash"

BBCLASSEXTEND = "native nativesdk"
