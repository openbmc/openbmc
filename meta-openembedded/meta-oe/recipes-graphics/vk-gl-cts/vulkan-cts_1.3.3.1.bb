DESCRIPTION = "Vulkan CTS"

require khronos-cts.inc
# vulkan-cts-1.3.3.1
SRCREV_vk-gl-cts = "6024a88390942876147a88dce82bbed73b866c1b"
SRCREV_amber = "8b145a6c89dcdb4ec28173339dd176fb7b6f43ed"
SRCREV_glslang = "7dda6a6347b0bd550e202942adee475956ef462a"
SRCREV_spirv-headers = "b765c355f488837ca4c77980ba69484f3ff277f5"
SRCREV_spirv-tools = "b930e734ea198b7aabbbf04ee1562cf6f57962f0"
SRCREV_jsoncpp = "9059f5cad030ba11d37818847443a53918c327b1"
SRCREV_vulkan-docs = "9b5562187a8ad72c171410b036ceedbc450153ba"
SRC_URI[renderdoc.sha256sum] = "e7b5f0aa5b1b0eadc63a1c624c0ca7f5af133aa857d6a4271b0ef3d0bdb6868e"
# Not yet needed
SRCREV_ESExtractor = "ce5d7ebcf0ebb0d78385ee4cc34653eb6764bfc4"
SRCREV_video-parser = "7d68747d3524842afaf050c5e00a10f5b8c07904"

SRC_URI += "file://0001-cmake-Define-WAYLAND_SCANNER-and-WAYLAND_PROTOCOLS_D.patch \
            file://0001-vulkan-cts-include-missing-cstdint.patch \
            file://0001-include-missing-cstdint.patch;patchdir=external/amber/src \
"

S = "${WORKDIR}/git"

REQUIRED_DISTRO_FEATURES = "vulkan"
inherit features_check

DEPENDS += " vulkan-loader"

do_install() {
	install -d ${D}/${CTSDIR}
	cp -r ${B}/external/vulkancts/modules/vulkan/* ${D}/${CTSDIR}/
	rm -rf ${D}/${CTSDIR}/*.a ${D}/${CTSDIR}/cmake_install.cmake ${D}/${CTSDIR}/CMakeFiles
	rm -rf ${D}/${CTSDIR}/*/*.a ${D}/${CTSDIR}/*/cmake_install.cmake ${D}/${CTSDIR}/*/CMakeFiles
}
