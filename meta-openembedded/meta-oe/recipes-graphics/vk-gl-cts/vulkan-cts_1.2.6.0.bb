DESCRIPTION = "Vulkan CTS"

require khronos-cts.inc
# vulkan-cts-1.2.6.0
SRCREV_vk-gl-cts = "2cab49df5ad25a2d0061152367a21c6da83ed097"
SRCREV_amber = "dabae26164714abf951c6815a2b4513260f7c6a4"
SRCREV_glslang = "5c4f421121c4d24aad23a507e630dc5dc6c92c7c"
SRCREV_spirv-headers = "faa570afbc91ac73d594d787486bcf8f2df1ace0"
SRCREV_spirv-tools = "f11f7434815838bbad349124767b258ce7df41f0"
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
