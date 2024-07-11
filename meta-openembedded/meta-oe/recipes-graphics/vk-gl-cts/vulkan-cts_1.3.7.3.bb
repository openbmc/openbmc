DESCRIPTION = "Vulkan CTS"

require khronos-cts.inc

SRC_URI += "git://github.com/Igalia/vk_video_samples.git;protocol=https;destsuffix=git/external/nvidia-video-samples/src;name=video-parser;nobranch=1"

# vulkan-cts-1.3.7.3
SRCREV_vk-gl-cts = "d71a36db16d98313c431829432a136dbda692a08"
SRCREV_amber = "933ecb4d6288675a92eb1650e0f52b1d7afe8273"
SRCREV_glslang = "c5117b328afc86e16edff6ed6afe0fe7872a7cf3"
SRCREV_spirv-headers = "b8b9eb8640c8c0107ba580fbcb10f969022ca32c"
SRCREV_spirv-tools = "bfc94f63a7adbcf8ae166f5f108ac9f69079efc0"
SRCREV_jsoncpp = "9059f5cad030ba11d37818847443a53918c327b1"
SRCREV_vulkan-docs = "b9aad705f0d9e5e6734ac2ad671d5d1de57b05e0"
SRC_URI[renderdoc.sha256sum] = "e7b5f0aa5b1b0eadc63a1c624c0ca7f5af133aa857d6a4271b0ef3d0bdb6868e"
# Not yet needed
SRCREV_ESExtractor = "75ffcaf55bb069f7a23764194742d2fb78c7f71f"
SRCREV_video-parser = "138bbe048221d315962ddf8413aa6a08cc62a381"

SRC_URI += "file://0001-cmake-Define-WAYLAND_SCANNER-and-WAYLAND_PROTOCOLS_D.patch \
            file://0001-vulkan-cts-include-missing-cstdint.patch \
"

TOOLCHAIN = "gcc"

# Workaround an optimization bug that breaks createMeshShaderMiscTestsEXT
OECMAKE_CXX_FLAGS:remove:toolchain-gcc = "-O2"

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
