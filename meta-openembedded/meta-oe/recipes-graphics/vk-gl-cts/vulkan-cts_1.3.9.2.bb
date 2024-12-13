DESCRIPTION = "Vulkan CTS"

require khronos-cts.inc

# vulkan-cts-1.3.9.2
SRCREV_vk-gl-cts = "24c1b1498ba4f05777f47541968ffe686265c645"
SRCREV_amber = "0f003c2785489f59cd01bb2440fcf303149100f2"
SRCREV_glslang = "2b19bf7e1bc0b60cf2fe9d33e5ba6b37dfc1cc83"
SRCREV_spirv-headers = "db5a00f8cebe81146cafabf89019674a3c4bf03d"
SRCREV_spirv-tools = "4c7e1fa5c3d988cca0e626d359d30b117b9c2822"
SRCREV_jsoncpp = "9059f5cad030ba11d37818847443a53918c327b1"
SRCREV_vulkan-docs = "7bb606eb87cde1d34f65f36f4d4c6f2c78f072c8"
SRCREV_vulkan-validationlayers = "a92629196a4fed15e59c74aa965dd47bd5ece3b7"
SRC_URI[renderdoc.sha256sum] = "e7b5f0aa5b1b0eadc63a1c624c0ca7f5af133aa857d6a4271b0ef3d0bdb6868e"
# Not yet needed
SRCREV_ESExtractor = "75ffcaf55bb069f7a23764194742d2fb78c7f71f"
SRCREV_video-parser = "6821adf11eb4f84a2168264b954c170d03237699"

# Workaround an optimization bug that breaks createMeshShaderMiscTestsEXT
OECMAKE_CXX_FLAGS:remove:toolchain-gcc = "-O2"

REQUIRED_DISTRO_FEATURES = "vulkan"
inherit features_check

DEPENDS += " vulkan-loader"

do_install() {
	install -d ${D}/${CTSDIR}
	cp -r ${B}/external/vulkancts/modules/vulkan/* ${D}/${CTSDIR}/
	rm -rf ${D}/${CTSDIR}/*.a ${D}/${CTSDIR}/cmake_install.cmake ${D}/${CTSDIR}/CMakeFiles
	rm -rf ${D}/${CTSDIR}/*/*.a ${D}/${CTSDIR}/*/cmake_install.cmake ${D}/${CTSDIR}/*/CMakeFiles
}
