LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=3b83ef96387f14655fc854ddc3c6bd57"

SRC_URI = "\
	git://github.com/KhronosGroup/VK-GL-CTS.git;protocol=https;tag=${BPN}-${PV};name=vk-gl-cts \
	git://github.com/google/amber;protocol=https;destsuffix=git/external/amber/src;name=amber;branch=main \
	git://github.com/KhronosGroup/glslang.git;protocol=https;destsuffix=git/external/glslang/src;name=glslang \
	git://github.com/KhronosGroup/SPIRV-Headers.git;protocol=https;destsuffix=git/external/spirv-headers/src;name=spirv-headers \
	git://github.com/KhronosGroup/SPIRV-Tools.git;protocol=https;destsuffix=git/external/spirv-tools/src;name=spirv-tools \
	https://raw.githubusercontent.com/baldurk/renderdoc/v1.1/renderdoc/api/app/renderdoc_app.h;subdir=git/external/renderdoc/src;name=renderdoc \
"

S = "${WORKDIR}/git"

SRCREV_FORMAT = "vk-gl-cts"

S = "${WORKDIR}/git"

inherit pkgconfig cmake

DEPENDS += "libpng zlib virtual/libgles2 virtual/egl"

SRC_URI_append_libc-musl = "\
	file://fix-musl.patch \
"
DEPENDS_append_libc-musl = " libexecinfo"

SRC_URI_append_toolchain-clang = "\
	file://fix-clang-private-operator.patch \
"

EXTRA_OECMAKE_append = " -DDEQP_TARGET=surfaceless"

CTSDIR = "/usr/lib/${BPN}"

FILES_${PN} += "${CTSDIR}"
