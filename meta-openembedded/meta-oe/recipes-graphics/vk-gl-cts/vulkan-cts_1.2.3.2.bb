DESCRIPTION = "Vulkan CTS"

require khronos-cts.inc
# vulkan-cts-1.2.3.2
SRCREV_vk-gl-cts = "5cd2240b60825fbbf6bd9ddda6af176ee3100c70"
SRCREV_amber = "a40bef4dba98d2d80b48e5a940d8574fbfceb197"
SRCREV_glslang = "b5f003d7a3ece37db45578a8a3140b370036fc64"
SRCREV_spirv-headers = "f8bf11a0253a32375c32cad92c841237b96696c0"
SRCREV_spirv-tools = "d2b486219495594f2e5d0e8d457fc234a3460b3b"
SRC_URI[renderdoc.sha256sum] = "e7b5f0aa5b1b0eadc63a1c624c0ca7f5af133aa857d6a4271b0ef3d0bdb6868e"

S = "${WORKDIR}/git"

REQUIRED_DISTRO_FEATURES = "vulkan"
inherit features_check

DEPENDS += " vulkan-loader"

do_install() {
	install -d ${D}/${CTSDIR}
	cp -r ${B}/external/vulkancts/modules/vulkan/* ${D}/${CTSDIR}/
	rm -r ${D}/${CTSDIR}/*.a ${D}/${CTSDIR}/cmake_install.cmake ${D}/${CTSDIR}/CMakeFiles
	rm -r ${D}/${CTSDIR}/*/*.a ${D}/${CTSDIR}/*/cmake_install.cmake ${D}/${CTSDIR}/*/CMakeFiles
}
